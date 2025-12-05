package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite,Long>{

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Favourite> findByUserIdAndProductId(Long userId, Long productId);

    List<Favourite> findAllByUserId(Long userId);
}