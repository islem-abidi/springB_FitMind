    package tn.esprit.pidevspringboot.Controller;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.AllArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.UrlResource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;
    import tn.esprit.pidevspringboot.Service.IInscriptionEvenementService;
    import org.springframework.beans.factory.annotation.Value;
    import tn.esprit.pidevspringboot.Service.MailService;

    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.List;
    import java.nio.file.Files;

    @RestController
    @AllArgsConstructor
    @RequestMapping("/InscriptionEvenement")
    @Tag(name = "Gestion des inscriptions aux événements")
    public class InscriptionEvenementController {

        @Autowired
        IInscriptionEvenementService iInscriptionEvenementService;
        @Autowired
        private MailService mailService;

        @Value("${app.upload.dir:${user.dir}/uploads}")
        private String uploadDir; // ✅ AJOUT ICI
        @Operation(description = "Affichage de toutes les inscriptions")
        @GetMapping("/retrieveAllInscriptions")
        public List<InscriptionEvenement> afficherInscriptions() {
            return iInscriptionEvenementService.retrieveAllInscriptions();
        }

        @Operation(description = "Affichage d'une inscription spécifique")
        @GetMapping("/retrieveInscription/{id}")
        public InscriptionEvenement afficherInscription(@PathVariable("id") Long id) {
            return iInscriptionEvenementService.retrieveInscription(id);
        }

        @CrossOrigin(origins = "http://localhost:4200") // <- AJOUT ICI

        @Operation(description = "Ajouter une inscription")
        @PostMapping("/addInscription")
        public InscriptionEvenement ajouterInscription(@RequestBody InscriptionEvenement inscriptionEvenement) {
            return iInscriptionEvenementService.addInscription(inscriptionEvenement);
        }

        @Operation(description = "Supprimer une inscription")
        @DeleteMapping("/deleteInscription/{id}")
        public void deleteInscription(@PathVariable("id") Long id) {
            iInscriptionEvenementService.deleteInscription(id);
        }
        @Operation(description = "Annuler l'inscription d'un utilisateur à un événement")
        @DeleteMapping("/deleteByUserAndEvent/{idUser}/{idEvenement}")
        public ResponseEntity<Void> deleteByUserAndEvent(@PathVariable Long idUser, @PathVariable Long idEvenement) {
            boolean removed = iInscriptionEvenementService.deleteByUserAndEvent(idUser, idEvenement);
            if (removed) {
                return ResponseEntity.noContent().build(); // ✅ 204
            } else {
                return ResponseEntity.notFound().build(); // ❌ 404 si non trouvée
            }
        }

        @Operation(description = "Récupérer les inscriptions pour un événement spécifique")
        @GetMapping("/event/{idEvenement}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<List<InscriptionEvenement>> getInscriptionsByEvent(@PathVariable Long idEvenement) {
            List<InscriptionEvenement> inscriptions = iInscriptionEvenementService.findByEventId(idEvenement);
            return ResponseEntity.ok(inscriptions);
        }
        @Operation(description = "Mettre à jour le statut d'une inscription")
        @PutMapping("/status/{id}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<Void> updateInscriptionStatus(@PathVariable Long id, @RequestBody InscriptionEvenement updatedInscription) {
            iInscriptionEvenementService.updateInscriptionStatus(id, updatedInscription.getStatutInscription());
            return ResponseEntity.ok().build();
        }
        @Operation(description = "Générer un QR code pour une inscription confirmée")
        @PostMapping("/generateQRCode/{id}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<InscriptionEvenement> generateQRCode(@PathVariable("id") Long id) {
            try {
                InscriptionEvenement inscription = iInscriptionEvenementService.generateQRCode(id);
                return ResponseEntity.ok(inscription);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @Operation(description = "Télécharger le QR code d'une inscription")
        @GetMapping("/downloadQRCode/{id}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<org.springframework.core.io.Resource> downloadQRCode(@PathVariable("id") Long id) {
            try {
                InscriptionEvenement inscription = iInscriptionEvenementService.retrieveInscription(id);
                if (inscription == null || inscription.getQrCodePath() == null) {
                    return ResponseEntity.notFound().build();
                }

                Path path = Paths.get(uploadDir + "/qrcodes/" + inscription.getQrCodePath());

                if (!Files.exists(path)) {
                    System.err.println("❌ Fichier QR code introuvable : " + path);
                    return ResponseEntity.internalServerError().build();
                }

                org.springframework.core.io.Resource resource = new UrlResource(path.toUri());

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + inscription.getQrCodePath() + "\"") // ✅ CORRECTION ICI
                        .body(resource);
            } catch (Exception e) {
                e.printStackTrace(); // Pour voir l’erreur réelle en console
                return ResponseEntity.internalServerError().build();
            }
        }

        @Operation(description = "Télécharger le billet PDF d'une inscription")
        @GetMapping("/downloadTicket/{id}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<org.springframework.core.io.Resource> downloadTicket(@PathVariable("id") Long id) {
            try {
                InscriptionEvenement inscription = iInscriptionEvenementService.retrieveInscription(id);
                if (inscription == null || inscription.getTicketPdfPath() == null) {
                    return ResponseEntity.notFound().build();
                }

                Path path = Paths.get(uploadDir + "/tickets/" + inscription.getTicketPdfPath());
                System.out.println("➡️ Tentative de chargement du fichier : " + path.toAbsolutePath());

                if (!Files.exists(path)) {
                    System.err.println("❌ Fichier ticket introuvable : " + path);
                    return ResponseEntity.internalServerError().build();
                }

                org.springframework.core.io.Resource resource = new UrlResource(path.toUri());


                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + inscription.getTicketPdfPath() + "\"")
                        .header(HttpHeaders.CACHE_CONTROL, "must-revalidate, post-check=0, pre-check=0")
                        .body(resource);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }


        @Operation(description = "Vérifier si une inscription peut être annulée")
        @GetMapping("/canCancel/{id}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<Boolean> canCancelInscription(@PathVariable("id") Long id) {
            boolean canCancel = iInscriptionEvenementService.canCancelInscription(id);
            return ResponseEntity.ok(canCancel);
        }
        @Operation(description = "Trouver l'ID d'inscription par utilisateur et événement")
        @GetMapping("/findInscriptionId/{idUser}/{idEvenement}")
        @CrossOrigin(origins = "http://localhost:4200")
        public ResponseEntity<Long> findInscriptionIdByUserAndEvent(@PathVariable Long idUser, @PathVariable Long idEvenement) {
            Long inscriptionId = iInscriptionEvenementService.findInscriptionIdByUserAndEvent(idUser, idEvenement);
            if (inscriptionId != null) {
                return ResponseEntity.ok(inscriptionId);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/test-mail-erouel01")
        public String testMailErouel01() {
            mailService.sendSimpleEmail(
                    "md.erouel01@gmail.com",
                    "📩 TEST manuel",
                    "Ceci est un test de mail vers md.erouel01@gmail.com"
            );
            return "✅ Mail envoyé";
        }


    }

