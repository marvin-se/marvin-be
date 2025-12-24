package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findByProduct(Product product);
    List<Image> findAllByProductIn(List<Product> products);

    void deleteAllByProduct(Product product);
}
