package com.marvin.campustrade.repository;

import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.entity.UsersBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UsersBlockRepository extends JpaRepository<UsersBlock, Long>{
    Optional<UsersBlock> findByBlockerAndBlocked(Users blocker, Users blocked);
    @Query("""
        select ub.blocked.id
        from UsersBlock ub
        where ub.blocker = :user
    """)
    Set<Long> findBlockedUserIds(@Param("user") Users user);

    @Query("""
        select ub.blocker.id
        from UsersBlock ub
        where ub.blocked = :user
    """)
    Set<Long> findUsersWhoBlockedMeIds(@Param("user") Users user);

    void deleteAllByBlocker(Users blocker);

    void deleteAllByBlocked(Users blocked);



}
