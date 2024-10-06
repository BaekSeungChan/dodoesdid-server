package com.backend.dodoesdidserver.user.entity;

import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.home.domain.Dazim;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_pwd")
    private String password;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_nickname")
    private String nickname;

    @Column(name = "user_birth")
    private String birth;

    @Column(name = "user_phone")
    private String phone;

    @Column(name = "user_role")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserImage> userImages = new ArrayList<>();

    public void passwordEncode(BCryptPasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(this.password);
        }

    public void updatePassword(String newPassword, BCryptPasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(newPassword);
        }

    public void updateNickname(String nickname) {
            this.nickname = nickname;
        }
 }


