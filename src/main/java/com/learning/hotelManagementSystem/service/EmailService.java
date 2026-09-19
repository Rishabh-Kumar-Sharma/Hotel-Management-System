package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.NotificationDTO.EmailDTO;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Resend resend;

    @Value("${resend.from.email}")
    private String fromEmail;

    public void sendMail(EmailDTO emailRequest) throws MessagingException {
        try {
            CreateEmailOptions.Builder builder=CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(emailRequest.to().toArray(new String[0]))
                    .subject(emailRequest.subject());

            if(emailRequest.isHtmlMessage()) {
                builder.html(emailRequest.body());
            } else {
                builder.text(emailRequest.body());
            }

            resend.emails().send(builder.build());
        } catch(ResendException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
