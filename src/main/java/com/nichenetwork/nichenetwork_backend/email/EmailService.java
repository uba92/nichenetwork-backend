package com.nichenetwork.nichenetwork_backend.email;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String firstname) throws MessagingException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Benvenuto su NicheNetwork");
        message.setText("Ciao " + firstname + ", benvenuto su NicheNetwork!  Siamo felici che tu sia dei nostri. \n\nIl Team di Niche Network");
        javaMailSender.send(message);

        log.info("✅ Email inviata correttamente!");
    }

}
