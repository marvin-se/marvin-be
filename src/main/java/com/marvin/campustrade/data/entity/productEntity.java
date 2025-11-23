package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class productEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;


    @Column(name = "price")
    private int price;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private java.sql.Timestamp created_at;

    @Column(name = "updated_at")
    private java.sql.Timestamp updated_at;

    @Column(name = "created_by")
    private int created_by;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @OneToMany(mappedBy = "product")
    private List<ImageEntity> images;

    @OneToMany(mappedBy = "product")
    private List<MessageEntity> messages;

    @OneToMany(mappedBy = "product")
    private List<FavouriteEntity> favourites;





}