package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUser1_IdOrUser2_Id(Long user1Id, Long user2Id);


    @Query("""
        SELECT c FROM Conversation c
        WHERE c.product.id = :productId
        AND (
       
        (c.user1.id = :user1Id AND c.user2.id = :user2Id)
        OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)
        )
        """)
    Optional<Conversation> findByUsersAndProduct(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id,
            @Param("productId") Long productId
    );


}

