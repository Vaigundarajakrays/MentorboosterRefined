package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.ChatMessage;
import com.mentorboosters.app.repository.ChatMessageRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.mentorboosters.app.util.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageControllerTest {

    @Mock
    ChatMessageRepository chatMessageRepository;

    @InjectMocks
    ChatMessageService chatMessageService;

    private ChatMessage msg1, msg2;

    @BeforeEach
    void setup(){
        LocalDateTime now = LocalDateTime.now();
        msg1 = ChatMessage.builder()
                .id(1L)
                .senderId(1L)
                .recipientId(2L)
                .content("Hello")
                .read(false)
                .build();
        msg1.setCreatedAt(now);
        msg1.setUpdatedAt(now);

        msg2 = ChatMessage.builder()
                .id(2L)
                .senderId(1L)
                .recipientId(2L)
                .content("How are you?")
                .read(true)
                .build();
        msg2.setCreatedAt(now);
        msg2.setUpdatedAt(now);
    }

    @Test
    void saveMessage_shouldReturnSavedMessageSuccessfully_withoutAssertAll() throws UnexpectedServerException {
        // Arrange
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(1L)
                .recipientId(2L)
                .content("Hello!")
                .build();

        ChatMessage savedMessage = ChatMessage.builder()
                .id(1L)
                .senderId(1L)
                .recipientId(2L)
                .content("Hello!")
                .read(false)
                .build();
        savedMessage.setCreatedAt(LocalDateTime.now());
        savedMessage.setUpdatedAt(LocalDateTime.now());

        when(chatMessageRepository.save(chatMessage)).thenReturn(savedMessage);

        // Act
        CommonResponse<ChatMessage> response = chatMessageService.saveMessage(chatMessage);
        ChatMessage result = response.getData();

        // Assert
        assertNotNull(response);
        assertTrue(response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());

        assertNotNull(result);
        assertEquals(savedMessage.getId(), result.getId());
        assertEquals(savedMessage.getSenderId(), result.getSenderId());
        assertEquals(savedMessage.getRecipientId(), result.getRecipientId());
        assertEquals(savedMessage.getContent(), result.getContent());
        assertEquals(savedMessage.isRead(), result.isRead());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(chatMessageRepository).save(chatMessage);
    }

    @Test
    void saveMessage_shouldThrowUnexpectedServerException_whenRepositoryFails() {
        // Arrange
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(1L)
                .recipientId(2L)
                .content("Hello!")
                .build();

        when(chatMessageRepository.save(chatMessage))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
            chatMessageService.saveMessage(chatMessage);
        });

        assertTrue(exception.getMessage().contains(ERROR_ADDING_CHAT_MESSAGE));

        verify(chatMessageRepository).save(chatMessage);
    }

    @Test
    void getChatHistory_shouldReturnMessages_whenMessagesExist() throws UnexpectedServerException {

        Long senderId = 1L;
        Long recipientId = 2L;

        List<ChatMessage> chatMessages = List.of(msg1, msg2);

        when(chatMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(chatMessages);

        CommonResponse<List<ChatMessage>> response = chatMessageService.getChatHistory(senderId, recipientId);

        // Assert
        assertNotNull(response);
        assertEquals(STATUS_TRUE, response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(LOADED_ALL_CHAT_MESSAGES, response.getMessage());
        assertEquals(2, response.getData().size());

        ChatMessage returnedMsg1 = response.getData().get(0);
        ChatMessage returnedMsg2 = response.getData().get(1);


        assertEquals(msg1.getId(), returnedMsg1.getId());
        assertEquals(msg1.getSenderId(), returnedMsg1.getSenderId());
        assertEquals(msg1.getRecipientId(), returnedMsg1.getRecipientId());
        assertEquals(msg1.getContent(), returnedMsg1.getContent());
        assertEquals(msg1.isRead(), returnedMsg1.isRead());
        assertEquals(msg1.getCreatedAt(), returnedMsg1.getCreatedAt());
        assertEquals(msg1.getUpdatedAt(), returnedMsg1.getUpdatedAt());

        assertEquals(msg2.getId(), returnedMsg2.getId());
        assertEquals(msg2.getSenderId(), returnedMsg2.getSenderId());
        assertEquals(msg2.getRecipientId(), returnedMsg2.getRecipientId());
        assertEquals(msg2.getContent(), returnedMsg2.getContent());
        assertEquals(msg2.isRead(), returnedMsg2.isRead());
        assertEquals(msg2.getCreatedAt(), returnedMsg2.getCreatedAt());
        assertEquals(msg2.getUpdatedAt(), returnedMsg2.getUpdatedAt());

        verify(chatMessageRepository).findBySenderIdAndRecipientId(senderId, recipientId);
    }

    @Test
    void getChatHistory_shouldReturnEmptyList_whenNoMessagesExist() throws UnexpectedServerException {

        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(List.of());

        CommonResponse<List<ChatMessage>> response = chatMessageService.getChatHistory(senderId, recipientId);

        // Assert
        assertNotNull(response);
        assertEquals(STATUS_TRUE, response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(NO_CHAT_MESSAGES_AVAILABLE, response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());

        verify(chatMessageRepository).findBySenderIdAndRecipientId(senderId, recipientId);
    }

    @Test
    void getChatHistory_shouldThrowUnexpectedServerException_whenRepositoryFails() {

        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenThrow(new RuntimeException("Database error"));

        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
            chatMessageService.getChatHistory(senderId, recipientId);
        });

        assertTrue(exception.getMessage().contains(ERROR_FETCHING_CHAT_MESSAGES));

        verify(chatMessageRepository).findBySenderIdAndRecipientId(senderId, recipientId);
    }

    @Test
    void getUnreadMessages_shouldReturnUnreadMessagesSuccessfully() throws UnexpectedServerException {

        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false))
                .thenReturn(List.of(msg1));

        CommonResponse<List<ChatMessage>> response = chatMessageService.getUnreadMessages(senderId, recipientId);

        // Assert
        assertNotNull(response);
        assertTrue(response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(LOADED_ALL_UNREAD_MESSAGES, response.getMessage());
        assertEquals(1, response.getData().size());

        ChatMessage actual = response.getData().get(0);
        assertEquals(msg1.getId(), actual.getId());
        assertEquals(msg1.getSenderId(), actual.getSenderId());
        assertEquals(msg1.getRecipientId(), actual.getRecipientId());
        assertEquals(msg1.getContent(), actual.getContent());
        assertEquals(msg1.isRead(), actual.isRead());
        assertEquals(msg1.getCreatedAt(), actual.getCreatedAt());
        assertEquals(msg1.getUpdatedAt(), actual.getUpdatedAt());

        verify(chatMessageRepository).findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false);
    }

    @Test
    void getUnreadMessages_shouldReturnEmptyList_whenNoUnreadMessagesExist() throws UnexpectedServerException {

        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false))
                .thenReturn(List.of());

        CommonResponse<List<ChatMessage>> response = chatMessageService.getUnreadMessages(senderId, recipientId);

        // Assert
        assertNotNull(response);
        assertTrue(response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(NO_UNREAD_MESSAGES_AVAILABLE, response.getMessage());
        assertTrue(response.getData().isEmpty());

        verify(chatMessageRepository).findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false);
    }

    @Test
    void getUnreadMessages_shouldThrowUnexpectedServerException_whenRepositoryFails() {

        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false))
                .thenThrow(new RuntimeException("Database error"));

        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () ->
                chatMessageService.getUnreadMessages(senderId, recipientId)
        );

        assertTrue(exception.getMessage().contains(ERROR_FETCHING_UNREAD_MESSAGES));
        verify(chatMessageRepository).findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false);
    }

    @Test
    void markMessagesAsRead_shouldReturnSuccessMessage_whenMessagesMarkedAsRead() throws UnexpectedServerException {
        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.markAllAsRead(senderId, recipientId)).thenReturn(5);

        CommonResponse<String> response = chatMessageService.markMessagesAsRead(senderId, recipientId);

        assertNotNull(response);
        assertTrue(response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(MESSAGES_MARKED_AS_READ, response.getMessage());

        verify(chatMessageRepository).markAllAsRead(senderId, recipientId);
    }

    @Test
    void markMessagesAsRead_shouldReturnNoUnreadMessage_whenUpdatedCountIsZero() throws UnexpectedServerException {
        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.markAllAsRead(senderId, recipientId)).thenReturn(0);

        CommonResponse<String> response = chatMessageService.markMessagesAsRead(senderId, recipientId);

        assertNotNull(response);
        assertTrue(response.getStatus());
        assertEquals(SUCCESS_CODE, response.getStatusCode());
        assertEquals(NO_CHAT_UNREAD_MESSAGES_AVAILABLE, response.getMessage());

        verify(chatMessageRepository).markAllAsRead(senderId, recipientId);
    }

    @Test
    void markMessagesAsRead_shouldThrowUnexpectedServerException_whenRepositoryFails() {
        Long senderId = 1L;
        Long recipientId = 2L;

        when(chatMessageRepository.markAllAsRead(senderId, recipientId))
                .thenThrow(new RuntimeException("DB error"));

        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () ->
                chatMessageService.markMessagesAsRead(senderId, recipientId)
        );

        assertTrue(exception.getMessage().contains(ERROR_MARKING_MESSAGES_AS_READ));
        verify(chatMessageRepository).markAllAsRead(senderId, recipientId);
    }









}
