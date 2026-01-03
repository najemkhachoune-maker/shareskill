package com.example.chatapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final com.example.chatapp.util.JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(
            org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message,
                    org.springframework.messaging.MessageChannel channel) {
                org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = org.springframework.messaging.support.MessageHeaderAccessor
                        .getAccessor(message, org.springframework.messaging.simp.stomp.StompHeaderAccessor.class);

                if (org.springframework.messaging.simp.stomp.StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        try {
                            if (jwtUtil.validateToken(token)) {
                                io.jsonwebtoken.Claims claims = jwtUtil.extractAllClaims(token);
                                String userId = claims.get("userId", String.class);
                                String username = claims.getSubject();

                                // Create a Principal using userId as the Name (crucial for
                                // convertAndSendToUser)
                                java.security.Principal principal = new java.security.Principal() {
                                    @Override
                                    public String getName() {
                                        return userId; // This MUST match the recipientId sent in messages
                                    }
                                };

                                accessor.setUser(principal);
                                log.info("Authenticated WebSocket connection for user: {} ({})", username, userId);
                            }
                        } catch (Exception e) {
                            log.error("WebSocket Token validation failed: {}", e.getMessage());
                        }
                    } else {
                        log.warn("WebSocket connection attempt without valid Authorization header");
                    }
                }
                return message;
            }
        });
    }
}
