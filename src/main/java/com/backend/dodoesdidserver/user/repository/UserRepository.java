package com.backend.dodoesdidserver.user.repository;

import com.backend.dodoesdidserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String username);
    User findByPhone(String phoneNumber);

    boolean existsByNickname(String userNickname);

    void deleteByEmail(String userEmail);
}
