package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findTopByConversationIdOrderBySentAtDesc(Long conversationId);
}

