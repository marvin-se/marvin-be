package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="images")
public class imageEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long product_id;

    @Column(name = "image_url", nullable = false)
    private String image_url;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;



}