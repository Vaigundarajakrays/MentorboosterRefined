package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.SubscribeDTO;
import com.mentorboosters.app.dto.SubscribeResponseDTO;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.enumUtil.SubscribeStatus;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Subscribe;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.repository.SubscribeRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final UsersRepository usersRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public CommonResponse<SubscribeResponseDTO> subscribe(String email) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            Users user = usersRepository.findByEmailId(email);

            if (user == null) {
                throw new ResourceNotFoundException("User not found with email: " + email);
            }

            // Double check
            if(user.getRole()== Role.MENTOR){
                if(!mentorProfileRepository.existsByEmail(email)){
                    throw new ResourceNotFoundException("Mentor not found with email: " + email);
                }
            }

            if(user.getRole()==Role.USER){
                if(!menteeProfileRepository.existsByEmail(email)){
                    throw new ResourceNotFoundException("Mentee not found with email: " + email);
                }
            }

            var status = SubscribeResponseDTO.builder()
                    .isSubscribed(true)
                    .build();

            if(subscribeRepository.existsByEmail(email)){
                return CommonResponse.<SubscribeResponseDTO>builder()
                        .statusCode(SUCCESS_CODE)
                        .status(STATUS_TRUE)
                        .message("This user is already subscribed")
                        .data(status)
                        .build();
            }

            var subscribe = Subscribe.builder()
                    .email(email)
                    .role(user.getRole())
                    .status(SubscribeStatus.SUBSCRIBED)
                    .build();

            subscribeRepository.save(subscribe);

            return CommonResponse.<SubscribeResponseDTO>builder()
                    .statusCode(SUCCESS_CODE)
                    .status(STATUS_TRUE)
                    .message("Subscribed successfully")
                    .data(status)
                    .build();

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException("Error while subscribing: " + e.getMessage());
        }

    }
}
