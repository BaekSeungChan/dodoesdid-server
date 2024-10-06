package com.backend.dodoesdidserver.home.dto.request;

import com.backend.dodoesdidserver.home.dto.response.DazimHomeResponse;
import com.backend.dodoesdidserver.user.dto.response.UserDefaultResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DazimResponseDto {

    private UserDefaultResponse userDefaultResponse;
    private List<DazimHomeResponse> groupMembers;
}
