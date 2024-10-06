package com.backend.dodoesdidserver.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class S3ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    public String upload(MultipartFile image){
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new ApiException(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image);
    }

    public String uploadImage(MultipartFile image){
        this.validateImageFileExtenstion(image.getOriginalFilename());
        try{
            return this.uploadImageToS3(image);
        } catch (IOException e){
            throw new ApiException(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    public void validateImageFileExtenstion(String filename){
        int lastDotIndex = filename.lastIndexOf(".");
        if(lastDotIndex == -1){
            throw new ApiException(ErrorCode.NO_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex  + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if(!allowedExtentionList.contains(extension)){
            throw new ApiException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename(); // 원본 파일 명
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFileName; // 변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }
}
