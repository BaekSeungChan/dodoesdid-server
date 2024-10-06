package com.backend.dodoesdidserver.user.dto.response;

import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.entity.UserImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDefaultResponse {

    private String userNickName;
    private String userImageUrl;
    private String groupName;


    public static UserDefaultResponse toUserDefaultData (User user){
        List<UserImage> userImage = user.getUserImages();
        UserImage storedUserImage = userImage.stream().findFirst().orElse(null);

        String storedImagePath = (storedUserImage != null) ? storedUserImage.getImagePath() : "유저 프로필 이미지가 존재하지 않습니다.";

        return UserDefaultResponse.builder()
                .userNickName(user.getNickname())
                .userImageUrl(storedImagePath)
                .groupName(null)
                .build();
    }
}
