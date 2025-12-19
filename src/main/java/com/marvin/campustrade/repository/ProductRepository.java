package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByUserId(Long userId);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.favouriteCount = p.favouriteCount + 1
        WHERE p.id = :productId
    """)
    void incrementFavouriteCount(Long productId);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.favouriteCount = p.favouriteCount - 1
        WHERE p.id = :productId
          AND p.favouriteCount > 0
    """)
    void decrementFavouriteCount(Long productId);
}
