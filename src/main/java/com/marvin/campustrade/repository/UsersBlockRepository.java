package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.entity.UsersBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersBlockRepository extends JpaRepository<UsersBlock, Long>{
    Optional<UsersBlock> findByBlockerAndBlocked(Users blocker, Users blocked);
}
