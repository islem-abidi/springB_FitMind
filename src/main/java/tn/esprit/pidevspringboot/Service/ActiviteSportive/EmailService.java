package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String subject, String body) {
        try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("oumayma.zou19@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("✅ Mail envoyé à : " + to);
    } catch (Exception e) {
        System.out.println("❌ Erreur d'envoi de mail : " + e.getMessage());
        e.printStackTrace();
    }
    }
}
