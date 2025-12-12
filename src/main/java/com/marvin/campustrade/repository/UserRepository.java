package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    // To fix University's lazy loading
    @Query("""
        SELECT u FROM Users u
        LEFT JOIN FETCH u.university
        WHERE u.email = :email
    """)
    Optional<Users> findByEmailWithUniversity(String email);
}
