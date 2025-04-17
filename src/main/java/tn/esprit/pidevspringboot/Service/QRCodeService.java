package tn.esprit.pidevspringboot.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class QRCodeService {

    @Value("${app.upload.dir:${user.dir}/uploads}")

    private String uploadDir;

    // Générer le QR Code
    public String generateQRCode(InscriptionEvenement inscription) throws Exception {
        String uploadPath = uploadDir + "/qrcodes/";
        File uploadDirFile = new File(uploadPath);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = "qr_" + inscription.getUser().getIdUser() + "_" +
                inscription.getEvenement().getIdEvenement() + "_" +
                UUID.randomUUID().toString() + ".png";
        String filePath = uploadPath + fileName;

        // ✅ Contenu réel du QR Code
        String qrContent = "http://192.168.1.11:8081/Evenement/InscriptionEvenement/downloadTicket/" + inscription.getIdInscription();

        // Générer l'image du QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(filePath));

        // ✅ ENREGISTRER LE CHEMIN DANS L’INSCRIPTION
        inscription.setQrCodePath(fileName);
        inscription.setQrCodeGenerated(true); // si tu as ce champ booléen
        // Tu dois ensuite sauvegarder l'inscription mise à jour :
        // (si tu es dans un service, appelle le repository)
        // inscriptionRepo.save(inscription); // à adapter

        return fileName;
    }


    // Créer le contenu du QR Code
    private String createQRContent(InscriptionEvenement inscription) {
        // Retourne une URL directe vers le billet PDF
        return "http://192.168.1.11:8081/Evenement/InscriptionEvenement/downloadTicket/" + inscription.getIdInscription();
    }




    // Générer le PDF du billet
    public String generateTicketPDF(InscriptionEvenement inscription, String qrCodePath) throws Exception {
        // Créer le répertoire si nécessaire
        String uploadPath = uploadDir + "/tickets/";
        File uploadDirFile = new File(uploadPath);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Générer un nom de fichier unique
        String fileName = "ticket_" + inscription.getUser().getIdUser() + "_" +
                inscription.getEvenement().getIdEvenement() + "_" +
                UUID.randomUUID().toString() + ".pdf";
        String filePath = uploadPath + fileName;

        // Créer le document PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Ajouter le titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Billet d'entrée - " + inscription.getEvenement().getTitre(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Ajouter les détails
        document.add(new Paragraph("Participant: " + inscription.getUser().getNom() + " " + inscription.getUser().getPrenom()));
        document.add(new Paragraph("Date: " + inscription.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        document.add(new Paragraph("Lieu: " + inscription.getEvenement().getLieu()));
        document.add(new Paragraph("Statut: " + inscription.getStatutInscription()));
        document.add(new Paragraph(" "));

        // Ajouter le QR Code
        Image qrImg = Image.getInstance(uploadDir + "/qrcodes/" + qrCodePath);
        qrImg.setAlignment(Element.ALIGN_CENTER);
        qrImg.scalePercent(50);
        document.add(qrImg);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Ce billet est personnel et ne peut être transféré. Veuillez présenter ce billet à l'entrée de l'événement."));

        document.close();

        return fileName;
    }
}