package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.enumUtil.OtpPurpose;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.OtpException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.*;
import com.mentorboosters.app.repository.*;
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
    private final SubscribeRepository subscribeRepository;
    private final EmailService emailService;

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

            String name = null;
            Long id = null;
            String timezone = null;
            String profileUrl = null;
            boolean isSubscribed = false;

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            if (user.getRole() == Role.USER) {
                MenteeProfile mentee = menteeProfileRepository.findByEmail(user.getEmailId())
                        .orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_EMAIL + user.getEmailId()));

                Subscribe subscribe = subscribeRepository.findByEmail(mentee.getEmail());
                if(!(subscribe ==null)){
                    isSubscribed=true;
                }

                name = mentee.getName();
                id = mentee.getId();
                timezone = mentee.getTimeZone();
                profileUrl = mentee.getProfileUrl();
            }

            if (user.getRole() == Role.MENTOR) {
                MentorProfile mentor = mentorProfileRepository.findByEmail(user.getEmailId())
                        .orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_EMAIL + user.getEmailId()));

                Subscribe subscribe = subscribeRepository.findByEmail(mentor.getEmail());
                if(!(subscribe ==null)){
                    isSubscribed=true;
                }

                name = mentor.getName();
                id = mentor.getId();
                timezone = mentor.getTimezone();
                profileUrl = mentor.getProfileUrl();
            }

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .role(user.getRole())
                    .name(name)
                    .id(id)
                    .timezone(timezone)
                    .profileUrl(profileUrl)
                    .isSubscribed(isSubscribed)
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


    public CommonResponse<String> sendOtp(String email, OtpPurpose otpPurpose) throws UnexpectedServerException {
        try {
            if (otpPurpose.isMentorRegister()) {
                if (mentorProfileRepository.existsByEmail(email)) {
                    throw new OtpException("You are already registered as a mentor", "MENTOR_EXISTS");
                }

                if (menteeProfileRepository.existsByEmail(email)) {
                    throw new OtpException("You are already registered as a mentee with this account. Please proceed with another account.", "MENTEE_EXISTS");
                }

                if (usersRepository.existsByEmailId(email)) {
                    throw new OtpException("Your email already exists in our system", "USER_EXISTS");
                }
            }

            if (otpPurpose.isMenteeRegister()) {
                if (menteeProfileRepository.existsByEmail(email)) {
                    throw new OtpException("You are already registered as a mentee", "MENTEE_EXISTS");
                }

                if (mentorProfileRepository.existsByEmail(email)) {
                    throw new OtpException("You are already registered as a mentor with this account. Please proceed with another account.", "MENTOR_EXISTS");
                }

                if (usersRepository.existsByEmailId(email)) {
                    throw new OtpException("Your email already exists in our system", "USER_EXISTS");
                }
            }

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

            // Send email
            emailService.sendOTPUser(email, otp, otpPurpose);

            return CommonResponse.<String>builder()
                    .message(OTP_SENT_SUCCESS)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (MailAuthenticationException | MailSendException | OtpException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_SENDING_OTP + e.getMessage());
        }
    }


//    public CommonResponse<String> sendOtp(String email) throws UnexpectedServerException {
//
//        try {
//
//            String otp = commonFiles.generateOTP(6);
//            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
//
//            Otp otpEntity = otpRepository.findByEmail(email)
//                    .map(existingOtp -> {
//                        existingOtp.setOtp(otp);
//                        existingOtp.setExpiryTime(expiryTime);
//                        return existingOtp;
//                    })
//                    .orElse(new Otp(email, otp, expiryTime));
//
//            otpRepository.save(otpEntity);
//
//            commonFiles.sendOTPUser(email, otp);
//
//            return CommonResponse.<String>builder()
//                    .message(OTP_SENT_SUCCESS)
//                    .status(STATUS_TRUE)
//                    .statusCode(SUCCESS_CODE)
//                    .build();
//
//        } catch (MailAuthenticationException | MailSendException | OtpException e){
//            throw e;
//        } catch (Exception e) {
//            throw new UnexpectedServerException(ERROR_SENDING_OTP + e.getMessage());
//        }
//    }

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
