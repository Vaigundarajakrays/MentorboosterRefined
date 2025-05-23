package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.NotificationDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Notification;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentorboosters/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){this.notificationService=notificationService;}

    @PostMapping("/saveNotification")
    public CommonResponse<Notification> saveNotification(@RequestBody NotificationDTO notificationDTO) throws UnexpectedServerException, ResourceNotFoundException {
        return notificationService.saveNotification(notificationDTO);
    }

    @GetMapping("/getAllNotificationsByMentorId/{mentorId}")
    public CommonResponse<List<Notification>> getAllNotificationByMentorId(@PathVariable Long mentorId) throws UnexpectedServerException {
        return notificationService.getAllNotificationByMentorId(mentorId, false);
    }

    @GetMapping("/getAllNotificationsByUserId/{userId}")
    public CommonResponse<List<Notification>> getAllNotificationByUserId(@PathVariable Long userId) throws UnexpectedServerException {
        return notificationService.getAllNotificationByUserId(userId, false);
    }

    @GetMapping("/getNotificationById/{id}")
    public  CommonResponse<Notification> getNotificationById(@PathVariable Long id) throws ResourceNotFoundException {
        return notificationService.getNotificationById(id);
    }

    @GetMapping("/getAllNotificationsByIsRead")
    public CommonResponse<List<Notification>> getAllNotificationsByIsRead() throws UnexpectedServerException {
        return notificationService.getAllNotificationsByIsRead(false);
    }

    @PutMapping("/updateNotificationAsRead/{id}")
    public CommonResponse<Notification> updateNotificationAsRead(@PathVariable Long id) throws ResourceNotFoundException, UnexpectedServerException {
        return notificationService.updateNotificationsAsRead(id);
    }

}
