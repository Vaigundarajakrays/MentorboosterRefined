//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.dto.ChangePasswordRequest;
//import com.mentorboosters.app.dto.LoginRequest;
//import com.mentorboosters.app.dto.LoginResponse;
//import com.mentorboosters.app.enumUtil.Role;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Users;
//import com.mentorboosters.app.repository.UsersRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.security.JwtService;
//import com.mentorboosters.app.service.AuthService;
//import com.mentorboosters.app.service.UsersService;
//import com.mentorboosters.app.util.CommonFiles;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDateTime;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthControllerTest {
//
//    @Mock
//    UsersRepository usersRepository;
//
//    @Mock
//    PasswordEncoder passwordEncoder;
//
//    @Mock
//    AuthenticationManager authenticationManager;
//
//    @Mock
//    JwtService jwtService;
//
//    @Mock
//    CommonFiles commonFiles;
//
//    @InjectMocks
//    UsersService usersService;
//
//    @InjectMocks
//    AuthService authService;
//
//    @Test
//    void signUp_shouldSaveUserSuccessfully() throws UnexpectedServerException {
//        Users newUser = Users.builder()
//                .userName("newUser")
//                .name("New Name")
//                .emailId("new@example.com")
//                .password("plainPassword")
//                .age(30)
//                .gender("Male")
//                .build();
//
//        Users savedUser = Users.builder()
//                .id(1L)
//                .userName("newUser")
//                .name("New Name")
//                .emailId("new@example.com")
//                .password("hashedPassword")
//                .age(30)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//        savedUser.setCreatedAt(LocalDateTime.now());
//        savedUser.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.existsByEmailId("new@example.com")).thenReturn(false);
//        when(usersRepository.existsByUserName("newUser")).thenReturn(false);
//        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
//        when(usersRepository.save(any(Users.class))).thenReturn(savedUser);
//
//        CommonResponse<Users> response = usersService.signUp(newUser);
//        Users resultUser = response.getData();
//
//        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        assertNotNull(resultUser);
//        assertEquals(1L, resultUser.getId());
//        assertEquals("newUser", resultUser.getUserName());
//        assertEquals("New Name", resultUser.getName());
//        assertEquals("new@example.com", resultUser.getEmailId());
//        assertEquals("hashedPassword", resultUser.getPassword());
//        assertEquals(30, resultUser.getAge());
//        assertEquals("Male", resultUser.getGender());
//        assertEquals(Role.USER, resultUser.getRole());
//        assertNotNull(resultUser.getCreatedAt());
//        assertNotNull(resultUser.getUpdatedAt());
//
//        verify(usersRepository).existsByEmailId("new@example.com");
//        verify(usersRepository).existsByUserName("newUser");
//        verify(passwordEncoder).encode("plainPassword");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//    @Test
//    void signUp_shouldReturnError_whenEmailAlreadyExists() throws UnexpectedServerException {
//        Users user = Users.builder()
//                .userName("newUser")
//                .emailId("existing@example.com")
//                .password("password123")
//                .build();
//
//        when(usersRepository.existsByEmailId("existing@example.com")).thenReturn(true);
//
//        CommonResponse<Users> response = usersService.signUp(user);
//
//        assertEquals(EMAIL_ALREADY_EXISTS, response.getMessage());
//        assertFalse(response.getStatus());
//        assertEquals(FORBIDDEN_CODE, response.getStatusCode());
//        assertEquals(user, response.getData());
//
//        verify(usersRepository).existsByEmailId("existing@example.com");
//        verifyNoMoreInteractions(usersRepository);
//    }
//
//    @Test
//    void signUp_shouldReturnError_whenUsernameAlreadyExists() throws UnexpectedServerException {
//        Users user = Users.builder()
//                .userName("existingUser")
//                .emailId("new@example.com")
//                .password("password123")
//                .build();
//
//        when(usersRepository.existsByEmailId("new@example.com")).thenReturn(false);
//        when(usersRepository.existsByUserName("existingUser")).thenReturn(true);
//
//        CommonResponse<Users> response = usersService.signUp(user);
//
//        assertEquals(USERNAME_ALREADY_EXISTS, response.getMessage());
//        assertFalse(response.getStatus());
//        assertEquals(FORBIDDEN_CODE, response.getStatusCode());
//        assertEquals(user, response.getData());
//
//        verify(usersRepository).existsByEmailId("new@example.com");
//        verify(usersRepository).existsByUserName("existingUser");
//    }
//
//    @Test
//    void signUp_shouldThrowUnexpectedServerException_whenSomethingGoesWrong() {
//        Users user = Users.builder()
//                .userName("newUser")
//                .emailId("new@example.com")
//                .password("password123")
//                .build();
//
//        when(usersRepository.existsByEmailId("new@example.com")).thenReturn(false);
//        when(usersRepository.existsByUserName("newUser")).thenReturn(false);
//        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
//        when(usersRepository.save(any(Users.class))).thenThrow(new RuntimeException("DB error"));
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class,
//                () -> usersService.signUp(user));
//
//        assertTrue(ex.getMessage().contains(ERROR_DURING_SIGN_UP));
//        assertTrue(ex.getMessage().contains("DB error"));
//
//        verify(usersRepository).existsByEmailId("new@example.com");
//        verify(usersRepository).existsByUserName("newUser");
//        verify(passwordEncoder).encode("password123");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//    @Test
//    void adminCreate_shouldReturnSuccessResponse_whenAdminIsNew() throws UnexpectedServerException {
//        Users newAdmin = Users.builder()
//                .userName("adminUser")
//                .name("Admin Name")
//                .emailId("admin@example.com")
//                .password("plainPassword")
//                .age(35)
//                .gender("Other")
//                .build();
//
//        Users savedAdmin = Users.builder()
//                .id(1L)
//                .userName("adminUser")
//                .name("Admin Name")
//                .emailId("admin@example.com")
//                .password("hashedPassword")
//                .age(35)
//                .gender("Other")
//                .role(Role.ADMIN)
//                .build();
//        savedAdmin.setCreatedAt(LocalDateTime.now());
//        savedAdmin.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.existsByEmailId("admin@example.com")).thenReturn(false);
//        when(usersRepository.existsByUserName("adminUser")).thenReturn(false);
//        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
//        when(usersRepository.save(any(Users.class))).thenReturn(savedAdmin);
//
//        CommonResponse<Users> response = usersService.adminCreate(newAdmin);
//        Users resultUser = response.getData();
//
//        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        assertNotNull(resultUser);
//        assertEquals("adminUser", resultUser.getUserName());
//        assertEquals("Admin Name", resultUser.getName());
//        assertEquals("admin@example.com", resultUser.getEmailId());
//        assertEquals("hashedPassword", resultUser.getPassword());
//        assertEquals(35, resultUser.getAge());
//        assertEquals("Other", resultUser.getGender());
//        assertEquals(Role.ADMIN, resultUser.getRole());
//
//        verify(usersRepository).existsByEmailId("admin@example.com");
//        verify(usersRepository).existsByUserName("adminUser");
//        verify(passwordEncoder).encode("plainPassword");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//    @Test
//    void adminCreate_shouldReturnForbidden_whenEmailAlreadyExists() throws UnexpectedServerException {
//        Users newAdmin = Users.builder()
//                .userName("adminUser")
//                .emailId("admin@example.com")
//                .build();
//
//        when(usersRepository.existsByEmailId("admin@example.com")).thenReturn(true);
//
//        CommonResponse<Users> response = usersService.adminCreate(newAdmin);
//
//        assertEquals(EMAIL_ALREADY_EXISTS, response.getMessage());
//        assertFalse(response.getStatus());
//        assertEquals(FORBIDDEN_CODE, response.getStatusCode());
//        assertEquals(newAdmin, response.getData());
//
//        verify(usersRepository).existsByEmailId("admin@example.com");
//        verify(usersRepository, never()).existsByUserName(any());
//        verify(usersRepository, never()).save(any());
//    }
//
//    @Test
//    void adminCreate_shouldReturnForbidden_whenUsernameAlreadyExists() throws UnexpectedServerException {
//        Users newAdmin = Users.builder()
//                .userName("adminUser")
//                .emailId("admin@example.com")
//                .build();
//
//        when(usersRepository.existsByEmailId("admin@example.com")).thenReturn(false);
//        when(usersRepository.existsByUserName("adminUser")).thenReturn(true);
//
//        CommonResponse<Users> response = usersService.adminCreate(newAdmin);
//
//        assertEquals(USERNAME_ALREADY_EXISTS, response.getMessage());
//        assertFalse(response.getStatus());
//        assertEquals(FORBIDDEN_CODE, response.getStatusCode());
//        assertEquals(newAdmin, response.getData());
//
//        verify(usersRepository).existsByEmailId("admin@example.com");
//        verify(usersRepository).existsByUserName("adminUser");
//        verify(usersRepository, never()).save(any());
//    }
//
//    @Test
//    void adminCreate_shouldThrowUnexpectedServerException_whenExceptionOccurs() {
//        Users newAdmin = Users.builder()
//                .userName("adminUser")
//                .emailId("admin@example.com")
//                .password("plainPassword")
//                .build();
//
//        when(usersRepository.existsByEmailId("admin@example.com")).thenReturn(false);
//        when(usersRepository.existsByUserName("adminUser")).thenReturn(false);
//        when(passwordEncoder.encode("plainPassword")).thenThrow(new RuntimeException("DB failure"));
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class, () ->
//                usersService.adminCreate(newAdmin));
//
//        assertTrue(ex.getMessage().contains(ERROR_DURING_SIGN_UP));
//        assertTrue(ex.getMessage().contains("DB failure"));
//
//        verify(usersRepository).existsByEmailId("admin@example.com");
//        verify(usersRepository).existsByUserName("adminUser");
//        verify(passwordEncoder).encode("plainPassword");
//    }
//
//    @Test
//    void authenticate_shouldReturnToken_whenCredentialsAreValid() throws UnexpectedServerException {
//        LoginRequest request = new LoginRequest("user@example.com", "password123");
//
//        Users user = Users.builder()
//                .id(1L)
//                .userName("testUser")
//                .emailId("user@example.com")
//                .password("hashedPassword")
//                .role(Role.USER)
//                .build();
//
//        String expectedToken = "mocked-jwt-token";
//
//        when(usersRepository.findByEmailId("user@example.com")).thenReturn(user);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(mock(Authentication.class));
//
//        when(jwtService.generateToken(user)).thenReturn(expectedToken);
//
//        CommonResponse<LoginResponse> response = authService.authenticate(request);
//
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(LOGIN_SUCCESS, response.getMessage());
//        assertNotNull(response.getData());
//        assertEquals(expectedToken, response.getData().getToken());
//
//        verify(usersRepository).findByEmailId("user@example.com");
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtService).generateToken(user);
//    }
//
//    @Test
//    void authenticate_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
//        LoginRequest request = new LoginRequest("invalid@example.com", "password123");
//
//        when(usersRepository.findByEmailId("invalid@example.com")).thenReturn(null);
//
//        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
//                authService.authenticate(request));
//
//        assertEquals(USER_NOT_FOUND_WITH_EMAIL + "invalid@example.com", exception.getMessage());
//        verify(usersRepository).findByEmailId("invalid@example.com");
//        verifyNoInteractions(authenticationManager, jwtService);
//    }
//
//    @Test
//    void authenticate_shouldThrowUnexpectedServerException_whenAuthenticationFails() {
//        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");
//
//        Users user = Users.builder()
//                .userName("testUser")
//                .emailId("user@example.com")
//                .build();
//
//        when(usersRepository.findByEmailId("user@example.com")).thenReturn(user);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new RuntimeException("Bad credentials"));
//
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () ->
//                authService.authenticate(request));
//
//        assertTrue(exception.getMessage().contains(ERROR_LOGGING_IN));
//        assertTrue(exception.getMessage().contains("Bad credentials"));
//
//        verify(usersRepository).findByEmailId("user@example.com");
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtService, never()).generateToken(any());
//    }
//
//    @Test
//    void sendOtp_shouldReturnSuccessResponse() throws UnexpectedServerException {
//        String email = "test@example.com";
//        String otp = "123456";
//
//        when(commonFiles.generateOTP(6)).thenReturn(otp);
//        doNothing().when(commonFiles).sendOTPUser(email, otp);
//
//        CommonResponse<String> response = authService.sendOtp(email);
//
//        assertEquals(OTP_SENT_SUCCESS, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(otp, response.getData());
//
//        verify(commonFiles).generateOTP(6);
//        verify(commonFiles).sendOTPUser(email, otp);
//    }
//
//    @Test
//    void sendOtp_shouldThrowUnexpectedServerException_whenOtpSendingFails() {
//        String email = "fail@example.com";
//
//        when(commonFiles.generateOTP(6)).thenReturn("654321");
//        doThrow(new RuntimeException("Email service down")).when(commonFiles).sendOTPUser(eq(email), anyString());
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class, () ->
//                authService.sendOtp(email)
//        );
//
//        assertTrue(ex.getMessage().contains(ERROR_SENDING_OTP));
//        verify(commonFiles).generateOTP(6);
//        verify(commonFiles).sendOTPUser(eq(email), anyString());
//    }
//
//    @Test
//    void changePassword_ShouldUpdateSuccessfully() throws Exception {
//        ChangePasswordRequest request = ChangePasswordRequest.builder()
//                .email("john@example.com")
//                .newPassword("newSecret456")
//                .build();
//
//        Long userId = 1L;
//        Users user = Users.builder()
//                .id(userId)
//                .userName("john_doe")
//                .name("John Doe")
//                .emailId("john@example.com")
//                .password("secret123")
//                .age(30)
//                .gender("Male")
//                .role(Role.MENTOR)
//                .build();
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.findByEmailId("john@example.com")).thenReturn(user);
//        when(passwordEncoder.encode("newSecret456")).thenReturn("encodedPassword");
//        when(usersRepository.save(any(Users.class))).thenReturn(user);
//
//        CommonResponse<String> response = authService.changePassword(request);
//
//        assertNotNull(response);
//        assertEquals("Password changed successfully", response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals("Password updated", response.getData());
//
//        verify(usersRepository).findByEmailId("john@example.com");
//        verify(passwordEncoder).encode("newSecret456");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//    @Test
//    void changePassword_ShouldThrowUserNotFoundException() {
//        ChangePasswordRequest request = ChangePasswordRequest.builder()
//                .email("unknown@example.com")
//                .newPassword("newSecret456")
//                .build();
//
//        when(usersRepository.findByEmailId("unknown@example.com")).thenReturn(null);
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
//                authService.changePassword(request)
//        );
//
//        assertEquals(USER_NOT_FOUND_WITH_EMAIL + "unknown@example.com", exception.getMessage());
//        verify(usersRepository).findByEmailId("unknown@example.com");
//    }
//
//    @Test
//    void changePassword_ShouldThrowUnexpectedServerException() {
//        ChangePasswordRequest request = ChangePasswordRequest.builder()
//                .email("john@example.com")
//                .newPassword("newSecret456")
//                .build();
//
//        Long userId = 1L;
//        Users user = Users.builder()
//                .id(userId)
//                .userName("john_doe")
//                .name("John Doe")
//                .emailId("john@example.com")
//                .password("secret123")
//                .age(30)
//                .gender("Male")
//                .role(Role.MENTOR)
//                .build();
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.findByEmailId("john@example.com")).thenReturn(user);
//        when(passwordEncoder.encode("newSecret456")).thenReturn("encodedPassword");
//        when(usersRepository.save(any(Users.class))).thenThrow(new RuntimeException("DB error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () ->
//                authService.changePassword(request)
//        );
//
//        assertTrue(exception.getMessage().contains("Error when updating new password: DB error"));
//
//        verify(usersRepository).findByEmailId("john@example.com");
//        verify(passwordEncoder).encode("newSecret456");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//
//
//
//
//
//
//
//
//}
