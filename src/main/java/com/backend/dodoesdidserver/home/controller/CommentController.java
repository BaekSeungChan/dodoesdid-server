package com.backend.dodoesdidserver.home.controller;

import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.home.domain.DazimComment;
import com.backend.dodoesdidserver.home.dto.request.DazimCommentRequest;
import com.backend.dodoesdidserver.home.dto.request.DazimCommentUpdate;
import com.backend.dodoesdidserver.home.dto.request.ReplyCommentRequest;
import com.backend.dodoesdidserver.home.dto.response.DazimDetailCommnent;
import com.backend.dodoesdidserver.home.dto.response.ReplyCommentResponse;
import com.backend.dodoesdidserver.home.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dazim")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "다짐 댓글 및 대댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성", description = "다짐에 대한 댓글을 작성합니다.")
    @PostMapping("/comment")
    public ApiCustomResponse createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DazimCommentRequest dazimCommentRequest
            ){

        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        commentService.createDazimComment(dazimCommentRequest, email);

        return ApiCustomResponse.OK("댓글 작성");
    }



    @Operation(summary = "댓글 목록 조회", description = "특정 다짐에 대한 모든 댓글을 조회합니다.")
    @GetMapping("/{dazimId}/comments")
    public ApiCustomResponse<List<DazimDetailCommnent>> getComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "조회할 다짐 ID")  @PathVariable("dazimId") Long dazimId
    ){
        if(userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        List<DazimDetailCommnent> dazimDetailCommnents = commentService.getCommentsByDazim(email, dazimId);

        return ApiCustomResponse.OK(dazimDetailCommnents, "다짐 댓글 작성자 조회");
    }

    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @PutMapping("/comment/{commentId}")
    public ApiCustomResponse updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("commentId") Long commentId,
            @RequestBody DazimCommentUpdate newContent
    ){
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        commentService.updateDazimComment(commentId, newContent.getNewContent(), email);

        return ApiCustomResponse.OK("댓글 수정 성공");
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @DeleteMapping("/comment/{commentId}")
    public ApiCustomResponse deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("commentId") Long commentId
    ){
        if(userDetails == null){
            throw  new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        commentService.deleteDazimComment(commentId, email);

        return ApiCustomResponse.OK("댓글 삭제 성공");
    }

    @Operation(summary = "대댓글 작성", description = "특정 댓글에 대한 대댓글을 작성합니다.")
    @PostMapping("/comment/{commentId}/reply")
    public ApiCustomResponse createReplyComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "대댓글을 작성할 댓글 ID") @PathVariable("commentId") Long commentId,
            @RequestBody ReplyCommentRequest request
            ){

        if(userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        commentService.createReplyComment(email, commentId, request);

        return ApiCustomResponse.OK("대댓글 작성");
    }

    @Operation(summary = "대댓글 목록 조회", description = "특정 댓글에 대한 대댓글 목록을 조회합니다.")
    @GetMapping("/comment/{commentId}/reply")
    public ApiCustomResponse<List<ReplyCommentResponse>> getReplyComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("commentId") Long commentId
    ){
        if(userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        List<ReplyCommentResponse> dazimDetailCommnents = commentService.getReplyByComments(email, commentId);

        return ApiCustomResponse.OK(dazimDetailCommnents, "다짐 대댓글 조회");
    }

    @Operation(summary = "대댓글 수정", description = "특정 대댓글을 수정합니다.")
    @PutMapping("/comment/reply/{replyId}")
    public ApiCustomResponse updateReplyComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("replyId") Long replyId,
            @RequestBody ReplyCommentRequest newContent
    ){
        if(userDetails == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        commentService.updateReplyComment(replyId, newContent, email);

        return ApiCustomResponse.OK("대댓글 수정 성공");
    }


    @Operation(summary = "대댓글 삭제", description = "특정 대댓글을 삭제합니다.")
    @DeleteMapping("/comment/reply/{replyId}")
    public ApiCustomResponse deleteReplyComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("replyId") Long replyId
    ){
        if(userDetails == null){
            throw  new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        String email = userDetails.getUsername();

        commentService.deleteDazimReplyComment(replyId, email);

        return ApiCustomResponse.OK("대댓글 삭제 성공");
    }
}
