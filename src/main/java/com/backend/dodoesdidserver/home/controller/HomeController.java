package com.backend.dodoesdidserver.home.controller;

import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.group.repository.GroupRepository;
import com.backend.dodoesdidserver.group.service.GroupService;
import com.backend.dodoesdidserver.home.dto.request.DazimContentRequest;
import com.backend.dodoesdidserver.home.dto.request.DazimImageRequest;
import com.backend.dodoesdidserver.home.dto.request.DazimResponseDto;
import com.backend.dodoesdidserver.home.dto.response.DazimHomeResponse;
import com.backend.dodoesdidserver.home.dto.response.DazimRequestResponse;
import com.backend.dodoesdidserver.home.dto.response.HomeGroupReponse;
import com.backend.dodoesdidserver.home.dto.response.HomeResponseDto;
import com.backend.dodoesdidserver.home.service.DazimService;
import com.backend.dodoesdidserver.user.dto.response.UserDefaultResponse;
import com.backend.dodoesdidserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "Home API", description = "홈 화면 및 다짐 관련 API")
public class HomeController {

    private final DazimService dazimService;
    private final GroupService groupService;
    private final UserService userService;

    @Operation(summary = "다짐 글 작성", description = "팀 내 다짐 글을 작성합니다.")
    @PostMapping("/{teamName}/dazim")
    public ApiCustomResponse<String> writeDazimContent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("teamName") String groupName,
            @RequestBody DazimContentRequest request){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();
        dazimService.writeDazimContent(request, email, groupName);

        return ApiCustomResponse.OK(request.getDazimContent(),"다짐글 작성 완료");
    }

    @Operation(summary = "메인 홈 그룹 조회", description = "사용자의 그룹 목록을 조회합니다.")
    @GetMapping
    public ApiCustomResponse getMainHome(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String date
    ){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        List<HomeResponseDto> homeGroups= groupService.findAllGroup(email);

        UserDefaultResponse userDefaultResponse = userService.getUserDefaultResponseByEmail(email);

        if(homeGroups.isEmpty()){
            return ApiCustomResponse.OK(userDefaultResponse ,"그룹이 없습니다.");
        }

        Long groupId = homeGroups.get(0).getGroupId();
        LocalDate requestDate;

        if (date == null) {
            requestDate = LocalDate.now();
        } else {
            try {
                requestDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new ApiException(ErrorCode.INVALID_DATE_ERROR);
            }
        }

        List<DazimHomeResponse> groupMembers = groupService.findByGroupMember(groupId, requestDate);

        return ApiCustomResponse.OK(new HomeGroupReponse(homeGroups, groupMembers), "그룹 조회");
    }

    @Operation(summary = "그룹 멤버 조회", description = "특정 그룹의 그룹원 목록을 조회합니다.")
    @GetMapping("/group")
    public ApiCustomResponse<List<DazimHomeResponse>> getfindGroupMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("groupid") Long groupId,
            @RequestParam(required = false) String date
            ){

        if(userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        LocalDate requestDate;

        if(date == null){
            requestDate = LocalDate.now();
        } else {
            try{
                requestDate = LocalDate.parse(date);
            } catch (DateTimeParseException e){
                throw new ApiException(ErrorCode.INVALID_DATE_ERROR);
            }
        }

        List<DazimHomeResponse> groupMembers = groupService.findByGroupMember(groupId, requestDate);

        return ApiCustomResponse.OK(groupMembers, "그룹별 그룹원 조회 성공");
    }

    @Operation(summary = "다짐 이미지 업로드", description = "다짐에 이미지를 업로드합니다.")
    @PostMapping("/{dazimId}/{groupMemberId}/dazim/image")
    public ApiCustomResponse uploadDazimImage(
            @ModelAttribute DazimImageRequest dazimImageRequest,
            @Parameter(description = "그룹 멤버 ID") @PathVariable("groupMemberId") Long groupMemberId,
            @Parameter(description = "다짐 ID") @PathVariable("dazimId") Long dazimId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if(userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        dazimService.uploadDzaimImage(dazimImageRequest, dazimId ,groupMemberId);

        return ApiCustomResponse.OK("이미지 업로드 완료");
    }

    @Operation(summary = "다짐 좋아요", description = "특정 다짐에 좋아요를 누릅니다.")
    @PostMapping("/{dazimId}/doeslike")
    public ApiCustomResponse doesLikeDazim(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "좋아요를 누를 다짐 ID") @PathVariable("dazimId") Long dazimId,
            @Parameter(description = "좋아요 번호 (1~5)", example = "1") @RequestParam int dodoesLikeNum
    ){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        dazimService.dodoesPresssButton(dazimId, email, dodoesLikeNum);

        return ApiCustomResponse.OK("좋아요 성공");
    }

}
