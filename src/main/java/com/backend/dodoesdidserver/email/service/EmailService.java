package com.backend.dodoesdidserver.email.service;

import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import com.backend.dodoesdidserver.user.service.UserService;
import com.backend.dodoesdidserver.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private static final String senderEmail = "ddudu@dodoesdid.com";
    private final UserRepository userRepository;

    @Value("${frontend.reset-password-url}")
    private String resetPasswordUrl;

    private String createCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 57; // number '9'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
    }

    // 이메일 내용 초기화
    private String setContext(String code) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html>");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<title>인증번호</title>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<h1>안녕하세요.</h1>");
        htmlContent.append("<p>인증번호는 <strong>")
                   .append(code)
                   .append("</strong>입니다.</p>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        return htmlContent.toString();
    }

    // 이메일 폼 생성
    private MimeMessage createEmailForm(String email) throws MessagingException {
        String authCode = createCode();

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("안녕하세요. 인증번호입니다.");
        message.setFrom(senderEmail);
        message.setText(setContext(authCode), "utf-8", "html");

        // Redis 에 해당 인증코드 인증 시간 설정
        redisUtil.setDataExpire(email, authCode, 60 * 30L);

        return message;
    }

    // 인증코드 이메일 발송
    public void sendEmail(String email) throws MessagingException {
        if (userRepository.findByEmail(email) != null) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_REGISTERED_ERROR);
        }
        if (redisUtil.existData(email)) {
            redisUtil.deleteData(email);
        }
        // 이메일 폼 생성
        MimeMessage emailForm = createEmailForm(email);
        // 이메일 발송
        javaMailSender.send(emailForm);
    }

    // 코드 검증
    public Boolean verifyEmailCode(String email, String code) {
        String codeFoundByEmail = redisUtil.getData(email);
        if (codeFoundByEmail == null) {
            throw new ApiException(ErrorCode.VERIFICATION_EMAIL_NOT_EXIST_ERROR);
        }else if(!codeFoundByEmail.equals(code)) {
            throw new ApiException(ErrorCode.VERIFICATION_CODE_NOT_MATCH_ERROR);
        }
        return true;
    }

    public void sendResetMail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new ApiException(ErrorCode.EMAIL_NOT_EXIST_ERROR);
        }
        String token = UUID.randomUUID().toString();
        redisUtil.setDataExpire(token, user.getEmail(), 15 * 60L);

        String resetUrl = resetPasswordUrl + "?token=" +  token;

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("안녕하세요. 비밀번호 재설정 링크입니다.");
        message.setFrom(senderEmail);
        message.setText(resetUrl, "utf-8", "html");

        javaMailSender.send(message);

    }
}
