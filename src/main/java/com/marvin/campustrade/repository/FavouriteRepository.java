package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.entity.Product;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite,Long> {

    boolean existsByUserAndProduct(Users user, Product product);

    List<Favourite> findAllByUser(Users user);

    Optional<Favourite> findByUserAndProductId(Users user, Long productId);
}