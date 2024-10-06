package com.backend.dodoesdidserver.user.service;

import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.image.service.S3ImageService;
import com.backend.dodoesdidserver.user.dto.ResetPasswordDto;
import com.backend.dodoesdidserver.user.dto.UpdateProfileDto;
import com.backend.dodoesdidserver.user.dto.UserSignUpDto;
import com.backend.dodoesdidserver.user.dto.response.UserDefaultResponse;
import com.backend.dodoesdidserver.user.dto.*;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.entity.UserImage;
import com.backend.dodoesdidserver.user.repository.UserImageRepository;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import com.backend.dodoesdidserver.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final S3ImageService s3ImageService;
    private final UserImageRepository userImageRepository;


    public void signUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByEmail(userSignUpDto.getUserEmail()) != null) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_REGISTERED_ERROR);
        }
        if(userRepository.findByPhone(userSignUpDto.getUserPhone()) != null){
            throw new ApiException(ErrorCode.PHONE_ALREADY_REGISTERED_ERROR);
        }

        User user = User.builder()
                .name(userSignUpDto.getUserName())
                .email(userSignUpDto.getUserEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getUserNickname())
                .birth(userSignUpDto.getUserBirth())
                .phone(userSignUpDto.getUserPhone())
                .role("user")
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);

    }

    public User findUserByPhone(String userPhone) {
        User user = userRepository.findByPhone(userPhone);
        if (user == null) {
            throw new ApiException(ErrorCode.PHONE_NOT_REGISTERED_ERROR);
        }
        return userRepository.findByPhone(userPhone);
    }

    public void resetPassword(ResetPasswordDto dto) {
        String email = redisUtil.getData(dto.getToken());
        if(email != null){
            User user = userRepository.findByEmail(email);
            user.updatePassword(dto.getPassword(), passwordEncoder);
            userRepository.save(user);
            redisUtil.deleteData(dto.getToken());
        }else{
            throw new ApiException(ErrorCode.TOKEN_NOT_EXIST_ERROR);
        }

    }

    @Transactional
    public void updateProfile(String userEmail, UpdateProfileDto dto) {

        User user = userRepository.findByEmail(userEmail);

        if(dto.getImage() != null) {
            if(userImageRepository.findByUser(user) != null){
                userImageRepository.delete(userImageRepository.findByUser(user));
            }

            String originalFileName = dto.getImage().getOriginalFilename();
            String uploadUrl = s3ImageService.upload(dto.getImage());
            UserImage userImage = UserImage.builder().user(user).imagePath(uploadUrl).imageOriginName(originalFileName).build();

            userImageRepository.save(userImage);
            System.err.println(uploadUrl);
        }

        String userNickname = dto.getNickname();
        boolean isExist = userRepository.existsByNickname(userNickname);

        if(isExist){
            throw new ApiException(ErrorCode.NICKNAME_ALREADY_EXIST_ERROR);
        }

        user.updateNickname(dto.getNickname());
        userRepository.save(user);

    }

    public UserDefaultResponse getUserDefaultResponseByEmail (String email){
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        return UserDefaultResponse.toUserDefaultData(user);

    public void updateNickname(String userEmail, String userNickname){
        User user = userRepository.findByEmail(userEmail);
        if(user.getNickname().equals(userNickname)){
            throw new ApiException(ErrorCode.NO_CHANGE_INFORMATION_ERROR);
        }
        boolean isExist = userRepository.existsByNickname(userNickname);
        if(isExist){
            throw new ApiException(ErrorCode.NICKNAME_ALREADY_EXIST_ERROR);
        }
        user.updateNickname(userNickname);
        userRepository.save(user);
    }

    public UserDetailResponseDto getUserDetail(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        String userImage = userImageRepository.findImagePathByUser(user);

        return UserDetailResponseDto.builder().userNickname(user.getNickname()).userEmail(user.getEmail()).userImage(userImage).build();

    }

    public MyPageDetailResponseDto getMainDetail(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        String userImage = userImageRepository.findImagePathByUser(user);

        return MyPageDetailResponseDto.builder().userNickname(user.getNickname()).userImage(userImage).build();

    }

    @Transactional
    public void updateUserImage(String userEmail, MultipartFile image) {
        User user = userRepository.findByEmail(userEmail);
        if (image != null) {
            if (userImageRepository.findByUser(user) != null) {
                userImageRepository.delete(userImageRepository.findByUser(user));
            }

            String originalFileName = image.getOriginalFilename();
            String uploadUrl = s3ImageService.upload(image);
            UserImage userImage = UserImage.builder().user(user).imagePath(uploadUrl).imageOriginName(originalFileName).build();

            userImageRepository.save(userImage);
            System.err.println(uploadUrl);
        }
    }

    public void verifyPassword(String username, PasswordCheckDto passwordCheckDto) {
        User user = userRepository.findByEmail(username);
        if(!passwordEncoder.matches(passwordCheckDto.getPassword(), user.getPassword())){
            throw new ApiException(ErrorCode.PASSWORD_NOT_MATCH_ERROR);
        }
    }

    @Transactional
    public void changePassword(String username, PasswordCheckDto passwordCheckDto) {
        User user = userRepository.findByEmail(username);
        user.updatePassword(passwordCheckDto.getPassword(), passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
    public void deleteByEmail(String username) {
        userRepository.deleteByEmail(username);
    }
}
