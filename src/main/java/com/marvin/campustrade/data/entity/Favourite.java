package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Setter;

@Entity
@Table(name = "favourite", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
        },
        indexes = {
                @Index(columnList = "user_id"),
                @Index(columnList = "product_id")
        })
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 🔥 JPA için ZORUNLU

@AllArgsConstructor
@Builder
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
