package com.backend.dodoesdidserver.user.repository;

import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserImageRepository  extends JpaRepository<UserImage, Long> {

    UserImage findByUser(User user);

    @Query("SELECT ui.imagePath FROM UserImage ui WHERE ui.user = :user")
    String findImagePathByUser(@Param("user") User user);

}

