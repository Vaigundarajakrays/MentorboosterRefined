package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.OtpException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.model.Otp;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.repository.OtpRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.security.JwtService;
import com.mentorboosters.app.util.CommonFiles;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CommonFiles commonFiles;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final MenteeProfileRepository menteeProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;

    public CommonResponse<LoginResponse> authenticate(LoginRequest request) throws UnexpectedServerException, ResourceNotFoundException {

        Users user = usersRepository.findByEmailId(request.getEmailId());

        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_EMAIL + request.getEmailId());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword())
            );

            String token = jwtService.generateToken(user);

            MentorProfileDTO mentorProfileDTO = null;
            MenteeProfileDTO menteeProfileDTO = null;

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            if (user.getRole() == Role.USER) {
                MenteeProfile mentee = menteeProfileRepository.findByEmail(user.getEmailId())
                        .orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_EMAIL + user.getEmailId()));

                menteeProfileDTO = MenteeProfileDTO.builder()
                        .menteeId(mentee.getId())
                        .name(mentee.getName())
                        .email(mentee.getEmail())
                        .phone(mentee.getPhone())
                        .description(mentee.getDescription())
                        .languages(mentee.getLanguages())
                        .timezone(mentee.getTimeZone())
                        .subscriptionPlan(mentee.getSubscriptionPlan())
                        .customerId(mentee.getId())
                        .joinDate(mentee.getCreatedAt().atZone(ZoneId.of(mentee.getTimeZone())).format(formatter))
                        .industry(mentee.getIndustry())
                        .location(mentee.getLocation())
                        .goals(mentee.getGoals())
                        .status(mentee.getStatus())
                        .build();
            }

            if (user.getRole() == Role.MENTOR) {
                MentorProfile mentor = mentorProfileRepository.findByEmail(user.getEmailId())
                        .orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_EMAIL + user.getEmailId()));

                mentorProfileDTO = MentorProfileDTO.builder()
                        .mentorId(mentor.getId())
                        .name(mentor.getName())
                        .email(mentor.getEmail())
                        .phone(mentor.getPhone())
                        .linkedinUrl(mentor.getLinkedinUrl())
                        .profileUrl(mentor.getProfileUrl())
                        .resumeUrl(mentor.getResumeUrl())
                        .yearsOfExperience(mentor.getYearsOfExperience())
                        .categories(mentor.getCategories())
                        .summary(mentor.getSummary())
                        .amount(mentor.getAmount())
                        .terms(mentor.getTerms())
                        .termsAndConditions(mentor.getTermsAndConditions())
                        .timezone(mentor.getTimezone())
                        .accountStatus(mentor.getAccountStatus())
                        .build();
            }

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .role(user.getRole())
                    .menteeProfile(menteeProfileDTO)
                    .mentorProfile(mentorProfileDTO)
                    .build();

            return CommonResponse.<LoginResponse>builder()
                    .message(LOGIN_SUCCESS)
                    .status(STATUS_TRUE)
                    .data(loginResponse)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_LOGGING_IN + e.getMessage());
        }
    }


    public CommonResponse<String> sendOtp(String email) throws UnexpectedServerException {

        try {

            String otp = commonFiles.generateOTP(6);
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

            Otp otpEntity = otpRepository.findByEmail(email)
                    .map(existingOtp -> {
                        existingOtp.setOtp(otp);
                        existingOtp.setExpiryTime(expiryTime);
                        return existingOtp;
                    })
                    .orElse(new Otp(email, otp, expiryTime));

            otpRepository.save(otpEntity);

            commonFiles.sendOTPUser(email, otp);

            return CommonResponse.<String>builder()
                    .message(OTP_SENT_SUCCESS)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (MailAuthenticationException | MailSendException e){
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_SENDING_OTP + e.getMessage());
        }
    }

    public CommonResponse<String> verifyOtp(String email, String otp) throws UnexpectedServerException {

        try {

            Otp otpEntity = otpRepository.findByEmailAndOtp(email, otp)
                    .orElseThrow(() -> new OtpException(INVALID_OTP, IN_VALID_OTP));

            if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new OtpException(OTP_EXPIRED, OTP_EXPIRE);
            }

            return CommonResponse.<String>builder()
                    .status(true)
                    .message(OTP_VERIFIED_SUCCESSFULLY)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (OtpException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_VERIFYING_OTP + e.getMessage());
        }

    }

    public CommonResponse<String> changePassword(ChangePasswordRequest changePasswordRequest) throws ResourceNotFoundException, UnexpectedServerException {

        Users user = usersRepository.findByEmailId(changePasswordRequest.getEmail());

        if(user == null){
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_EMAIL + changePasswordRequest.getEmail());
        }

        try {

            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            usersRepository.save(user);

            return CommonResponse.<String>builder()
                    .message(PASSWORD_CHANGE_SUCCESS)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .data(PASSWORD_UPDATED)
                    .build();

        }

        catch (Exception e){
            throw new UnexpectedServerException(ERROR_UPDATING_NEW_PASSWORD + e.getMessage());
        }
    }


}
