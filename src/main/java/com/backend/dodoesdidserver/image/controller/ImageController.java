package com.backend.dodoesdidserver.image.controller;

import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.image.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3ImageService s3ImageService;

    @PostMapping("/s3/upload")
    public ApiCustomResponse s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String profileImage = s3ImageService.upload(image);
        return ApiCustomResponse.OK(profileImage,"이미지 업로드 되었습니다.");
    }
}
