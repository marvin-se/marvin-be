package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findByProduct(Product product);
    List<Image> findAllByProductIn(List<Product> products);

    void deleteAllByProduct(Product product);

    Optional<Image> findByProductAndImageUrl(Product product, String imageUrl);

    long countByProduct(Product product);
}
