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
    Optional<Conversation> findByUser1_IdAndUser2_IdOrUser2_IdAndUser1_Id(Long user1Id, Long user2Id, Long user1IdOther, Long user2IdOther);

    @Query("""
        SELECT c FROM Conversation c
        WHERE (c.user1 = :u1 AND c.user2 = :u2)
           OR (c.user1 = :u2 AND c.user2 = :u1)
    """)
    Optional<Conversation> findConversationBetween(
            @Param("u1") Users user1,
            @Param("u2") Users user2
    );

    @Query("""
SELECT c FROM Conversation c
WHERE c.product.id = :productId
AND (
     (c.user1.id = :userA AND c.user2.id = :userB)
  OR (c.user1.id = :userB AND c.user2.id = :userA)
)
""")
    Optional<Conversation> findByUsersAndProduct(
            @Param("userA") Long userA,
            @Param("userB") Long userB,
            @Param("productId") Long productId
    );


}

