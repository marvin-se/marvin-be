package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findTopByConversationIdOrderBySentAtDesc(Long conversationId);

    @Query("""
        SELECT m
        FROM Message m
        WHERE m.id IN (
            SELECT MAX(m2.id)
            FROM Message m2
            WHERE m2.conversation.id IN :conversationIds
            GROUP BY m2.conversation.id
        )
        """)
    List<Message> findLastMessagesForConversations(@Param("conversationIds") List<Long> ids);

}

