package com.backend.dodoesdidserver.user.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userimage")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userimage_id", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "userimage_origin_name")
    private String imageOriginName;

    @Column(name = "userimage_path")
    private String imagePath;

}
