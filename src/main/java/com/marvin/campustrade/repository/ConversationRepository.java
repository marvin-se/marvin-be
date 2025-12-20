package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUser1_IdOrUser2_Id(Long user1Id, Long user2Id);
}

