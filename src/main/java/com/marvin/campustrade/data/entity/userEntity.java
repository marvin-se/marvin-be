package com.marvin.campustrade.data.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class userEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name")
    private String full_name;


    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_pic_url")
    private String profile_pic_url;

    @Column(name = "password_hash", nullable = false)
    private String password_hash;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "university", nullable = false)
    private int university;

    @Column(name = "created_at")
    private Java.sql.Timestamp created_at;

    @Column(name = "is_active")
    private Boolean is_active;

    @ManyToOne
    @JoinColumn(name = "university", nullable = false)
    private UniversityEntity university;

    @OneToMany(mappedBy = "createdBy")
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "sender")
    private List<MessageEntity> sentMessages;

    @OneToMany(mappedBy = "receiver")
    private List<MessageEntity> receivedMessages;

    @OneToMany(mappedBy = "user")
    private List<FavouriteEntity> favourites;




}