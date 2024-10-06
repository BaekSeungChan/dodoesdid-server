package com.backend.dodoesdidserver.group.contoller;

import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.group.dto.request.GroupImageUpdateDto;
import com.backend.dodoesdidserver.group.dto.request.GroupNameRequestDto;
import com.backend.dodoesdidserver.group.dto.request.GroupNoticeDto;
import com.backend.dodoesdidserver.group.dto.request.GroupRequestDto;
import com.backend.dodoesdidserver.group.dto.response.GroupMemberListResponseDto;
import com.backend.dodoesdidserver.group.dto.response.GroupResponseDto;
import com.backend.dodoesdidserver.group.dto.response.UserGroupListResponseDto;
import com.backend.dodoesdidserver.group.service.GroupService;
import com.backend.dodoesdidserver.user.dto.UserGroupDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "Group API", description = "그룹 관련 API")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping
    public ApiCustomResponse createGroup(
            @Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute GroupRequestDto groupRequestDto){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();
        GroupResponseDto groupResponseDto = groupService.createGroup(email, groupRequestDto);

        return ApiCustomResponse.OK(groupResponseDto,"그룹 생성 성공");
    }

    @Operation(summary = "그룹 참여", description = "기존 그룹에 참여합니다.")
    @PostMapping("/{groupName}")
    public ApiCustomResponse joinGroup(
            @Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "참여할 그룹의 이름", example = "최강공부") @PathVariable("groupName") String groupName){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        groupService.joinGroup(email, groupName);

        return ApiCustomResponse.OK("그룹 참여 성공");
    }

    @Operation(summary = "마이페이지 그룹관리 유저가 속한 그룹 조회", description = "유저가 속한 그룹을 조회합니다.")
    @GetMapping
    public ApiCustomResponse userGroupList(@Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        List<UserGroupListResponseDto> groupList = groupService.getUserGroupList(userDetails.getUsername());
        return ApiCustomResponse.OK(groupList, "유저가 속한 그룹의 리스트 입니다.");
    }

    @Operation(summary = "그룹관리 사용자가 속한 그룹 상세정보", description = "사용자가 속한 그룹의 상세정보 조회.")
    @GetMapping("/mypage/{groupId}")
    public ApiCustomResponse userGroupDetail(@Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails, @PathVariable("groupId") Long groupId){
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        UserGroupDetailDto groupDetail = groupService.getUserGroupDetail(groupId);
        return ApiCustomResponse.OK(groupDetail, "사용자가 속한 그룹의 상세정보 입니다.");
    }

    @Operation(summary = "그룹 이름 변경", description = "그룹 이름을 변경합니다.")
    @PutMapping("/mypage")
    public ApiCustomResponse changeGroupName(@Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails, @RequestBody GroupNameRequestDto dto){
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        groupService.upadateGroupName(dto);
        groupService.getUserGroupDetail(dto.getGroupId());
        return ApiCustomResponse.OK("그룹 이름 변경에 성공하였습니다.");
    }

    @Operation(summary = "그룹 탈퇴", description = "그룹에서 탈퇴합니다.")
    @DeleteMapping("/{groupId}")
    public ApiCustomResponse exitGroup(@Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails, @PathVariable("groupId") Long groupId) {
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        groupService.exitGroup(userDetails.getUsername(), groupId);
        return ApiCustomResponse.OK("그룹 탈퇴에 성공했습니다.");
    }

    @Operation(summary = "그룹 공지사항 등록", description = "그룹 공지사항을 등록합니다.")
    @PutMapping("/notice")
    public ApiCustomResponse notice(@Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails, @RequestBody GroupNoticeDto groupNoticeDto) {
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        groupService.uploadGroupNotice(groupNoticeDto);
        return ApiCustomResponse.OK("공지사항 등록에 성공하였습니다.");
    }

    @Operation(summary = "그룹 이미지 수정", description = "그룹 이미지를 수정합니다.")
    @PutMapping("/image")
    public ApiCustomResponse updateGroupImage(@Parameter(description = "현재 로그인된 사용자 정보") @AuthenticationPrincipal UserDetails userDetails, @RequestBody GroupImageUpdateDto dto){
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        groupService.updateGroupImage(dto);
        return ApiCustomResponse.OK("그룹 이미지를 수정하였습니다.");
    }


}
