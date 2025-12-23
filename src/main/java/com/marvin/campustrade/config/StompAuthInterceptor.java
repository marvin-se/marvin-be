package com.marvin.campustrade.config;

import com.marvin.campustrade.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AccessDeniedException("Missing Authorization header");
            }

            String token = authHeader.substring(7);

            Authentication auth = jwtUtils.getAuthentication(token);
            accessor.setUser(auth);
        }

        return message;
    }
}
