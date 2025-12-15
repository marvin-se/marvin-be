package com.marvin.campustrade.repository;


import com.marvin.campustrade.constants.Category;
import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ProductSearchRepository extends JpaRepository<Product, Long> {
    @Query("""
            SELECT p FROM Product p
            LEFT JOIN p.user u
            LEFT JOIN u.university univ
            WHERE p.status = :status
            AND (
                    :keyword IS NULL 
                    OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND (:category IS NULL OR p.category = :category)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:universityId IS NULL OR univ.id = :universityId)
            """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("universityId") Long universityId,
            @Param("status") Status status,
            Pageable pageable
    );


    @Query("""
            SELECT COUNT(DISTINCT p) FROM Product p
            LEFT JOIN p.user u
            WHERE p.status = :status
            AND (
                :keyword IS NULL 
                OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND (:category IS NULL OR p.category = :category)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:universityId IS NULL OR u.university.id = :universityId)
            """)
    Long countSearchResults(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("universityId") Long universityId,
            @Param("status") Status status
    );



}
