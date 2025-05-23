//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.enumUtil.Role;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Users;
//import com.mentorboosters.app.repository.UsersRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.UsersService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UsersControllerTest {
//
//    @Mock
//    UsersRepository usersRepository;
//
//    @Mock
//    PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    UsersService usersService;
//
//    @Test
//    void getUserById_shouldReturnUserDetails_whenUserExists() throws ResourceNotFoundException {
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
//                .role(Role.USER)
//                .build();
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        CommonResponse<Users> response = usersService.getUserById(userId);
//
//        assertEquals(LOADED_USER_DETAILS, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        Users returnedUser = response.getData();
//        assertNotNull(returnedUser);
//        assertEquals(userId, returnedUser.getId());
//        assertEquals("john_doe", returnedUser.getUserName());
//        assertEquals("John Doe", returnedUser.getName());
//        assertEquals("john@example.com", returnedUser.getEmailId());
//        assertEquals("secret123", returnedUser.getPassword());
//        assertEquals(30, returnedUser.getAge());
//        assertEquals("Male", returnedUser.getGender());
//        assertEquals(Role.USER, returnedUser.getRole());
//        assertNotNull(returnedUser.getCreatedAt());
//        assertNotNull(returnedUser.getUpdatedAt());
//
//        verify(usersRepository).findById(userId);
//    }
//
//    @Test
//    void getUserById_shouldThrowException_whenUserNotFound() {
//
//        Long userId = 99L;
//        when(usersRepository.findById(userId)).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            usersService.getUserById(userId);
//        });
//
//        assertEquals(USER_NOT_FOUND_WITH_ID + userId, exception.getMessage());
//        verify(usersRepository).findById(userId);
//    }
//
//    @Test
//    void updateUser_shouldUpdateUserSuccessfully() throws ResourceNotFoundException, UnexpectedServerException {
//        Long userId = 1L;
//
//        Users existingUser = Users.builder()
//                .id(userId)
//                .userName("oldUser")
//                .name("Old Name")
//                .emailId("old@example.com")
//                .password("oldPassword")
//                .age(25)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//        existingUser.setCreatedAt(LocalDateTime.now().minusDays(1));
//        existingUser.setUpdatedAt(LocalDateTime.now().minusDays(1));
//
//        Users updatedUserInput = Users.builder()
//                .userName("newUser")
//                .name("New Name")
//                .emailId("new@example.com")
//                .password("newPassword")
//                .age(30)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//
//        String encodedPassword = "encodedPassword123";
//
//        Users savedUser = Users.builder()
//                .id(userId)
//                .userName("newUser")
//                .name("New Name")
//                .emailId("new@example.com")
//                .password(encodedPassword)
//                .age(30)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//        savedUser.setCreatedAt(existingUser.getCreatedAt());
//        savedUser.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
//        when(passwordEncoder.encode(updatedUserInput.getPassword())).thenReturn(encodedPassword);
//        when(usersRepository.save(any(Users.class))).thenReturn(savedUser);
//
//        CommonResponse<Users> response = usersService.updateUser(userId, updatedUserInput);
//
//        assertEquals(USER_UPDATED, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        Users resultUser = response.getData();
//        assertNotNull(resultUser);
//        assertEquals(savedUser.getId(), resultUser.getId());
//        assertEquals(savedUser.getUserName(), resultUser.getUserName());
//        assertEquals(savedUser.getName(), resultUser.getName());
//        assertEquals(savedUser.getEmailId(), resultUser.getEmailId());
//        assertEquals(savedUser.getPassword(), resultUser.getPassword());
//        assertEquals(savedUser.getAge(), resultUser.getAge());
//        assertEquals(savedUser.getGender(), resultUser.getGender());
//        assertEquals(savedUser.getRole(), resultUser.getRole());
//        assertEquals(savedUser.getCreatedAt(), resultUser.getCreatedAt());
//        assertEquals(savedUser.getUpdatedAt(), resultUser.getUpdatedAt());
//
//        verify(usersRepository).findById(userId);
//        verify(passwordEncoder).encode("newPassword");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//    @Test
//    void updateUser_shouldThrowException_whenUserNotFound() {
//        Long userId = 1L;
//        Users updatedUser = Users.builder()
//                .userName("newUser")
//                .name("New Name")
//                .emailId("new@example.com")
//                .password("newPassword")
//                .age(30)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//
//        when(usersRepository.findById(userId)).thenReturn(Optional.empty());
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
//                () -> usersService.updateUser(userId, updatedUser));
//
//        assertEquals(USER_NOT_FOUND_WITH_ID + userId, ex.getMessage());
//
//        verify(usersRepository).findById(userId);
//        verifyNoMoreInteractions(usersRepository);
//        verifyNoInteractions(passwordEncoder);
//    }
//
//    @Test
//    void updateUser_shouldThrowUnexpectedServerException_whenSaveFails() {
//        Long userId = 1L;
//
//        Users existingUser = Users.builder()
//                .id(userId)
//                .userName("oldUser")
//                .name("Old Name")
//                .emailId("old@example.com")
//                .password("oldPassword")
//                .age(25)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//
//        Users updatedUser = Users.builder()
//                .userName("newUser")
//                .name("New Name")
//                .emailId("new@example.com")
//                .password("newPassword")
//                .age(30)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//
//        when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
//        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("encodedPassword");
//        when(usersRepository.save(any(Users.class))).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class,
//                () -> usersService.updateUser(userId, updatedUser));
//
//        assertTrue(ex.getMessage().contains(ERROR_UPDATING_USER));
//        assertTrue(ex.getMessage().contains("Database error"));
//
//        verify(usersRepository).findById(userId);
//        verify(passwordEncoder).encode("newPassword");
//        verify(usersRepository).save(any(Users.class));
//    }
//
//
//
//
//}
