package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.ChangePasswordRequest;
import com.mentorboosters.app.dto.LoginRequest;
import com.mentorboosters.app.dto.LoginResponse;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.security.JwtService;
import com.mentorboosters.app.util.CommonFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CommonFiles commonFiles;
    private final PasswordEncoder passwordEncoder;

    public AuthService( CommonFiles commonFiles, UsersRepository usersRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder){
        this.usersRepository=usersRepository;
        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
        this.commonFiles=commonFiles;
        this.passwordEncoder=passwordEncoder;
    }

    public CommonResponse<LoginResponse> authenticate(LoginRequest request) throws UnexpectedServerException {

        Users user = usersRepository.findByEmailId(request.getEmailId());

        if (user == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_WITH_EMAIL + request.getEmailId());
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), request.getPassword())
            );

            String token = jwtService.generateToken(user);

            LoginResponse loginResponse = new LoginResponse(token);

            return CommonResponse.<LoginResponse>builder()
                    .message(LOGIN_SUCCESS)
                    .status(STATUS_TRUE)
                    .data(loginResponse)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_LOGGING_IN + e.getMessage());
        }


    }

    public CommonResponse<String> sendOtp(String email) throws UnexpectedServerException {

        try {

            String otp = commonFiles.generateOTP(6);
            commonFiles.sendOTPUser(email, otp);

            return CommonResponse.<String>builder()
                    .message(OTP_SENT_SUCCESS)
                    .status(STATUS_TRUE)
                    .data(otp)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_SENDING_OTP + e.getMessage());
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
