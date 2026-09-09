package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.NotificationDTO.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(EmailDTO emailRequest) throws MessagingException {
        final MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message,true);
        helper.setTo(emailRequest.to().toArray(new String[0]));
        helper.setSubject(emailRequest.subject());
        helper.setText(emailRequest.body(), emailRequest.isHtmlMessage());

        mailSender.send(message);
    }
}
