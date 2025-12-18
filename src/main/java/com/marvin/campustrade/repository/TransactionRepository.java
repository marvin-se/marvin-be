package com.marvin.campustrade.repository;

import com.marvin.campustrade.data.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transactions,Long> {
    @Query("""
       SELECT t FROM Transactions t
       JOIN FETCH t.product
       JOIN FETCH t.buyer
       JOIN FETCH t.seller
       WHERE t.seller.id = :sellerId
    """)
    Optional<List<Transactions>> findTransactionBySellerId(@Param("sellerId") Long sellerId);

    @Query("""
        SELECT t FROM Transactions t
        JOIN FETCH t.product
        JOIN FETCH t.buyer
        JOIN FETCH t.seller
        WHERE t.buyer.id = :buyerId
    """)
    Optional<List<Transactions>> findTransactionByBuyerId(@Param("buyerId") Long buyerId);

}
