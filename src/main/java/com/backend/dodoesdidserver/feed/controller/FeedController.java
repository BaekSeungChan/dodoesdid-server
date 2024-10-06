package com.backend.dodoesdidserver.feed.controller;

import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.feed.dto.response.FeedDetailResponseDto;
import com.backend.dodoesdidserver.feed.dto.response.FeedResponseDTO;
import com.backend.dodoesdidserver.feed.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Tag(name = "Feed API", description = "피드 관련 API")
public class FeedController {

    private final FeedService feedService;

    @Operation(summary = "피드 목록 조회", description = "사용자의 피드 목록을 조회합니다.")
    @GetMapping
    public ApiCustomResponse<List<FeedResponseDTO>> findByFeed(
            @Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        List<FeedResponseDTO> feedResponseDTOList = feedService.findAllFeed(email);

        if(feedResponseDTOList.isEmpty()){
            return ApiCustomResponse.OK("피드가 존재하지 않습니다.");
        }

        return ApiCustomResponse.OK(feedResponseDTOList, "피드 조회");
    }

    @Operation(summary = "피드 상세 조회", description = "특정 다짐 콘텐츠의 상세 정보를 조회합니다.")
    @GetMapping("/{dazimContentId}")
    public ApiCustomResponse<FeedDetailResponseDto> getDetailFeed(
            @Parameter(description = "현재 로그인된 사용자 정보")  @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "조회할 다짐 콘텐츠 ID", example = "1") @PathVariable("dazimContentId") Long dazimContentId){

        if(userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        FeedDetailResponseDto feedDetailResponseDto =  feedService.findById(email, dazimContentId);

        return ApiCustomResponse.OK(feedDetailResponseDto, "피드 상세 조회");
    }
}
