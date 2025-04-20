package tn.esprit.pidevspringboot.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class QRCodeService {

    @Value("${app.upload.dir:${user.dir}/uploads}")
    private String uploadDir;
    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;
    // Couleurs de la marque FitMind
    private static final BaseColor PRIMARY_COLOR = new BaseColor(0, 158, 96); // Vert FitMind
    private static final BaseColor SECONDARY_COLOR = new BaseColor(70, 130, 180); // Bleu acier
    private static final BaseColor ACCENT_COLOR = new BaseColor(245, 130, 32); // Orange accent
    private static final BaseColor LIGHT_BG = new BaseColor(240, 250, 240); // Fond très légèrement vert
    private static final BaseColor DARK_TEXT = new BaseColor(45, 45, 45); // Texte presque noir
    private static final BaseColor BORDER_COLOR = new BaseColor(220, 230, 220); // Bordure subtile

    // Générer le QR Code avec correction d'erreur améliorée
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

        // Contenu du QR Code avec préfixe pour les apps mobiles
        String qrContent = baseUrl + "/Evenement/InscriptionEvenement/downloadTicket/" + inscription.getIdInscription();
        // Paramètres avancés pour QR Code - niveau de correction d'erreur élevé
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Niveau H = 30% de correction d'erreur
        hints.put(EncodeHintType.MARGIN, 2); // Marge réduite pour QR élégant

        // Générer QR code haute qualité
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300, hints);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(filePath));

        // Enregistrer dans l'inscription
        inscription.setQrCodePath(fileName);
        inscription.setQrCodeGenerated(true);

        return fileName;
    }

    // Générer le PDF du billet premium FitMind
    public String generateTicketPDF(InscriptionEvenement inscription, String qrCodePath) throws Exception {
        String uploadPath = uploadDir + "/tickets/";
        File uploadDirFile = new File(uploadPath);
        if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

        String fileName = "fitmind_" + inscription.getUser().getIdUser() + "_" +
                inscription.getEvenement().getIdEvenement() + "_" +
                UUID.randomUUID().toString() + ".pdf";
        String filePath = uploadPath + fileName;

        // Création du document avec marges professionnelles
        Document document = new Document(PageSize.A4, 36, 36, 72, 36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

        // Gestionnaire d'événements de page pour graphismes avancés
        FitMindPageEvent pageEvent = new FitMindPageEvent(inscription);
        writer.setPageEvent(pageEvent);

        document.open();

        // Polices personnalisées
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.NORMAL, SECONDARY_COLOR);
        Font headingFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, DARK_TEXT);
        Font highlightFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, SECONDARY_COLOR);
        Font accentFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, ACCENT_COLOR);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);
        Font ticketNumberFont = new Font(Font.FontFamily.COURIER, 14, Font.BOLD, PRIMARY_COLOR);

        // Section supérieure avec logo et titre
        addHeaderSection(document, inscription);

        // Ligne de séparation élégante
        document.add(createDivider());

        // Section principale
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        float[] widths = {0.6f, 0.4f};
        mainTable.setWidths(widths);
        mainTable.setSpacingBefore(15);
        mainTable.setSpacingAfter(15);

        // ===== COLONNE DE GAUCHE: DÉTAILS DE L'ÉVÉNEMENT =====
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPaddingRight(20);

        // Type d'événement
        // Type d'événement
        Paragraph eventType = new Paragraph(inscription.getEvenement().getTypeEvenement() != null ?
                inscription.getEvenement().getTypeEvenement().toString().toUpperCase() : "ÉVÉNEMENT", accentFont);
        eventType.setSpacingAfter(5);
        leftCell.addElement(eventType);

        // Titre de l'événement
        Paragraph eventTitle = new Paragraph(inscription.getEvenement().getTitre(), subtitleFont);
        eventTitle.setSpacingAfter(10);
        leftCell.addElement(eventTitle);

        // Numéro de billet avec étiquette
        Paragraph ticketNumberPara = new Paragraph();
        ticketNumberPara.add(new Chunk("BILLET N° ", smallFont));
        ticketNumberPara.add(new Chunk(String.format("FM-%08d", inscription.getIdInscription()), ticketNumberFont));
        ticketNumberPara.setSpacingAfter(15);
        leftCell.addElement(ticketNumberPara);

        // Informations principales
        leftCell.addElement(createInfoTable(inscription, headingFont, normalFont, highlightFont));

        // Status avec badge coloré
        Paragraph statusPara = new Paragraph();
        statusPara.setSpacingBefore(15);
        statusPara.add(new Chunk("STATUT: ", highlightFont));

        // Badge de statut
        PdfPTable statusBadge = new PdfPTable(1);
        statusBadge.setWidthPercentage(50);
        PdfPCell statusCell = new PdfPCell(new Phrase(getFormattedStatus(inscription.getStatutInscription().toString()),
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        statusCell.setBackgroundColor(getStatusColor(inscription.getStatutInscription().toString()));
        statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        statusCell.setPadding(4);
        statusBadge.addCell(statusCell);
        statusPara.add(new Chunk(" "));

        leftCell.addElement(statusPara);
        leftCell.addElement(statusBadge);

        mainTable.addCell(leftCell);

        // ===== COLONNE DE DROITE: QR CODE =====
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);

        // QR Code avec encadrement élégant
        PdfPTable qrContainer = new PdfPTable(1);
        qrContainer.setWidthPercentage(100);

        PdfPCell qrWrapperCell = new PdfPCell();
        qrWrapperCell.setBorder(Rectangle.BOX);
        qrWrapperCell.setBorderColor(BORDER_COLOR);
        qrWrapperCell.setBorderWidth(1f);
        qrWrapperCell.setPadding(8);
        qrWrapperCell.setBackgroundColor(BaseColor.WHITE);

        // QR Code - Version améliorée avec ombre
        Image qrImg = Image.getInstance(uploadDir + "/qrcodes/" + qrCodePath);
        qrImg.scaleToFit(180, 180);
        qrImg.setAlignment(Image.ALIGN_CENTER);

        Paragraph scanMePara = new Paragraph("SCANNEZ-MOI",
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, PRIMARY_COLOR));
        scanMePara.setAlignment(Element.ALIGN_CENTER);
        scanMePara.setSpacingBefore(5);
        scanMePara.setSpacingAfter(5);

        qrWrapperCell.addElement(scanMePara);
        qrWrapperCell.addElement(qrImg);

        Paragraph validationText = new Paragraph("Validation à l'entrée", smallFont);
        validationText.setAlignment(Element.ALIGN_CENTER);
        validationText.setSpacingBefore(5);
        qrWrapperCell.addElement(validationText);

        qrContainer.addCell(qrWrapperCell);
        rightCell.addElement(qrContainer);

        // Instructions sous le QR
        Paragraph qrInstructions = new Paragraph(
                "Présentez ce code à l'entrée pour valider votre participation. " +
                        "Le QR code contient votre identifiant unique et ne peut être transféré.", smallFont);
        qrInstructions.setAlignment(Element.ALIGN_CENTER);
        qrInstructions.setSpacingBefore(10);
        rightCell.addElement(qrInstructions);

        mainTable.addCell(rightCell);
        document.add(mainTable);

        // Section d'informations importantes
        document.add(createDivider());
        document.add(createImportantInfoSection(inscription, headingFont, normalFont, smallFont));

        // Pied de page avec contact et mention légale
        document.add(createFooterSection(smallFont));

        document.close();
        return fileName;
    }

    // Ajouter l'en-tête avec logo FitMind et titre
    private void addHeaderSection(Document document, InscriptionEvenement inscription) throws DocumentException, IOException {
        // En-tête avec logo et titre
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        float[] headerWidths = {0.6f, 0.4f};
        headerTable.setWidths(headerWidths);

        // Cellule pour le titre principal
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);

        // Logo textuel FitMind
        Paragraph logoText = new Paragraph();
        logoText.add(new Chunk("Fit", new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, PRIMARY_COLOR)));
        logoText.add(new Chunk("Mind", new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, SECONDARY_COLOR)));
        logoText.setSpacingAfter(5);
        titleCell.addElement(logoText);

        // Type de billet
        Paragraph ticketType = new Paragraph("BILLET OFFICIEL",
                new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, DARK_TEXT));
        ticketType.setSpacingAfter(3);
        titleCell.addElement(ticketType);

        // Date d'émission du billet
        Paragraph issueDate = new Paragraph("Émis le: " +
                DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH)
                        .format(java.time.LocalDate.now()),
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY));
        titleCell.addElement(issueDate);

        headerTable.addCell(titleCell);

        // Cellule pour le logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        try {
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.ALIGN_RIGHT);
            logoCell.addElement(logo);
        } catch (Exception e) {
            // Logo texte comme fallback si l'image n'est pas trouvée
            Paragraph fallbackLogo = new Paragraph("FitMind",
                    new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR));
            fallbackLogo.setAlignment(Element.ALIGN_RIGHT);
            logoCell.addElement(fallbackLogo);
        }

        headerTable.addCell(logoCell);
        document.add(headerTable);
    }

    // Créer une table d'informations élégante
    private PdfPTable createInfoTable(InscriptionEvenement inscription, Font headingFont, Font normalFont, Font highlightFont) {
        PdfPTable infoTable = new PdfPTable(2);
        try {
            infoTable.setWidthPercentage(100);
            float[] infoWidths = {0.35f, 0.65f};
            infoTable.setWidths(infoWidths);

            // Formatage spécifique des dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm", Locale.FRENCH);

            // Rangées d'information
            addInfoRow(infoTable, "PARTICIPANT", inscription.getUser().getNom().toUpperCase() + " " +
                    inscription.getUser().getPrenom(), normalFont, highlightFont);

            // Date avec formatage élégant
            addInfoRow(infoTable, "DATE", capitalize(inscription.getEvenement().getDateEvenement()
                    .format(dateFormatter)), normalFont, highlightFont);

            // Heure avec formatage français
            addInfoRow(infoTable, "HEURE", inscription.getEvenement().getDateEvenement()
                    .format(timeFormatter), normalFont, highlightFont);

            // Lieu avec formatage élégant
            addInfoRow(infoTable, "LIEU", formatLocation(inscription.getEvenement().getLieu()),
                    normalFont, highlightFont);

            // Si disponible, ajouter la durée
            if (inscription.getEvenement().getDateFin() != null) {
                addInfoRow(infoTable, "DURÉE", calculateDuration(
                        inscription.getEvenement().getDateEvenement(),
                        inscription.getEvenement().getDateFin()), normalFont, highlightFont);
            }
        } catch (DocumentException e) {
            System.err.println("Erreur lors de la création du tableau d'informations: " + e.getMessage());
        }

        return infoTable;
    }

    // Méthode pour ajouter une ligne d'information avec style amélioré
    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        // Cellule d'étiquette avec style compact
        PdfPCell labelCell = new PdfPCell(new Phrase(label + ":", labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingTop(4);
        labelCell.setPaddingBottom(4);
        labelCell.setPaddingRight(5);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Cellule de valeur avec style compact
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderWidthBottom(0.5f);
        valueCell.setBorderColor(BORDER_COLOR);
        valueCell.setPaddingTop(4);
        valueCell.setPaddingBottom(4);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    // Création d'un séparateur élégant
    private PdfPTable createDivider() {
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingBefore(15);
        divider.setSpacingAfter(15);

        PdfPCell dividerCell = new PdfPCell();
        dividerCell.setBorder(Rectangle.NO_BORDER);
        dividerCell.setFixedHeight(2f);
        dividerCell.setBackgroundColor(BORDER_COLOR);

        divider.addCell(dividerCell);
        return divider;
    }

    // Section d'informations importantes
    private PdfPTable createImportantInfoSection(InscriptionEvenement inscription, Font headingFont,
                                                 Font normalFont, Font smallFont) {
        PdfPTable infoSectionTable = new PdfPTable(1);
        infoSectionTable.setWidthPercentage(100);
        infoSectionTable.setSpacingBefore(10);

        // En-tête de la section
        PdfPCell infoHeaderCell = new PdfPCell(new Phrase("INFORMATIONS IMPORTANTES", headingFont));
        infoHeaderCell.setBorder(Rectangle.NO_BORDER);
        infoHeaderCell.setBackgroundColor(LIGHT_BG);
        infoHeaderCell.setPadding(8);
        infoSectionTable.addCell(infoHeaderCell);

        // Contenu de la section d'information
        PdfPCell infoContentCell = new PdfPCell();
        infoContentCell.setBorder(Rectangle.BOX);
        infoContentCell.setBorderColor(BORDER_COLOR);
        infoContentCell.setPadding(12);

        // Section 1: Avant l'événement
        Paragraph beforeEvent = new Paragraph("Avant l'événement:", normalFont);
        beforeEvent.setSpacingAfter(5);
        infoContentCell.addElement(beforeEvent);

        Paragraph beforeEventBullets = new Paragraph();
        beforeEventBullets.add(new Chunk("• ", normalFont));
        beforeEventBullets.add(new Chunk("Veuillez vous présenter au moins 30 minutes avant le début.", smallFont));
        beforeEventBullets.add(Chunk.NEWLINE);

        beforeEventBullets.add(new Chunk("• ", normalFont));
        beforeEventBullets.add(new Chunk("Une pièce d'identité correspondant au nom sur le billet pourra être demandée.", smallFont));
        beforeEventBullets.add(Chunk.NEWLINE);

        beforeEventBullets.add(new Chunk("• ", normalFont));
        beforeEventBullets.add(new Chunk("Prévoyez une tenue adaptée à l'événement: " +
                getRecommendedOutfit(inscription.getEvenement().getTypeEvenement().toString()), smallFont));

        beforeEventBullets.setIndentationLeft(20);
        beforeEventBullets.setSpacingAfter(10);
        infoContentCell.addElement(beforeEventBullets);

        // Section 2: Pendant l'événement
        Paragraph duringEvent = new Paragraph("Pendant l'événement:", normalFont);
        duringEvent.setSpacingAfter(5);
        infoContentCell.addElement(duringEvent);

        Paragraph duringEventBullets = new Paragraph();
        duringEventBullets.add(new Chunk("• ", normalFont));
        duringEventBullets.add(new Chunk("Respectez les consignes de sécurité et le règlement intérieur.", smallFont));
        duringEventBullets.add(Chunk.NEWLINE);

        duringEventBullets.add(new Chunk("• ", normalFont));
        duringEventBullets.add(new Chunk("Ce billet est personnel, nominatif et non transférable.", smallFont));
        duringEventBullets.add(Chunk.NEWLINE);

        duringEventBullets.add(new Chunk("• ", normalFont));
        duringEventBullets.add(new Chunk("L'organisateur se réserve le droit de refuser l'entrée en cas de non-respect des conditions.", smallFont));

        duringEventBullets.setIndentationLeft(20);
        infoContentCell.addElement(duringEventBullets);

        infoSectionTable.addCell(infoContentCell);
        return infoSectionTable;
    }

    // Section de pied de page
    private Paragraph createFooterSection(Font smallFont) {
        Paragraph footer = new Paragraph();
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);

        footer.add(new Chunk("FitMind - Bien-être du corps et de l'esprit\n",
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));

        footer.add(new Chunk("Contact: support@fitmind.com | +216 XX XXX XXX | www.fitmind.com\n", smallFont));

        footer.add(new Chunk("© " + java.time.Year.now().getValue() + " FitMind. Tous droits réservés.",
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY)));

        return footer;
    }

    // Formater le statut pour un style professionnel
    private String getFormattedStatus(String status) {
        switch (status) {
            case "CONFIRMEE":
                return "CONFIRMÉ";
            case "EN_ATTENTE":
                return "EN ATTENTE";
            case "REJETEE":
                return "REJETÉ";
            default:
                return status;
        }
    }

    // Obtenir la couleur selon le statut
    private BaseColor getStatusColor(String status) {
        switch (status) {
            case "CONFIRMEE":
                return new BaseColor(46, 125, 50); // Vert foncé
            case "EN_ATTENTE":
                return new BaseColor(245, 124, 0); // Orange
            case "REJETEE":
                return new BaseColor(211, 47, 47); // Rouge
            default:
                return BaseColor.GRAY;
        }
    }

    // Formatage du lieu
    private String formatLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return "À déterminer";
        }

        // Mettre en majuscule la première lettre de chaque mot
        String[] words = location.split("\\s+");
        StringBuilder formattedLocation = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedLocation.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    formattedLocation.append(word.substring(1).toLowerCase());
                }
                formattedLocation.append(" ");
            }
        }

        return formattedLocation.toString().trim();
    }

    // Capitalize pour le français
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    // Calcul et formatage de la durée
    private String calculateDuration(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        long minutes = java.time.Duration.between(start, end).toMinutes();

        if (minutes < 60) {
            return minutes + " minutes";
        }

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours < 24) {
            if (remainingMinutes == 0) {
                return hours + " heure" + (hours > 1 ? "s" : "");
            }
            return hours + "h" + String.format("%02d", remainingMinutes);
        }

        long days = hours / 24;
        long remainingHours = hours % 24;

        if (remainingHours == 0 && remainingMinutes == 0) {
            return days + " jour" + (days > 1 ? "s" : "");
        } else if (remainingMinutes == 0) {
            return days + "j " + remainingHours + "h";
        } else {
            return days + "j " + remainingHours + "h" + String.format("%02d", remainingMinutes);
        }
    }

    // Recommandation de tenue selon le type d'événement
    private String getRecommendedOutfit(String eventType) {
        if (eventType == null) {
            return "tenue confortable";
        }

        switch (eventType.toUpperCase()) {
            case "YOGA":
            case "MEDITATION":
                return "tenue souple et confortable";
            case "FITNESS":
            case "SPORT":
                return "tenue de sport et chaussures adaptées";
            case "CONFÉRENCE":
            case "SÉMINAIRE":
                return "tenue décontractée élégante";
            case "ATELIER":
                return "tenue décontractée et pratique";
            default:
                return "tenue adaptée à l'activité";
        }
    }

    // Classe de gestion des événements de page - arrière-plans, en-têtes, etc.
    private class FitMindPageEvent extends PdfPageEventHelper {
        private InscriptionEvenement inscription;
        private PdfTemplate template;
        private BaseFont baseFont;

        public FitMindPageEvent(InscriptionEvenement inscription) {
            this.inscription = inscription;
            try {
                this.baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                System.err.println("Erreur lors de la création de la police: " + e.getMessage());
            }
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            template = writer.getDirectContent().createTemplate(30, 16);
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                // Fond subtil avec dégradé professionnel
                PdfContentByte canvas = writer.getDirectContentUnder();
                Rectangle rect = document.getPageSize();

                // Dégradé de fond ultra léger
                PdfShading shading = PdfShading.simpleAxial(writer,
                        rect.getLeft(), rect.getTop(),
                        rect.getRight(), rect.getBottom(),
                        BaseColor.WHITE, LIGHT_BG);

                PdfShadingPattern pattern = new PdfShadingPattern(shading);
                canvas.setShadingFill(pattern);
                canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
                canvas.fill();

                // Bordure élégante
                canvas.setColorStroke(BORDER_COLOR);
                canvas.setLineWidth(1f);
                canvas.rectangle(rect.getLeft() + 20, rect.getBottom() + 20, rect.getWidth() - 40, rect.getHeight() - 40);
                canvas.stroke();

                // Petit accent de couleur en haut
                canvas.setColorFill(PRIMARY_COLOR);
                canvas.rectangle(rect.getLeft() + 20, rect.getTop() - 20, rect.getWidth() - 40, 4);
                canvas.fill();

                // Petit accent de couleur en bas
                canvas.setColorFill(SECONDARY_COLOR);
                canvas.rectangle(rect.getLeft() + 20, rect.getBottom() + 20, rect.getWidth() - 40, 2);
                canvas.fill();

            } catch (Exception e) {
                System.err.println("Erreur lors de la création des éléments graphiques: " + e.getMessage());
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Pied de page élégant avec numéro et ID du billet
            PdfContentByte cb = writer.getDirectContent();
            if (baseFont != null) {
                cb.saveState();

                // Ligne fine en bas
                cb.setColorStroke(BORDER_COLOR);
                cb.setLineWidth(0.5f);
                cb.moveTo(document.left(), document.bottom() - 5);
                cb.lineTo(document.right(), document.bottom() - 5);
                cb.stroke();

                // Numéro de page en pied de page avec style discret
                String text = "Page " + writer.getPageNumber();
                float textSize = baseFont.getWidthPoint(text, 8);
                float textBase = document.bottom() - 15;

                cb.beginText();
                cb.setFontAndSize(baseFont, 8);
                cb.setColorFill(BaseColor.GRAY);
                cb.setTextMatrix(document.right() - textSize - 20, textBase);
                cb.showText(text);
                cb.endText();

                // ID du billet à gauche
                String ticketId = "ID: FM-" + String.format("%08d", inscription.getIdInscription());
                cb.beginText();
                cb.setFontAndSize(baseFont, 8);
                cb.setColorFill(BaseColor.GRAY);
                cb.setTextMatrix(document.left() + 20, textBase);
                cb.showText(ticketId);
                cb.endText();

                cb.restoreState();
            }
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            // Utilisation du template pour finaliser le document si nécessaire
            template.beginText();
            template.setFontAndSize(baseFont, 8);
            template.showText("Billet FitMind - Émis le " +
                    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH).format(java.time.LocalDate.now()));
            template.endText();
        }
    }

    // Méthode publique pour récupérer le chemin d'un ticket existant
    public String getTicketPath(String fileName) {
        return uploadDir + "/tickets/" + fileName;
    }

    // Méthode pour vérifier si un ticket existe déjà
    public boolean ticketExists(String fileName) {
        File file = new File(uploadDir + "/tickets/" + fileName);
        return file.exists();
    }

    // Méthode pour vérifier si un QR code existe déjà
    public boolean qrCodeExists(String fileName) {
        File file = new File(uploadDir + "/qrcodes/" + fileName);
        return file.exists();
    }

    // Méthode pour ajouter des données supplémentaires au besoin
    public void addAdditionalQRCodeData(InscriptionEvenement inscription) {
        // Vous pouvez étendre cette méthode pour ajouter des données sécurisées
        // comme un hash vérifiant l'authenticité du billet, etc.
    }

    // Méthode pour valider un QR code scanné
    public boolean validateScannedQRCode(String qrContent) {
        // Vérification basique actuelle
        if (qrContent != null && qrContent.contains("/downloadTicket/")) {
            try {
                // Extraire l'ID d'inscription
                String idPart = qrContent.substring(qrContent.lastIndexOf("/") + 1);
                long id = Long.parseLong(idPart);
                // Logique de validation supplémentaire possible ici
                return id > 0;
            } catch (Exception e) {
                System.err.println("Format de QR code invalide: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // Méthode pour créer une version mobile du ticket (version simplifiée pour smartphones)
    public String generateMobileTicket(InscriptionEvenement inscription, String qrCodePath) throws Exception {
        String uploadPath = uploadDir + "/mobile_tickets/";
        File uploadDirFile = new File(uploadPath);
        if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

        String fileName = "mobile_" + inscription.getUser().getIdUser() + "_" +
                inscription.getEvenement().getIdEvenement() + "_" +
                UUID.randomUUID().toString() + ".pdf";
        String filePath = uploadPath + fileName;

        // Format plus petit pour mobile
        Document document = new Document(PageSize.A6.rotate(), 10, 10, 15, 10);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Polices adaptées au format mobile
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, DARK_TEXT);
        Font highlightFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, SECONDARY_COLOR);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.DARK_GRAY);

        // En-tête compact
        Paragraph header = new Paragraph();
        header.add(new Chunk("FitMind ", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, PRIMARY_COLOR)));
        header.add(new Chunk("Mobile", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR)));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // Titre événement
        Paragraph eventTitle = new Paragraph(inscription.getEvenement().getTitre(), titleFont);
        eventTitle.setAlignment(Element.ALIGN_CENTER);
        eventTitle.setSpacingBefore(5);
        eventTitle.setSpacingAfter(5);
        document.add(eventTitle);

        // Layout principal
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);

        // QR Code plus grand et centré
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);

        Image qrImg = Image.getInstance(uploadDir + "/qrcodes/" + qrCodePath);
        qrImg.scaleToFit(100, 100);
        qrImg.setAlignment(Image.ALIGN_CENTER);
        qrCell.addElement(qrImg);

        // Informations essentielles
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm", Locale.FRENCH);

        Paragraph infoPara = new Paragraph();
        infoPara.add(new Chunk("PARTICIPANT: ", highlightFont));
        infoPara.add(new Chunk(inscription.getUser().getNom() + " " + inscription.getUser().getPrenom() + "\n", normalFont));

        infoPara.add(new Chunk("DATE: ", highlightFont));
        infoPara.add(new Chunk(inscription.getEvenement().getDateEvenement().format(dateFormatter) + "\n", normalFont));

        infoPara.add(new Chunk("HEURE: ", highlightFont));
        infoPara.add(new Chunk(inscription.getEvenement().getDateEvenement().format(timeFormatter) + "\n", normalFont));

        infoPara.add(new Chunk("LIEU: ", highlightFont));
        infoPara.add(new Chunk(formatLocation(inscription.getEvenement().getLieu()) + "\n", normalFont));

        infoPara.add(new Chunk("STATUT: ", highlightFont));
        infoPara.add(new Chunk(getFormattedStatus(inscription.getStatutInscription().toString()) + "\n", normalFont));

        infoPara.add(new Chunk("ID: ", highlightFont));
        infoPara.add(new Chunk("FM-" + String.format("%08d", inscription.getIdInscription()), normalFont));

        infoCell.addElement(infoPara);

        mainTable.addCell(qrCell);
        mainTable.addCell(infoCell);
        document.add(mainTable);

        // Pied de page minimal
        Paragraph footer = new Paragraph("© FitMind - www.fitmind.com", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);

        document.close();
        return fileName;
    }

    // Méthode pour créer une version imprimable sans fond ni graphiques complexes (économie d'encre)
    public String generatePrintFriendlyTicket(InscriptionEvenement inscription, String qrCodePath) throws Exception {
        String uploadPath = uploadDir + "/print_tickets/";
        File uploadDirFile = new File(uploadPath);
        if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

        String fileName = "print_" + inscription.getUser().getIdUser() + "_" +
                inscription.getEvenement().getIdEvenement() + "_" +
                UUID.randomUUID().toString() + ".pdf";
        String filePath = uploadPath + fileName;

        // Document A4 sans fioritures
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Polices standards en noir pour l'impression
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font headingFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // En-tête simple
        Paragraph header = new Paragraph();
        header.add(new Chunk("FITMIND - BILLET D'ÉVÉNEMENT", titleFont));
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20);
        document.add(header);

        // Titre événement
        Paragraph eventTitle = new Paragraph(inscription.getEvenement().getTitre(), headingFont);
        eventTitle.setAlignment(Element.ALIGN_CENTER);
        eventTitle.setSpacingAfter(20);
        document.add(eventTitle);

        // Tableau d'informations
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm", Locale.FRENCH);

        // Informations principales en deux colonnes
        addSimpleInfoRow(infoTable, "PARTICIPANT:", inscription.getUser().getNom() + " " +
                inscription.getUser().getPrenom(), boldFont, normalFont);
        addSimpleInfoRow(infoTable, "DATE:", inscription.getEvenement().getDateEvenement()
                .format(dateFormatter), boldFont, normalFont);
        addSimpleInfoRow(infoTable, "HEURE:", inscription.getEvenement().getDateEvenement()
                .format(timeFormatter), boldFont, normalFont);
        addSimpleInfoRow(infoTable, "LIEU:", formatLocation(inscription.getEvenement().getLieu()),
                boldFont, normalFont);
        addSimpleInfoRow(infoTable, "STATUT:", getFormattedStatus(inscription.getStatutInscription().toString()),
                boldFont, normalFont);
        addSimpleInfoRow(infoTable, "RÉFÉRENCE:", "FM-" + String.format("%08d", inscription.getIdInscription()),
                boldFont, normalFont);

        document.add(infoTable);

        // QR Code centré
        Paragraph qrPara = new Paragraph();
        qrPara.setAlignment(Element.ALIGN_CENTER);
        qrPara.setSpacingBefore(20);
        qrPara.setSpacingAfter(20);

        Image qrImg = Image.getInstance(uploadDir + "/qrcodes/" + qrCodePath);
        qrImg.scaleToFit(150, 150);
        qrImg.setAlignment(Image.ALIGN_CENTER);
        qrPara.add(qrImg);

        document.add(qrPara);

        // Instructions d'impression
        Paragraph printNote = new Paragraph("Imprimez ce billet et présentez-le à l'entrée de l'événement.", smallFont);
        printNote.setAlignment(Element.ALIGN_CENTER);
        document.add(printNote);

        document.close();
        return fileName;
    }

    // Méthode simplifiée pour ajouter des lignes d'information sans style complexe
    private void addSimpleInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(10);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(10);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    // Nettoyage des fichiers temporaires
    public void cleanupTempFiles(long olderThanDays) {
        try {
            cleanDirectory(uploadDir + "/qrcodes/", olderThanDays);
            cleanDirectory(uploadDir + "/tickets/", olderThanDays);
            cleanDirectory(uploadDir + "/mobile_tickets/", olderThanDays);
            cleanDirectory(uploadDir + "/print_tickets/", olderThanDays);
        } catch (Exception e) {
            System.err.println("Erreur lors du nettoyage des fichiers temporaires: " + e.getMessage());
        }
    }

    private void cleanDirectory(String directoryPath, long olderThanDays) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            long cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000);
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.lastModified() < cutoffTime) {
                        file.delete();
                    }
                }
            }
        }
    }
}