package com.backend.dodoesdidserver.home.dto.response;


import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DazimHomeResponse {

    private String nickName;
    private String dazimContent;
    private List<String> dazimImages;
    private String userProfileImage;
    private Long dazimId;
    private Long groupMemberId;
    private String dodesSuccessCheck;

    public DazimHomeResponse(String nickname, String userProfileImage, String dazimContent, String dazimImage, Long dazimId,Long groupMember, String dodesSuccessImage) {
        this.nickName = nickname;
        this.userProfileImage = userProfileImage;
        this.dazimContent = dazimContent;
        this.dazimImages = (dazimImage != null) ? List.of(dazimImage) : null;
        this.dazimId = dazimId;
        this.groupMemberId = groupMember;
        this.dodesSuccessCheck = dodesSuccessImage;
    }

}
