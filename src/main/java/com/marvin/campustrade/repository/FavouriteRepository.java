package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.entity.Product;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FavouriteRepository extends JpaRepository<Favourite,Long> {
    @Query("""
        select f.product.id
        from Favourite f
        where f.user.id = :userId
    """)
    Set<Long> findFavouriteProductIdsByUserId(Long userId);

    boolean existsByUserAndProduct(Users user, Product product);

    List<Favourite> findAllByUser(Users user);

    Optional<Favourite> findByUserAndProductId(Users user, Long productId);
}