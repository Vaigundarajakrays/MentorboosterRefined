package com.mentorboosters.app.service;

import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder){
        this.usersRepository=usersRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public CommonResponse<Users> signUp(Users users) throws UnexpectedServerException {

        if(users.getPhoneNumber()==null){
            return CommonResponse.<Users>builder()
                    .message(PHONE_NUMBER_REQUIRED)
                    .status(STATUS_FALSE)
                    .statusCode(BAD_REQUEST)
                    .build();
        }

        try {

            boolean exists = usersRepository.existsByEmailIdOrPhoneNumber(users.getEmailId(), users.getPhoneNumber());

            if (exists) {
                return CommonResponse.<Users>builder()
                        .message(EMAIL_OR_PHONE_NUMBER_ALREADY_EXISTS)
                        .status(STATUS_FALSE)
                        .data(users)
                        .statusCode(FORBIDDEN_CODE)
                        .build();
            }

            String hashedPassword = passwordEncoder.encode(users.getPassword());
            users.setPassword(hashedPassword);
            users.setRole(Role.USER);

            Users savedUser = usersRepository.save(users);

            return CommonResponse.<Users>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(savedUser)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_DURING_SIGN_UP + e.getMessage());
        }


    }

    public CommonResponse<Users> adminCreate(Users users) throws UnexpectedServerException {

        try {

            boolean exists = usersRepository.existsByEmailIdOrPhoneNumber(users.getEmailId(), users.getPhoneNumber());

            if (exists) {
                return CommonResponse.<Users>builder()
                        .message(EMAIL_OR_PHONE_NUMBER_ALREADY_EXISTS)
                        .status(STATUS_FALSE)
                        .data(users)
                        .statusCode(FORBIDDEN_CODE)
                        .build();
            }

            String hashedPassword = passwordEncoder.encode(users.getPassword());
            users.setPassword(hashedPassword);
            users.setRole(Role.ADMIN);
            users.setPhoneNumber(null);

            Users savedUser = usersRepository.save(users);

            return CommonResponse.<Users>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(savedUser)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_DURING_SIGN_UP + e.getMessage());
        }

    }

    public CommonResponse<Users> getUserById(Long id) throws ResourceNotFoundException {

        Users user = usersRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

        return CommonResponse.<Users>builder()
                .message(LOADED_USER_DETAILS)
                .status(STATUS_TRUE)
                .data(user)
                .statusCode(SUCCESS_CODE)
                .build();

    }

    public CommonResponse<Users> updateUser(Long id, Users updatedUser) throws ResourceNotFoundException, UnexpectedServerException {

        Users existingUser = usersRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

        try {

            existingUser.setName(updatedUser.getName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setEmailId(updatedUser.getEmailId());
            existingUser.setDescription(updatedUser.getDescription());
            existingUser.setGoals(updatedUser.getGoals());

            Users updated = usersRepository.save(existingUser);

            return CommonResponse.<Users>builder()
                    .message(USER_UPDATED)
                    .status(STATUS_TRUE)
                    .data(updated)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_UPDATING_USER + e.getMessage());
        }

    }
}
