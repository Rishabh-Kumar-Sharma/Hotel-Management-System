package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.NotificationDTO.EmailDTO;
import com.learning.hotelManagementSystem.DTO.UserDTO.*;
import com.learning.hotelManagementSystem.exceptions.EmailServiceException;
import com.learning.hotelManagementSystem.utils.AppUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class EmailVerificationService {
    private final AppUtil appUtil;
    private final EmailService emailService;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final long OTP_EXPIRY_MINUTES=10;

    public VerifyEmailResponse sendAndSaveOTP(VerifyEmailRequest userRequest) {
        final String redisKey=appUtil.generateOTPKey(userRequest.userName());
        try {
            final List<String> to = List.of(userRequest.userName());
            final String subjectText = "OTP for email verification";
            final String otp=appUtil.generateOTP();
            final String otpHash=appUtil.generateOTPHash(otp);
            final String bodyText = "Hi " + userRequest.name() +
                    ", please use the below OTP for email verification:\n\n" +
                    otp +
                    "\n\nThis OTP is valid for 10 minutes.";

            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    otpHash,
                    OTP_EXPIRY_MINUTES,
                    TimeUnit.MINUTES
            );
            emailService.sendMail(new EmailDTO(to, subjectText, bodyText, false));
            return new VerifyEmailResponse(true);
        } catch(MessagingException e) {
            stringRedisTemplate.delete(redisKey);
            throw new EmailServiceException(e.getMessage());
        }
    }

    @Transactional
    public VerifyOTPResponse verifyOTP(VerifyOTPRequest request) {
        final String otpKey=appUtil.generateOTPKey(request.userName());
        String storedHash = stringRedisTemplate.opsForValue().get(otpKey);

        if(storedHash==null) return new VerifyOTPResponse(false);

        if(appUtil.matchOTP(request.OTP(),storedHash)) {
            userService.updateIsEmailVerified(request.userId(),true);
            return new VerifyOTPResponse(true);
        }

        return new VerifyOTPResponse(false);
    }
}
