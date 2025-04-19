package tn.esprit.pidevspringboot.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("md.erouel01@gmail.com"); // 👈 doit correspondre à spring.mail.username
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // ✅ `true` ici signifie HTML

            mailSender.send(message);
            System.out.println("✅ Email HTML envoyé à : " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi du mail : " + e.getMessage());
        }
    }
}
