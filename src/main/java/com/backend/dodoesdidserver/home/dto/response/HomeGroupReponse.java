package com.backend.dodoesdidserver.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeGroupReponse {

    private List<HomeResponseDto> groupNames;
    private List<DazimHomeResponse> groupMembers;
}
