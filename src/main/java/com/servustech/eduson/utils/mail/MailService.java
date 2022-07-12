package com.servustech.eduson.utils.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * Created by Luci on 22-Jun-17.
 */

@Slf4j
@Async
@Service
public class MailService {


    private static final String REGISTRATION_CONFIRM_HTML = "registrationConfirm";
    private static final String NEW_PASSWORD_HTML = "newPassword";
    private static final String EMAIL_TO_HTML="send_email";


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${properties.baseUrl}")
    private String baseUrl;


    public void sendRegisterConfirmationEmail(String emailTo, String token) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("email",emailTo);
        context.setVariable("baseUrl", baseUrl);
        String htmlContent = templateEngine.process(REGISTRATION_CONFIRM_HTML, context);
        try {
            log.info("Sending register confirmation email to " + emailTo);
            configureMessage(message, emailTo, "Confirm account", htmlContent);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Exception occurred while trying to send password change confirmation email", e);
        }
    }



    private void configureMessage(MimeMessageHelper message, String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        message.setFrom(new InternetAddress("eduson@servustech.com", "eduson application"));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content, true /* is html */);
    }
}
