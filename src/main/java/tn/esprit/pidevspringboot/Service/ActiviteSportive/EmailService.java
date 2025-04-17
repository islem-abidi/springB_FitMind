package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String subject, String body) {
        try {
            System.out.println("📨 Envoi du mail à " + to + " - Sujet : " + subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("oumayma.zou19@gmail.com"); // 🟢 adresse de l'expéditeur
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // ✅ true = HTML enabled

            mailSender.send(message);
            System.out.println("✅ Mail HTML envoyé à : " + to);
    } catch (Exception e) {
        System.out.println("❌ Erreur d'envoi de mail : " + e.getMessage());
        e.printStackTrace();
    }
    }
}
