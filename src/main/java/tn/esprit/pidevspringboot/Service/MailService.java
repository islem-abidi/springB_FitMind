package tn.esprit.pidevspringboot.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envoie un email simple avec du contenu HTML
     */
    public void sendSimpleEmail(String to, String subject, String htmlContent) {
        try {
            System.out.println("📧 Préparation d'un email simple pour : " + to);
            System.out.println("📧 Sujet : " + subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("md.erouel01@gmail.com"); // Doit correspondre à spring.mail.username
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true signifie que le contenu est au format HTML

            // Envoi de l'email
            System.out.println("📧 Envoi de l'email...");
            mailSender.send(message);
            System.out.println("✅ Email HTML envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi du mail à " + to + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envoie un email avec du contenu HTML et une image intégrée (logo)
     */
    public void sendEmailWithInlineImage(String to, String subject, String htmlContent) {
        try {
            System.out.println("📧 Préparation d'un email avec image pour : " + to);
            System.out.println("📧 Sujet : " + subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("md.erouel01@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);

            // Remplace l'URL par la référence cid:logoImage
            htmlContent = htmlContent.replace("http://localhost:8081/logo.png", "cid:logoImage");
            helper.setText(htmlContent, true);

            try {
                // Ajouter le logo à partir des ressources statiques
                ClassPathResource logoResource = new ClassPathResource("static/logo.png");
                if (logoResource.exists()) {
                    helper.addInline("logoImage", logoResource);
                    System.out.println("📧 Logo ajouté avec succès");
                } else {
                    System.err.println("⚠️ Le fichier logo.png n'existe pas dans le classpath");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Impossible d'ajouter le logo : " + e.getMessage());
                // Continuer sans le logo
            }

            // Envoi de l'email
            System.out.println("📧 Envoi de l'email avec image...");
            mailSender.send(message);
            System.out.println("✅ Email avec image intégrée envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi du mail avec image à " + to + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Version robuste pour l'envoi d'emails qui gère les erreurs
     * et renvoie un booléen indiquant le succès ou l'échec
     */
    public boolean sendRobustEmail(String to, String subject, String htmlContent, boolean withImage) {
        try {
            if (withImage) {
                sendEmailWithInlineImage(to, subject, htmlContent);
            } else {
                sendSimpleEmail(to, subject, htmlContent);
            }
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur critique lors de l'envoi de l'email à " + to + " : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}