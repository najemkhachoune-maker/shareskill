package com.example.chatapp.controller;

import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.ChatNotification;
import com.example.chatapp.service.ChatMessageService;
import com.example.chatapp.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

        private final SimpMessagingTemplate messagingTemplate;
        private final ChatMessageService chatMessageService;
        private final ChatRoomService chatRoomService;

        @GetMapping("/chat/health")
        @org.springframework.web.bind.annotation.ResponseBody
        public String health() {
                return "Chat Service is up and running";
        }

        @MessageMapping("/chat")
        public void processMessage(@Payload ChatMessage chatMessage) {
                ChatMessage savedMsg = chatMessageService.save(chatMessage);
                // Send to specific user
                messagingTemplate.convertAndSendToUser(
                                chatMessage.getRecipientId(), "/queue/messages",
                                new ChatNotification(
                                                savedMsg.getId().toString(),
                                                savedMsg.getSenderId(),
                                                savedMsg.getRecipientId(),
                                                savedMsg.getContent()));

                // DEBUG: Broadcast to public topic to verify connectivity
                messagingTemplate.convertAndSend("/topic/public", savedMsg);
        }

        @GetMapping("/messages/{senderId}/{recipientId}")
        public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                        @PathVariable String recipientId) {
                return ResponseEntity
                                .ok(chatMessageService.findChatMessages(senderId, recipientId));
        }
}
