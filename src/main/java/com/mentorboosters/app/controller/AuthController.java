package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.ChangePasswordRequest;
import com.mentorboosters.app.dto.LoginRequest;
import com.mentorboosters.app.dto.LoginResponse;
import com.mentorboosters.app.enumUtil.OtpPurpose;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.AuthService;
import com.mentorboosters.app.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersService usersService;
    private final AuthService authService;

    public AuthController(UsersService usersService, AuthService authService){
        this.usersService=usersService;
        this.authService=authService;
    }

//    @PostMapping("/signUp")
//    public CommonResponse<Users> signUp(@RequestBody Users users) throws UnexpectedServerException {
//        return usersService.signUp(users);
//    }

    @PostMapping("/adminSignUp")
    public CommonResponse<Users> adminCreate(@RequestBody Users users) throws UnexpectedServerException {
        return usersService.adminCreate(users);
    }

    @PostMapping("/login")
    public CommonResponse<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) throws UnexpectedServerException, ResourceNotFoundException {
        return authService.authenticate(loginRequest);
    }

    @PostMapping("/sendOtp")
    public CommonResponse<String> sendOtp(@RequestParam String email, @RequestParam String purpose) throws UnexpectedServerException {
        OtpPurpose otpPurpose = OtpPurpose.from(purpose);
        return authService.sendOtp(email, otpPurpose);
    }

//    @PostMapping("/sendOtp")
//    public CommonResponse<String> sendOtp(@RequestParam String email) throws UnexpectedServerException {
//        return authService.sendOtp(email);
//    }

    @PostMapping("/verifyOtp")
    public CommonResponse<String> verifyOtp(@RequestParam String email, @RequestParam String otp) throws UnexpectedServerException {
        return authService.verifyOtp(email, otp);
    }

    @PutMapping("/changePassword")
    public CommonResponse<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) throws UnexpectedServerException, ResourceNotFoundException {
        return authService.changePassword(changePasswordRequest);
    }
}
