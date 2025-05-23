//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.dto.NotificationDTO;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Notification;
//import com.mentorboosters.app.repository.NotificationRepository;
//import com.mentorboosters.app.repository.UsersRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.NotificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class NotificationControllerTest {
//
//    @Mock
//    NotificationRepository notificationRepository;
//
//    @Mock
//    UsersRepository usersRepository;
//
//    @InjectMocks
//    NotificationService notificationService;
//
//    @Test
//    void saveNotification_shouldReturnNotification_whenValidInputGiven() throws UnexpectedServerException, ResourceNotFoundException {
//
//        NotificationDTO dto = NotificationDTO.builder()
//                .title("Session Reminder")
//                .message("Your session starts in 1 hour")
//                .recipientId(101L)
//                .mentorId(202L)
//                .build();
//
//        String recipientName = "John Doe";
//
//        Notification savedNotification = Notification.builder()
//                .id(1L)
//                .title(dto.getTitle())
//                .message(dto.getMessage())
//                .recipientId(dto.getRecipientId())
//                .recipientName(recipientName)
//                .mentorId(dto.getMentorId())
//                .isRead(false)
//                .build();
//        savedNotification.setCreatedAt(LocalDateTime.now());
//        savedNotification.setUpdatedAt(LocalDateTime.now());
//
//        when(usersRepository.findUserNameById(dto.getRecipientId())).thenReturn(recipientName);
//        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
//
//        CommonResponse<Notification> response = notificationService.saveNotification(dto);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(STATUS_TRUE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());
//
//        Notification actual = response.getData();
//        assertNotNull(actual);
//        assertEquals(dto.getTitle(), actual.getTitle());
//        assertEquals(dto.getMessage(), actual.getMessage());
//        assertEquals(dto.getRecipientId(), actual.getRecipientId());
//        assertEquals(dto.getMentorId(), actual.getMentorId());
//        assertEquals(recipientName, actual.getRecipientName());
//        assertFalse(actual.getIsRead());
//        assertNotNull(actual.getCreatedAt());
//        assertNotNull(actual.getUpdatedAt());
//
//        verify(usersRepository).findUserNameById(dto.getRecipientId());
//        verify(notificationRepository).save(any(Notification.class));
//    }
//
//    @Test
//    void saveNotification_shouldThrowResourceNotFoundException_whenRecipientNotFound() {
//
//        NotificationDTO dto = NotificationDTO.builder()
//                .title("Session Reminder")
//                .message("Session coming up")
//                .recipientId(999L)
//                .mentorId(202L)
//                .build();
//
//        when(usersRepository.findUserNameById(dto.getRecipientId())).thenReturn(null);
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            notificationService.saveNotification(dto);
//        });
//
//        assertEquals(USER_NOT_FOUND_WITH_ID + dto.getRecipientId(), exception.getMessage());
//
//        verify(usersRepository).findUserNameById(dto.getRecipientId());
//        verifyNoInteractions(notificationRepository);
//    }
//
//    @Test
//    void saveNotification_shouldThrowUnexpectedServerException_whenRepositoryFails() {
//
//        NotificationDTO dto = NotificationDTO.builder()
//                .title("Test Notification")
//                .message("This is a test")
//                .recipientId(101L)
//                .mentorId(202L)
//                .build();
//
//        String recipientName = "John Doe";
//
//        when(usersRepository.findUserNameById(dto.getRecipientId())).thenReturn(recipientName);
//        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("DB is down"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            notificationService.saveNotification(dto);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_ADDING_NOTIFICATION));
//
//        verify(usersRepository).findUserNameById(dto.getRecipientId());
//        verify(notificationRepository).save(any(Notification.class));
//    }
//
//    @Test
//    void getAllNotificationByMentorId_shouldReturnNotifications_whenIsReadIsFalse() throws UnexpectedServerException {
//
//        Long mentorId = 1L;
//        Boolean isRead = false;
//
//        Notification notification = Notification.builder()
//                .id(1L)
//                .title("Session Reminder")
//                .message("You have a session at 10 AM")
//                .recipientId(2L)
//                .recipientName("John Doe")
//                .mentorId(mentorId)
//                .isRead(false)
//                .build();
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setUpdatedAt(LocalDateTime.now());
//
//        when(notificationRepository.findByMentorIdAndIsRead(mentorId, isRead)).thenReturn(List.of(notification));
//
//        CommonResponse<List<Notification>> response = notificationService.getAllNotificationByMentorId(mentorId, isRead);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(LOADED_ALL_NOTIFICATIONS_FOR_MENTOR, response.getMessage());
//        assertEquals(1, response.getData().size());
//
//        Notification result = response.getData().get(0);
//        assertEquals(notification.getId(), result.getId());
//        assertEquals(notification.getTitle(), result.getTitle());
//        assertEquals(notification.getMessage(), result.getMessage());
//        assertEquals(notification.getRecipientId(), result.getRecipientId());
//        assertEquals(notification.getRecipientName(), result.getRecipientName());
//        assertEquals(notification.getMentorId(), result.getMentorId());
//        assertEquals(notification.getIsRead(), result.getIsRead());
//
//        verify(notificationRepository).findByMentorIdAndIsRead(mentorId, isRead);
//    }
//
//    @Test
//    void getAllNotificationByMentorId_shouldReturnEmptyList_whenNoNotificationsFound() throws UnexpectedServerException {
//
//        Long mentorId = 1L;
//        Boolean isRead = false;
//
//        when(notificationRepository.findByMentorIdAndIsRead(mentorId, isRead)).thenReturn(List.of());
//
//        CommonResponse<List<Notification>> response = notificationService.getAllNotificationByMentorId(mentorId, isRead);
//
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(NO_NOTIFICATIONS_AVAILABLE_FOR_MENTOR, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//
//        verify(notificationRepository).findByMentorIdAndIsRead(mentorId, isRead);
//    }
//
//    @Test
//    void getAllNotificationByMentorId_shouldThrowUnexpectedServerException_whenRepositoryFails() throws UnexpectedServerException {
//
//        Long mentorId = 1L;
//        Boolean isRead = false;
//
//        when(notificationRepository.findByMentorIdAndIsRead(mentorId, isRead))
//                .thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException thrown = assertThrows(UnexpectedServerException.class, () ->
//                notificationService.getAllNotificationByMentorId(mentorId, isRead)
//        );
//
//        assertEquals(ERROR_FETCHING_NOTIFICATIONS_FOR_MENTOR + "Database error", thrown.getMessage());
//
//        verify(notificationRepository).findByMentorIdAndIsRead(mentorId, isRead);
//    }
//
//    @Test
//    void getAllNotificationByUserId_shouldReturnNotifications_whenPresent() throws UnexpectedServerException {
//        Long userId = 1L;
//        boolean isRead = false;
//
//        Notification notification = Notification.builder()
//                .id(100L)
//                .title("Reminder")
//                .message("Session at 5 PM")
//                .recipientId(userId)
//                .recipientName("John")
//                .mentorId(200L)
//                .isRead(isRead)
//                .readAt(null)
//                .build();
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setUpdatedAt(LocalDateTime.now());
//
//        when(notificationRepository.findByRecipientIdAndIsRead(userId, isRead))
//                .thenReturn(List.of(notification));
//
//        CommonResponse<List<Notification>> response = notificationService.getAllNotificationByUserId(userId, isRead);
//
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(LOADED_ALL_NOTIFICATIONS_FOR_USER, response.getMessage());
//        assertEquals(1, response.getData().size());
//
//        Notification returned = response.getData().get(0);
//        assertEquals(notification.getId(), returned.getId());
//        assertEquals(notification.getTitle(), returned.getTitle());
//        assertEquals(notification.getMessage(), returned.getMessage());
//        assertEquals(notification.getRecipientId(), returned.getRecipientId());
//        assertEquals(notification.getRecipientName(), returned.getRecipientName());
//        assertEquals(notification.getMentorId(), returned.getMentorId());
//        assertEquals(notification.getIsRead(), returned.getIsRead());
//
//        verify(notificationRepository).findByRecipientIdAndIsRead(userId, isRead);
//    }
//
//    @Test
//    void getAllNotificationByUserId_shouldReturnEmptyList_whenNoNotificationsExist() throws UnexpectedServerException {
//        Long userId = 1L;
//        boolean isRead = false;
//
//        when(notificationRepository.findByRecipientIdAndIsRead(userId, isRead))
//                .thenReturn(List.of());
//
//        CommonResponse<List<Notification>> response = notificationService.getAllNotificationByUserId(userId, isRead);
//
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(NO_NOTIFICATIONS_AVAILABLE_FOR_USER, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//
//        verify(notificationRepository).findByRecipientIdAndIsRead(userId, isRead);
//    }
//
//    @Test
//    void getAllNotificationByUserId_shouldThrowException_whenRepositoryFails() {
//        Long userId = 1L;
//        boolean isRead = false;
//
//        when(notificationRepository.findByRecipientIdAndIsRead(userId, isRead))
//                .thenThrow(new RuntimeException("DB Failure"));
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class, () ->
//                notificationService.getAllNotificationByUserId(userId, isRead));
//
//        assertEquals(ERROR_FETCHING_NOTIFICATIONS_FOR_USER + "DB Failure", ex.getMessage());
//
//        verify(notificationRepository).findByRecipientIdAndIsRead(userId, isRead);
//    }
//
//    @Test
//    void getNotificationById_shouldReturnNotification_whenFound() throws ResourceNotFoundException {
//        Long id = 1L;
//
//        Notification notification = Notification.builder()
//                .id(id)
//                .title("Reminder")
//                .message("Mentor session at 5 PM")
//                .recipientId(100L)
//                .recipientName("Alice")
//                .mentorId(200L)
//                .isRead(false)
//                .readAt(null)
//                .build();
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setUpdatedAt(LocalDateTime.now());
//
//        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
//
//        CommonResponse<Notification> response = notificationService.getNotificationById(id);
//
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(LOADED_NOTIFICATION_FOR_ID + id, response.getMessage());
//
//        Notification returned = response.getData();
//        assertEquals(notification.getId(), returned.getId());
//        assertEquals(notification.getTitle(), returned.getTitle());
//        assertEquals(notification.getMessage(), returned.getMessage());
//        assertEquals(notification.getRecipientId(), returned.getRecipientId());
//        assertEquals(notification.getRecipientName(), returned.getRecipientName());
//        assertEquals(notification.getMentorId(), returned.getMentorId());
//        assertEquals(notification.getIsRead(), returned.getIsRead());
//
//        verify(notificationRepository).findById(id);
//    }
//
//    @Test
//    void getNotificationById_shouldThrowException_whenNotificationNotFound() {
//        Long id = 99L;
//
//        when(notificationRepository.findById(id)).thenReturn(Optional.empty());
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
//                notificationService.getNotificationById(id));
//
//        assertEquals(NOTIFICATION_NOT_FOUND_BY_ID + id, ex.getMessage());
//
//        verify(notificationRepository).findById(id);
//    }
//
//    @Test
//    void getAllNotificationsByIsRead_shouldReturnNotifications_whenExist() throws UnexpectedServerException {
//        Boolean isRead = false;
//
//        LocalDateTime now = LocalDateTime.now();
//
//        Notification notification = Notification.builder()
//                .id(1L)
//                .title("Reminder")
//                .message("Mentor session at 5 PM")
//                .recipientId(100L)
//                .recipientName("Alice")
//                .mentorId(200L)
//                .isRead(isRead)
//                .readAt(null)
//                .build();
//        notification.setCreatedAt(now);
//        notification.setUpdatedAt(now);
//
//        when(notificationRepository.findByIsRead(isRead)).thenReturn(List.of(notification));
//
//        CommonResponse<List<Notification>> response = notificationService.getAllNotificationsByIsRead(isRead);
//
//        assertNotNull(response);
//        assertEquals(STATUS_TRUE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(LOADED_ALL_NOTIFICATIONS, response.getMessage());
//
//        List<Notification> resultList = response.getData();
//        assertNotNull(resultList);
//        assertEquals(1, resultList.size());
//
//        Notification result = resultList.get(0);
//        assertEquals(notification.getId(), result.getId());
//        assertEquals(notification.getTitle(), result.getTitle());
//        assertEquals(notification.getMessage(), result.getMessage());
//        assertEquals(notification.getRecipientId(), result.getRecipientId());
//        assertEquals(notification.getRecipientName(), result.getRecipientName());
//        assertEquals(notification.getMentorId(), result.getMentorId());
//        assertEquals(notification.getIsRead(), result.getIsRead());
//        assertEquals(notification.getReadAt(), result.getReadAt());
//        assertEquals(notification.getCreatedAt(), result.getCreatedAt());
//        assertEquals(notification.getUpdatedAt(), result.getUpdatedAt());
//
//        verify(notificationRepository).findByIsRead(isRead);
//    }
//
//    @Test
//    void getAllNotificationsByIsRead_shouldReturnEmptyList_whenNoNotificationsExist() throws UnexpectedServerException {
//        Boolean isRead = false;
//
//        when(notificationRepository.findByIsRead(isRead)).thenReturn(List.of());
//
//        CommonResponse<List<Notification>> response = notificationService.getAllNotificationsByIsRead(isRead);
//
//        assertNotNull(response);
//        assertEquals(STATUS_TRUE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(NO_NOTIFICATIONS_AVAILABLE, response.getMessage());
//        assertNotNull(response.getData());
//        assertTrue(response.getData().isEmpty());
//
//        verify(notificationRepository).findByIsRead(isRead);
//    }
//
//    @Test
//    void getAllNotificationsByIsRead_shouldThrowException_whenServerErrorOccurs() {
//        Boolean isRead = false;
//
//        when(notificationRepository.findByIsRead(isRead)).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class, () ->
//                notificationService.getAllNotificationsByIsRead(isRead));
//
//        assertTrue(ex.getMessage().contains(ERROR_FETCHING_NOTIFICATIONS));
//        assertTrue(ex.getMessage().contains("Database error"));
//
//        verify(notificationRepository).findByIsRead(isRead);
//    }
//
//    @Test
//    void updateNotificationsAsRead_shouldUpdateNotificationWhenNotRead() throws ResourceNotFoundException, UnexpectedServerException {
//        Long id = 1L;
//        Notification notification = Notification.builder()
//                .id(id)
//                .title("Reminder")
//                .message("Mentor session at 5 PM")
//                .recipientId(100L)
//                .recipientName("Alice")
//                .mentorId(200L)
//                .isRead(false)
//                .readAt(null)
//                .build();
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setUpdatedAt(LocalDateTime.now());
//
//        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
//        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
//
//        CommonResponse<Notification> response = notificationService.updateNotificationsAsRead(id);
//
//        assertEquals(SUCCESSFULLY_UPDATED_AS_READ, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertNotNull(response.getData());
//
//        Notification updatedNotification = response.getData();
//
//        assertEquals(id, updatedNotification.getId());
//        assertEquals("Reminder", updatedNotification.getTitle());
//        assertEquals("Mentor session at 5 PM", updatedNotification.getMessage());
//        assertEquals(100L, updatedNotification.getRecipientId());
//        assertEquals("Alice", updatedNotification.getRecipientName());
//        assertEquals(200L, updatedNotification.getMentorId());
//        assertTrue(updatedNotification.getIsRead());
//        assertNotNull(updatedNotification.getReadAt());
//        assertNotNull(updatedNotification.getCreatedAt());
//        assertNotNull(updatedNotification.getUpdatedAt());
//
//        verify(notificationRepository).findById(id);
//        verify(notificationRepository).save(any(Notification.class));
//    }
//
//
//    @Test
//    void updateNotificationsAsRead_shouldReturnAlreadyMarkedWhenNotificationIsAlreadyRead() throws ResourceNotFoundException, UnexpectedServerException {
//        Long id = 1L;
//        Notification notification = Notification.builder()
//                .id(id)
//                .title("Reminder")
//                .message("Mentor session at 5 PM")
//                .recipientId(100L)
//                .recipientName("Alice")
//                .mentorId(200L)
//                .isRead(true)
//                .readAt(LocalDateTime.now())
//                .build();
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setUpdatedAt(LocalDateTime.now());
//
//        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
//
//        CommonResponse<Notification> response = notificationService.updateNotificationsAsRead(id);
//
//        assertEquals(ALREADY_MARKED_AS_TRUE, response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertNull(response.getData());
//
//        verify(notificationRepository).findById(id);
//    }
//
//    @Test
//    void updateNotificationsAsRead_shouldThrowExceptionWhenNotificationNotFound() {
//        Long id = 99L;
//
//        when(notificationRepository.findById(id)).thenReturn(Optional.empty());
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
//                notificationService.updateNotificationsAsRead(id));
//
//        assertEquals(NOTIFICATION_NOT_FOUND_BY_ID + id, ex.getMessage());
//
//        verify(notificationRepository).findById(id);
//    }
//
//    @Test
//    void updateNotificationsAsRead_shouldThrowExceptionWhenServerErrorOccurs() {
//        Long id = 1L;
//
//        Notification notification = Notification.builder()
//                .id(id)
//                .title("Reminder")
//                .message("Mentor session at 5 PM")
//                .recipientId(100L)
//                .recipientName("Alice")
//                .mentorId(200L)
//                .isRead(false)
//                .readAt(null)
//                .build();
//
//        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
//        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException ex = assertThrows(UnexpectedServerException.class, () ->
//                notificationService.updateNotificationsAsRead(id));
//
//        assertTrue(ex.getMessage().contains(ERROR_UPDATING_NOTIFICATION_AS_READ));
//        assertTrue(ex.getMessage().contains("Database error"));
//
//        verify(notificationRepository).findById(id);
//        verify(notificationRepository).save(any(Notification.class));
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
//
//
//
//}
