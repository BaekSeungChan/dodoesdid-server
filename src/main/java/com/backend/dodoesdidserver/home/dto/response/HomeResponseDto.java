package com.backend.dodoesdidserver.home.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class HomeResponseDto {
    // 이미지 // 다짐 글 // 내가 속한 팀들
    private Long groupId;
    private String groupImage;
    private String groupName;

    @Builder
    public HomeResponseDto(Long groupId, String groupImage, String groupName){
        this.groupId = groupId;
        this.groupImage = groupImage;
        this.groupName = groupName;
    }
}
