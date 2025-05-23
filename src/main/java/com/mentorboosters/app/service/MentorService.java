package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.mapper.DtoMapper;
import com.mentorboosters.app.model.*;
import com.mentorboosters.app.repository.*;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.CommonFiles;
import com.mentorboosters.app.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class MentorService {

    private final MentorRepository mentorRepository;
    private final CategoryRepository categoryRepository;
    private final CertificateRepository certificateRepository;
    private final ExperienceRepository experienceRepository;
    private final FixedTimeSlotRepository fixedTimeSlotRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonFiles commonFiles;
    private final UsersRepository usersRepository;

    @Autowired
    public MentorService(MentorRepository mentorRepository, CategoryRepository categoryRepository, CertificateRepository certificateRepository, ExperienceRepository experienceRepository, FixedTimeSlotRepository fixedTimeSlotRepository, PasswordEncoder passwordEncoder, CommonFiles commonFiles, UsersRepository usersRepository){
        this.mentorRepository=mentorRepository;
        this.categoryRepository=categoryRepository;
        this.certificateRepository=certificateRepository;
        this.experienceRepository=experienceRepository;
        this.fixedTimeSlotRepository=fixedTimeSlotRepository;
        this.passwordEncoder=passwordEncoder;
        this.commonFiles=commonFiles;
        this.usersRepository=usersRepository;
    }

    public CommonResponse<List<Mentor>> findAllMentorsWithSlots() throws UnexpectedServerException {

        try {
            List<Mentor> mentors = mentorRepository.findAll();

            if (mentors.isEmpty()) {
                return CommonResponse.<List<Mentor>>builder()
                        .status(STATUS_FALSE)
                        .message(NO_MENTORS_AVAILABLE)
                        .data(mentors)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<Mentor>>builder()
                    .status(STATUS_TRUE)
                    .message(LOADED_ALL_MENTOR_DETAILS)
                    .data(mentors)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_FETCHING_MENTORS + e.getMessage());
        }
    }

    //Transactional is not used in get method but in save method because after saving certificate if experience table failed, then it will be like some data saved some not. to prevent that.
    @Transactional
    public CommonResponse<Mentor> saveMentor(Mentor mentor) throws UnexpectedServerException {

        if (mentorRepository.existsByEmail(mentor.getEmail())) {
            return CommonResponse.<Mentor>builder()
                    .message(EMAIL_ALREADY_EXISTS)
                    .status(STATUS_FALSE)
                    .statusCode(FORBIDDEN_CODE)
                    .build();
        }

        try {

//            Mentor mentor = DtoMapper.toMentorEntity(mentorDTO);

            List<Category> updatedCategories = mentor.getCategories().stream().map(category -> categoryRepository.findByName(category.getName()).orElse(category)).toList();

            // if a category already exists, it has id in the updatedCategory, if its new it doesnt have id. Hibernate if sees id it wont insert it again.
            mentor.setCategories(updatedCategories);

            // If we dont give this, Hibernate doesn’t know which mentor owns this certificate, exp, timeslot.
            mentor.getCertificates().forEach(certificate -> certificate.setMentor(mentor));
            mentor.getExperiences().forEach(experience -> experience.setMentor(mentor));
            mentor.getTimeSlots().forEach(fixedTimeSlot -> fixedTimeSlot.setMentor(mentor));

            Mentor savedMentor = mentorRepository.save(mentor);

            String password = commonFiles.generateAlphaPassword(6);
            String hashedPassword = passwordEncoder.encode(password);

            Users user = Users.builder()
                    .name(mentor.getName())
                    .emailId(mentor.getEmail())
                    .userName(commonFiles.generateAlphaPassword(6))
                    .role(Role.MENTOR)
                    .password(hashedPassword)
                    .build();

            Users savedUser = usersRepository.save(user);

            //Send email to mentor
            commonFiles.sendPasswordToMentor(mentor, password);

            return CommonResponse.<Mentor>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(savedMentor)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_ADDING_MENTORS + e.getMessage());
        }

    }

    // WANNA ADD REVIEW UPDATE TOO
    @Transactional
    public CommonResponse<Mentor> updateMentor(Long id, Mentor updatedMentor) throws ResourceNotFoundException, UnexpectedServerException {

        Mentor existingMentor = mentorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_WITH_ID + id));

        try {

            existingMentor.setName(updatedMentor.getName());
            existingMentor.setEmail(updatedMentor.getEmail());
            existingMentor.setGender(updatedMentor.getGender());
            existingMentor.setAvatarUrl(updatedMentor.getAvatarUrl());
            existingMentor.setBio(updatedMentor.getBio());
            existingMentor.setRole(updatedMentor.getRole());
            existingMentor.setFreePrice(updatedMentor.getFreePrice());
            existingMentor.setFreeUnit(updatedMentor.getFreeUnit());
            existingMentor.setVerified(updatedMentor.getVerified());
            existingMentor.setRate(updatedMentor.getRate());
            existingMentor.setNumberOfMentoree(updatedMentor.getNumberOfMentoree());

            existingMentor.getCertificates().clear();
            for (Certificate cert : updatedMentor.getCertificates()) {
                if (cert.getId() != null) {
                    Certificate existingCert = certificateRepository.findById(cert.getId())
                            .orElseThrow(() -> new ResourceNotFoundException(CERTIFICATE_NOT_FOUND_WITH_ID + cert.getId()));

                    existingCert.setName(cert.getName());
                    existingCert.setProvideBy(cert.getProvideBy());
                    existingCert.setCreateDate(cert.getCreateDate());
                    existingCert.setImageUrl(cert.getImageUrl());
                    existingCert.setMentor(existingMentor);

                    existingMentor.getCertificates().add(existingCert);
                } else {
                    cert.setMentor(existingMentor);
                    existingMentor.getCertificates().add(cert);
                }
            }


            existingMentor.getExperiences().clear();
            for (Experience exp : updatedMentor.getExperiences()) {
                if (exp.getId() != null) {
                    Experience existingExp = experienceRepository.findById(exp.getId())
                            .orElseThrow(() -> new ResourceNotFoundException(EXPERIENCE_NOT_FOUND_WITH_ID + exp.getId()));

                    existingExp.setRole(exp.getRole());
                    existingExp.setCompanyName(exp.getCompanyName());
                    existingExp.setStartDate(exp.getStartDate());
                    existingExp.setEndDate(exp.getEndDate());
                    existingExp.setDescription(exp.getDescription());
                    existingExp.setMentor(existingMentor);

                    existingMentor.getExperiences().add(existingExp);
                } else {
                    exp.setMentor(existingMentor);
                    existingMentor.getExperiences().add(exp);
                }
            }

            existingMentor.getTimeSlots().clear();
            for (FixedTimeSlot slot : updatedMentor.getTimeSlots()) {
                if (slot.getId() != null) {
                    FixedTimeSlot existingSlot = fixedTimeSlotRepository.findById(slot.getId())
                            .orElseThrow(() -> new ResourceNotFoundException(TIMESLOT_NOT_FOUND_WITH_ID + slot.getId()));

                    existingSlot.setTimeStart(slot.getTimeStart());
                    existingSlot.setTimeEnd(slot.getTimeEnd());
                    existingSlot.setMentor(existingMentor);

                    existingMentor.getTimeSlots().add(existingSlot);
                } else {
                    slot.setMentor(existingMentor);
                    existingMentor.getTimeSlots().add(slot);
                }
            }


            List<Category> updatedCategories = new ArrayList<>();
            for (Category incomingCat : updatedMentor.getCategories()) {
                Category category = categoryRepository.findByName(incomingCat.getName())
                        .orElseGet(() -> categoryRepository.save(incomingCat));
                updatedCategories.add(category);
            }
            existingMentor.setCategories(updatedCategories);

            Mentor updated = mentorRepository.save(existingMentor);

            return CommonResponse.<Mentor>builder()
                    .message(MENTOR_UPDATED_SUCCESS)
                    .status(STATUS_TRUE)
                    .data(updated)
                    .statusCode(SUCCESS_CODE)
                    .build();
        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_UPDATING_MENTORS + e.getMessage());
        }

    }

    @Transactional
    public CommonResponse<Mentor> deleteMentor(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_WITH_ID + id));

        try {
            // Remove associations from mentor_categories
            mentor.getCategories().clear();
            mentorRepository.save(mentor);

            usersRepository.deleteByEmailId(mentor.getEmail());

            // Now delete the mentor
            mentorRepository.deleteById(id);

            return CommonResponse.<Mentor>builder()
                    .message(MENTOR_DELETED_SUCCESSFULLY)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_DELETING_MENTORS + e.getMessage());
        }
    }

    public CommonResponse<List<VerifiedMentorDTO>> getVerifiedMentors() throws UnexpectedServerException {

        try {

            List<VerifiedMentorDTO> verifiedMentorDTOS = mentorRepository.findVerifiedMentors();

            if (verifiedMentorDTOS.isEmpty()) {
                return CommonResponse.<List<VerifiedMentorDTO>>builder()
                        .status(STATUS_FALSE)
                        .message(NO_VERIFIED_MENTORS_AVAILABLE)
                        .data(verifiedMentorDTOS)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<VerifiedMentorDTO>>builder()
                    .message(LOADED_ALL_VERIFIED_MENTOR_DETAILS)
                    .status(STATUS_TRUE)
                    .data(verifiedMentorDTOS)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {

            throw new UnexpectedServerException(ERROR_FETCHING_VERIFIED_MENTORS + e.getMessage());
        }
    }

    public CommonResponse<List<TopRatedMentorsDTO>> getTopRatedMentors() throws UnexpectedServerException {

        try {

            List<TopRatedMentorsDTO> topRatedMentorsDTOS = mentorRepository.findTopRatedMentors();

            if (topRatedMentorsDTOS.isEmpty()) {
                return CommonResponse.<List<TopRatedMentorsDTO>>builder()
                        .status(STATUS_FALSE)
                        .message(NO_TOP_RATED_MENTORS_AVAILABLE)
                        .data(topRatedMentorsDTOS)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<TopRatedMentorsDTO>>builder()
                    .message(LOADED_ALL_TOP_RATED_MENTORS_DETAILS)
                    .status(STATUS_TRUE)
                    .data(topRatedMentorsDTOS)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {

            throw new UnexpectedServerException(ERROR_FETCHING_TOP_RATED_MENTORS + e.getMessage());
        }
    }

    public CommonResponse<List<TopMentorsDTO>> getTopMentors() throws UnexpectedServerException {

        try {

            List<TopMentorsDTO> topMentorsDTOS = mentorRepository.findTopMentors();

            if (topMentorsDTOS.isEmpty()) {
                return CommonResponse.<List<TopMentorsDTO>>builder()
                        .status(STATUS_FALSE)
                        .message(NO_TOP_MENTORS_AVAILABLE)
                        .data(topMentorsDTOS)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<TopMentorsDTO>>builder()
                    .message(LOADED_ALL_TOP_MENTORS_DETAILS)
                    .status(STATUS_TRUE)
                    .data(topMentorsDTOS)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {

            throw new UnexpectedServerException(ERROR_FETCHING_TOP_MENTORS + e.getMessage());
        }
    }

    public CommonResponse<List<SearchMentorsDTO>> searchMentors(String keyword) throws UnexpectedServerException {

        if(keyword==null || keyword.trim().isEmpty()){
            return CommonResponse.<List<SearchMentorsDTO>>builder()
                    .status(STATUS_FALSE)
                    .message(NO_MENTORS_AVAILABLE)
                    .data(List.of())
                    .statusCode(SUCCESS_CODE)
                    .build();
        }

        try {

            String[] items = Arrays.stream(keyword.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);

            var results = mentorRepository.searchMentors(items);

            if (results.isEmpty()) {
                return CommonResponse.<List<SearchMentorsDTO>>builder()
                        .status(STATUS_FALSE)
                        .message(NO_MENTORS_AVAILABLE)
                        .data(results)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<SearchMentorsDTO>>builder()
                    .message(LOADED_ALL_MENTOR_DETAILS)
                    .status(STATUS_TRUE)
                    .data(results)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_FETCHING_MENTORS + e.getMessage());
        }
    }





}
