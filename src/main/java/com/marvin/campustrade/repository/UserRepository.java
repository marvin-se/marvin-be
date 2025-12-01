package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

}
