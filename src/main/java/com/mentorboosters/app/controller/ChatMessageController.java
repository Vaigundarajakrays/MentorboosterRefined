package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.ChatMessage;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.ChatMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService){this.chatMessageService=chatMessageService;}

    @PostMapping("/saveMessage")
    public CommonResponse<ChatMessage> saveMessage(@RequestBody ChatMessage chatMessage) throws UnexpectedServerException {
        return chatMessageService.saveMessage(chatMessage);
    }

    @GetMapping("/getChatHistory/{senderId}/{recipientId}")
    public CommonResponse<List<ChatMessage>> getChatHistory(@PathVariable Long senderId, @PathVariable Long recipientId) throws UnexpectedServerException {
        return chatMessageService.getChatHistory(senderId, recipientId);
    }

    @GetMapping("/getUnreadMessages/{senderId}/{recipientId}")
    public CommonResponse<List<ChatMessage>> getUnreadMessages(@PathVariable Long senderId, @PathVariable Long recipientId) throws UnexpectedServerException {
        return chatMessageService.getUnreadMessages(senderId, recipientId);
    }

    @PutMapping("/markMessageAsRead/{senderId}/{recipientId}")
    public CommonResponse<String> markMessagesAsRead(@PathVariable Long senderId, @PathVariable Long recipientId) throws UnexpectedServerException {
        return chatMessageService.markMessagesAsRead(senderId, recipientId);
    }
}
