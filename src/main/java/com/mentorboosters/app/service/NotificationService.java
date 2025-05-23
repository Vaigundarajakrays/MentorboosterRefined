package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.NotificationDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Notification;
import com.mentorboosters.app.repository.NotificationRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

    public NotificationService(NotificationRepository notificationRepository, UsersRepository usersRepository){
        this.notificationRepository=notificationRepository;
        this.usersRepository=usersRepository;
    }

    public CommonResponse<Notification> saveNotification(NotificationDTO notificationDTO) throws UnexpectedServerException, ResourceNotFoundException {

        String recipientName = usersRepository.findUserNameById(notificationDTO.getRecipientId());

        if (recipientName == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + notificationDTO.getRecipientId());
        }

        try {
            Notification notification = Notification.builder()
                    .mentorId(notificationDTO.getMentorId())
                    .recipientId(notificationDTO.getRecipientId())
                    .recipientName(recipientName)
                    .title(notificationDTO.getTitle())
                    .message(notificationDTO.getMessage())
                    .build();

            Notification savedNotification = notificationRepository.save(notification);

            return CommonResponse.<Notification>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(savedNotification)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_ADDING_NOTIFICATION + e.getMessage());
        }

    }

    public CommonResponse<List<Notification>> getAllNotificationByMentorId(Long mentorId, Boolean isRead) throws UnexpectedServerException {

        try {

            List<Notification> notifications = notificationRepository.findByMentorIdAndIsRead(mentorId, isRead);

            if (notifications.isEmpty()) {
                return CommonResponse.<List<Notification>>builder()
                        .message(NO_NOTIFICATIONS_AVAILABLE_FOR_MENTOR)
                        .status(STATUS_TRUE)
                        .data(notifications)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<Notification>>builder()
                    .message(LOADED_ALL_NOTIFICATIONS_FOR_MENTOR)
                    .status(STATUS_TRUE)
                    .data(notifications)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_FETCHING_NOTIFICATIONS_FOR_MENTOR + e.getMessage());
        }

    }

    public CommonResponse<List<Notification>> getAllNotificationByUserId(Long userId, boolean isRead) throws UnexpectedServerException {

        try {

            List<Notification> notifications = notificationRepository.findByRecipientIdAndIsRead(userId, isRead);

            if (notifications.isEmpty()) {
                return CommonResponse.<List<Notification>>builder()
                        .message(NO_NOTIFICATIONS_AVAILABLE_FOR_USER)
                        .status(STATUS_TRUE)
                        .data(notifications)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<Notification>>builder()
                    .message(LOADED_ALL_NOTIFICATIONS_FOR_USER)
                    .status(STATUS_TRUE)
                    .data(notifications)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_FETCHING_NOTIFICATIONS_FOR_USER + e.getMessage());
        }
    }

    public CommonResponse<Notification> getNotificationById(Long id) throws ResourceNotFoundException {

        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOTIFICATION_NOT_FOUND_BY_ID + id));

        return CommonResponse.<Notification>builder()
                .message(LOADED_NOTIFICATION_FOR_ID + id)
                .status(STATUS_TRUE)
                .data(notification)
                .statusCode(SUCCESS_CODE)
                .build();

    }

    public CommonResponse<List<Notification>> getAllNotificationsByIsRead(Boolean isRead) throws UnexpectedServerException {

        try {

            List<Notification> notifications = notificationRepository.findByIsRead(isRead);

            if (notifications.isEmpty()) {
                return CommonResponse.<List<Notification>>builder()
                        .message(NO_NOTIFICATIONS_AVAILABLE)
                        .status(STATUS_TRUE)
                        .data(notifications)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<Notification>>builder()
                    .message(LOADED_ALL_NOTIFICATIONS)
                    .status(STATUS_TRUE)
                    .data(notifications)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_FETCHING_NOTIFICATIONS + e.getMessage());
        }

    }

    public CommonResponse<Notification> updateNotificationsAsRead(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOTIFICATION_NOT_FOUND_BY_ID + id));

        if(notification.getIsRead()){
            return CommonResponse.<Notification>builder()
                    .message(ALREADY_MARKED_AS_TRUE)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .build();
        }

        try {

            notification.setReadAt(LocalDateTime.now());
            notification.setIsRead(true);

            Notification updatedNotification = notificationRepository.save(notification);

            return CommonResponse.<Notification>builder()
                    .message(SUCCESSFULLY_UPDATED_AS_READ)
                    .status(STATUS_TRUE)
                    .data(updatedNotification)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_UPDATING_NOTIFICATION_AS_READ + e.getMessage());
        }
    }
}
