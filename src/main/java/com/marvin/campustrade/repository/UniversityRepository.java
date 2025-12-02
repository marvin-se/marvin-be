package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.University;
import com.marvin.campustrade.data.entity.Users;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByName(String name);
}
