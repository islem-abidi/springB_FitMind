package tn.esprit.pidevspringboot.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tn.esprit.pidevspringboot.Entities.Evenement.*;
import tn.esprit.pidevspringboot.Repository.EvenementRepository;
import tn.esprit.pidevspringboot.Repository.InscriptionEvenementRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EvenementServiceImpl implements IEvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private InscriptionEvenementRepository inscriptionEvenementRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private RestTemplate restTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Récupère tous les événements et met à jour leurs états
     */
    @Override
    public List<Evenement> retrieveAllEvenements() {
        List<Evenement> events = evenementRepository.findAll();
        events.forEach(event -> event.setEtatEvent(calculerEtat(event.getDateEvenement(), event.getDateFin())));
        return events;
    }

    /**
     * Récupère un événement par son ID
     */
    @Override
    @Transactional
    public Evenement retrieveEvenement(Long id) {
        Evenement event = evenementRepository.findById(id).orElse(null);
        if (event != null) {
            event.setEtatEvent(calculerEtat(event.getDateEvenement(), event.getDateFin()));
            // Forcer le chargement des inscriptions pour éviter les problèmes LazyInitializationException
            if (event.getInscriptions() != null) {
                event.getInscriptions().size();
            }
        }
        return event;
    }

    /**
     * Calcule l'état d'un événement en fonction de sa date de début et de fin
     */
    public EtatEvent calculerEtat(LocalDateTime dateDebut, LocalDateTime dateFin) {
        LocalDateTime maintenant = LocalDateTime.now();
        if (dateDebut == null || dateFin == null) return EtatEvent.A_VENIR;
        if (maintenant.isBefore(dateDebut)) return EtatEvent.A_VENIR;
        if (maintenant.isAfter(dateFin)) return EtatEvent.PASSE;
        return EtatEvent.EN_COURS;
    }

    /**
     * Ajoute un nouvel événement
     */
    @Override
    public Evenement addEvenement(Evenement evenement) {
        if (evenement.getIdEvenement() != null) {
            Evenement existing = evenementRepository.findById(evenement.getIdEvenement()).orElse(null);
            if (existing != null && existing.getInscriptions() != null) {
                evenement.setInscriptions(existing.getInscriptions());
            }
        }
        evenement.setEtatEvent(calculerEtat(evenement.getDateEvenement(), evenement.getDateFin()));
        System.out.println("✅ Ajout d'un nouvel événement : " + evenement.getTitre());
        return evenementRepository.save(evenement);
    }

    /**
     * Met à jour un événement et notifie les utilisateurs inscrits des changements
     */
    @Override
    @Transactional
    public Evenement updateEvenement(Evenement evenement) {
        System.out.println("🛠️ Mise à jour de l'événement ID : " + evenement.getIdEvenement());

        Evenement original = evenementRepository.findById(evenement.getIdEvenement()).orElse(null);
        if (original == null) {
            System.out.println("❌ Événement non trouvé !");
            return evenementRepository.save(evenement);
        }

        List<InscriptionEvenement> inscriptions = original.getInscriptions() != null
                ? original.getInscriptions() : new ArrayList<>();

        // Détecter les modifications
        Map<String, FieldChange> changes = detectChanges(original, evenement);
        EtatEvent etat = calculerEtat(original.getDateEvenement(), original.getDateFin());

        // Vérifier si la date ou le lieu ont été modifiés
        boolean dateChanged = changes.containsKey("date") || changes.containsKey("dateFin");
        boolean lieuChanged = changes.containsKey("lieu");

        // Envoyer les notifications seulement si la date ou le lieu ont été modifiés
        if ((dateChanged || lieuChanged) && (etat == EtatEvent.A_VENIR || etat == EtatEvent.EN_COURS)) {
            System.out.println("📧 Notification des utilisateurs pour les modifications de date ou lieu...");
            // Filtrer les changements pour ne garder que date et lieu
            Map<String, FieldChange> importantChanges = new HashMap<>();
            if (changes.containsKey("date")) importantChanges.put("date", changes.get("date"));
            if (changes.containsKey("dateFin")) importantChanges.put("dateFin", changes.get("dateFin"));
            if (changes.containsKey("lieu")) importantChanges.put("lieu", changes.get("lieu"));

            notifyUsersAboutChanges(original, evenement, importantChanges);
        } else {
            System.out.println("ℹ️ Aucun changement de date ou lieu détecté ou événement passé.");
        }

        // Mise à jour des champs
        original.setTitre(evenement.getTitre());
        original.setDescription(evenement.getDescription());
        original.setLieu(evenement.getLieu());
        original.setDateEvenement(evenement.getDateEvenement());
        original.setDateFin(evenement.getDateFin());
        original.setCapaciteMax(evenement.getCapaciteMax());
        original.setImage(evenement.getImage());
        original.setTypeEvenement(evenement.getTypeEvenement());

        original.setEtatEvent(calculerEtat(original.getDateEvenement(), original.getDateFin()));

        return evenementRepository.save(original);
    }

    /**
     * Classe interne pour représenter un changement de champ
     */
    private static class FieldChange {
        public final String oldValue;
        public final String newValue;
        public final String label;
        public final String emoji;

        public FieldChange(String oldValue, String newValue, String label, String emoji) {
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.label = label;
            this.emoji = emoji;
        }
    }

    /**
     * Détecte les changements entre l'événement original et l'événement mis à jour
     */
    private Map<String, FieldChange> detectChanges(Evenement original, Evenement updated) {
        System.out.println("🧪 detectChanges() appelée !");

        Map<String, FieldChange> changes = new HashMap<>();

        // ✅ Vérifie la date de début
        if ((original.getDateEvenement() == null && updated.getDateEvenement() != null) ||
                (original.getDateEvenement() != null && updated.getDateEvenement() != null &&
                        !original.getDateEvenement().isEqual(updated.getDateEvenement()))) {

            String oldDate = original.getDateEvenement() != null ?
                    original.getDateEvenement().format(DATE_FORMATTER) : "Non définie";
            String newDate = updated.getDateEvenement() != null ?
                    updated.getDateEvenement().format(DATE_FORMATTER) : "Non définie";

            changes.put("date", new FieldChange(oldDate, newDate, "Date de début", "📅"));
            System.out.println("Changement de date détecté: " + oldDate + " -> " + newDate);
        }

        // ✅ Vérifie la date de fin
        if ((original.getDateFin() == null && updated.getDateFin() != null) ||
                (original.getDateFin() != null && updated.getDateFin() != null &&
                        !original.getDateFin().isEqual(updated.getDateFin()))) {

            String oldDate = original.getDateFin() != null ?
                    original.getDateFin().format(DATE_FORMATTER) : "Non définie";
            String newDate = updated.getDateFin() != null ?
                    updated.getDateFin().format(DATE_FORMATTER) : "Non définie";

            changes.put("dateFin", new FieldChange(oldDate, newDate, "Date de fin", "🏁"));
            System.out.println("Changement de date de fin détecté: " + oldDate + " -> " + newDate);
        }

        // ✅ Vérifie le lieu
        String oldLieu = original.getLieu() != null ? original.getLieu().trim() : "";
        String newLieu = updated.getLieu() != null ? updated.getLieu().trim() : "";
        if (!oldLieu.equals(newLieu)) {
            changes.put("lieu", new FieldChange(oldLieu, newLieu, "Lieu", "📍"));
            System.out.println("Changement de lieu détecté: " + oldLieu + " -> " + newLieu);
        }

        // ✅ Vérifie la description
        String oldDesc = original.getDescription() != null ? original.getDescription().trim() : "";
        String newDesc = updated.getDescription() != null ? updated.getDescription().trim() : "";
        if (!oldDesc.equals(newDesc)) {
            changes.put("description", new FieldChange(oldDesc, newDesc, "Description", "📝"));
        }

        // ✅ Vérifie la capacité max
        Integer oldCap = original.getCapaciteMax() != null ? original.getCapaciteMax() : 0;
        Integer newCap = updated.getCapaciteMax() != null ? updated.getCapaciteMax() : 0;
        if (!oldCap.equals(newCap)) {
            changes.put("capacite", new FieldChange(
                    oldCap.toString(), newCap.toString(), "Capacité maximale", "👥"));
        }

        // CORRECTION: Ligne de debug supprimée
        // changes.put("debugTest", new FieldChange("ancien", "nouveau", "DEBUG", "🧪"));

        return changes;
    }

    /**
     * Envoie des notifications aux utilisateurs concernant les modifications de l'événement
     */
    private void notifyUsersAboutChanges(Evenement original, Evenement updated, Map<String, FieldChange> changes) {
        System.out.println("📨 Lancement de notifyUsersAboutChanges avec " + changes.size() + " changement(s)");

        if (original.getInscriptions() == null) {
            System.out.println("⚠️ La liste des inscriptions est null");
            return;
        }

        if (original.getInscriptions().isEmpty()) {
            System.out.println("⚠️ La liste des inscriptions est vide");
            return;
        }

        System.out.println("📊 Analyse de " + original.getInscriptions().size() + " inscriptions");

        // Compter les inscriptions par statut pour debug
        Map<StatutInscription, Integer> compteurStatuts = new HashMap<>();
        for (InscriptionEvenement inscription : original.getInscriptions()) {
            StatutInscription statut = inscription.getStatutInscription();
            compteurStatuts.put(statut, compteurStatuts.getOrDefault(statut, 0) + 1);
        }

        System.out.println("📊 Répartition des statuts d'inscription :");
        for (Map.Entry<StatutInscription, Integer> entry : compteurStatuts.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
        }

        // Filtrer les inscriptions confirmées
        List<InscriptionEvenement> inscriptionsConfirmees = original.getInscriptions().stream()
                .filter(inscription -> {
                    boolean userOk = inscription.getUser() != null;
                    boolean emailOk = userOk && inscription.getUser().getEmail() != null && !inscription.getUser().getEmail().isEmpty();
                    boolean statutOk = inscription.getStatutInscription() == StatutInscription.CONFIRMEE;

                    System.out.println("🔍 Inscription ID " + inscription.getIdInscription() +
                            ": User " + (userOk ? "OK" : "NULL") +
                            ", Email " + (emailOk ? "OK" : "NULL") +
                            ", Statut " + (statutOk ? "CONFIRMEE" : inscription.getStatutInscription()));

                    return userOk && emailOk && statutOk;
                })
                .collect(Collectors.toList());

        System.out.println("📧 Nombre d'inscriptions éligibles pour notification : " + inscriptionsConfirmees.size());

        if (inscriptionsConfirmees.isEmpty()) {
            System.out.println("⚠️ Aucune inscription confirmée avec email valide trouvée");
            return;
        }

        String subject = "🔔 Mise à jour de l'événement " + original.getTitre();

        for (InscriptionEvenement inscription : inscriptionsConfirmees) {
            try {
                String email = inscription.getUser().getEmail();
                String nom = inscription.getUser().getNom() != null ? inscription.getUser().getNom() : "Utilisateur";

                System.out.println("📧 Préparation de l'email pour : " + email);

                // Créer le contenu HTML pour l'email
                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 5px;'>");
                contentBuilder.append("<h2 style='color: #3498db;'>Mise à jour d'événement</h2>");
                contentBuilder.append("<p>Bonjour <strong>").append(nom).append("</strong>,</p>");
                contentBuilder.append("<p>L'événement <strong>").append(original.getTitre()).append("</strong> auquel vous êtes inscrit a été mis à jour :</p>");

                contentBuilder.append("<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>");
                contentBuilder.append("<tr style='background-color: #f8f8f8;'><th style='padding: 10px; text-align: left;'>Modification</th><th style='padding: 10px; text-align: left;'>Ancienne valeur</th><th style='padding: 10px; text-align: left;'>Nouvelle valeur</th></tr>");

                // Ajouter chaque modification avec l'ancienne valeur et la nouvelle en tableau
                for (FieldChange change : changes.values()) {
                    contentBuilder.append("<tr>");
                    contentBuilder.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'>")
                            .append(change.emoji).append(" ").append(change.label).append("</td>");
                    contentBuilder.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'><s>")
                            .append(change.oldValue).append("</s></td>");
                    contentBuilder.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>")
                            .append(change.newValue).append("</strong></td>");
                    contentBuilder.append("</tr>");
                }

                contentBuilder.append("</table>");

                contentBuilder.append("<p>Merci de prendre en compte ces changements.</p>");
                contentBuilder.append("<p style='margin-top: 20px;'>Cordialement,<br>L'équipe FitMind</p>");
                contentBuilder.append("</div>");

                // Envoyer l'email et tester son envoi
                System.out.println("📧 Tentative d'envoi d'email à : " + email);
                try {
                    mailService.sendEmailWithInlineImage(email, subject, contentBuilder.toString());
                    System.out.println("✅ Email envoyé avec succès à : " + email);
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de l'envoi d'email via mailService : " + e.getMessage());
                    e.printStackTrace();

                    // TEST DIRECT avec mailSender si disponible
                    System.out.println("🔄 Tentative directe d'envoi...");
                    try {
                        sendDirectEmail(email, subject, contentBuilder.toString());
                    } catch (Exception ex) {
                        System.err.println("❌ Échec de l'envoi direct : " + ex.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur générale lors du traitement de l'email : " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("✅ Fin du traitement des notifications");
    }

    // Méthode d'envoi direct pour test
    private void sendDirectEmail(String to, String subject, String htmlContent) throws Exception {
        // Si vous avez accès direct au JavaMailSender
        if (mailService != null) {
            System.out.println("📧 Test d'envoi direct via MailService");

            // Utilisez une méthode simple
            mailService.sendSimpleEmail(to, subject, htmlContent);
        } else {
            System.out.println("❌ Pas d'accès direct au MailSender pour le test");
        }
    }

    /**
     * Supprime un événement et notifie les utilisateurs inscrits
     */
    @Override
    @Transactional
    public void deleteEvenement(Long id) {
        System.out.println("⚙️ Début de la suppression de l'événement ID : " + id);

        Evenement event = evenementRepository.findById(id).orElse(null);
        if (event == null) {
            System.out.println("❌ Événement non trouvé, ID : " + id);
            return;
        }

        // S'assurer que les inscriptions sont chargées
        if (event.getInscriptions() != null && !event.getInscriptions().isEmpty()) {
            System.out.println("📋 Nombre d'inscriptions à l'événement : " + event.getInscriptions().size());
            notifyUsersAboutDeletion(event);
        } else {
            System.out.println("📋 Aucune inscription trouvée pour cet événement, pas de notification nécessaire");
        }

        evenementRepository.deleteById(id);
        System.out.println("✅ Événement supprimé avec succès");
    }

    /**
     * Envoie des notifications aux utilisateurs concernant la suppression de l'événement
     */
    private void notifyUsersAboutDeletion(Evenement event) {
        String subject = "❌ Annulation de l'événement " + event.getTitre();
        System.out.println("📨 Préparation de l'envoi de notifications d'annulation...");

        // Filtrer les inscriptions avec des utilisateurs valides
        List<InscriptionEvenement> inscriptionsValides = event.getInscriptions().stream()
                .filter(inscription -> inscription.getUser() != null && inscription.getUser().getEmail() != null)
                .collect(Collectors.toList());

        System.out.println("📨 Nombre d'inscriptions valides : " + inscriptionsValides.size());

        for (InscriptionEvenement inscription : inscriptionsValides) {
            try {
                String email = inscription.getUser().getEmail();
                String nom = inscription.getUser().getNom() != null ? inscription.getUser().getNom() : "Utilisateur";

                System.out.println("📧 Préparation de l'email d'annulation pour : " + email);

                String content = "<p>Bonjour <strong>" + nom + "</strong>,</p>" +
                        "<p>L'événement <strong>" + event.getTitre() + "</strong> prévu le " +
                        (event.getDateEvenement() != null ? event.getDateEvenement().format(DATE_FORMATTER) : "date non définie") +
                        " a été <strong>annulé</strong>.</p>" +
                        "<p>Nous vous prions de nous excuser pour ce désagrément.</p>" +
                        "<p>L'équipe FitMind</p>";

                // Envoyer l'email
                mailService.sendEmailWithInlineImage(email, subject, content);
                System.out.println("✅ Email d'annulation envoyé avec succès à : " + email);
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'envoi d'email d'annulation : " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("✅ Notifications d'annulation envoyées avec succès");
    }

    /**
     * Met à jour périodiquement l'état des événements
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void mettreAJourEtatsEvenements() {
        List<Evenement> events = evenementRepository.findAll();
        int count = 0;

        for (Evenement event : events) {
            EtatEvent nouvelEtat = calculerEtat(event.getDateEvenement(), event.getDateFin());
            if (event.getEtatEvent() != nouvelEtat) {
                event.setEtatEvent(nouvelEtat);
                evenementRepository.save(event);
                count++;
            }
        }

        if (count > 0) {
            System.out.println("🔄 " + count + " événements ont été mis à jour automatiquement");
        }
    }

    /**
     * Récupère les recommandations d'événements pour un utilisateur
     */
    @Override
    public List<Evenement> getRecommandations(Long idUser) {
        System.out.println("⚙️ Récupération des recommandations pour l'utilisateur ID : " + idUser);

        List<Evenement> history = evenementRepository.findHistoriqueByUser(idUser);
        List<Evenement> candidats = evenementRepository.findAllNotRegisteredByUser(idUser);

        System.out.println("📋 Historique : " + history.size() + " événements, Candidats : " + candidats.size() + " événements");

        Map<String, Object> payload = new HashMap<>();
        payload.put("history", nettoyerChamps(history));
        payload.put("candidats", nettoyerChamps(candidats));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            System.out.println("🔍 Envoi des données au service de recommandation...");
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:5005/recommend", request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Evenement> recommendations = parseRecommendedEvents(response.getBody());
                System.out.println("✅ " + recommendations.size() + " recommandations reçues");
                return recommendations;
            } else {
                System.err.println("❌ Erreur du service de recommandation : " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la communication avec le service de recommandation : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("⚠️ Retour d'une liste vide de recommandations");
        return Collections.emptyList();
    }

    /**
     * Nettoie les champs des événements avant envoi au service de recommandation
     */
    public List<Evenement> nettoyerChamps(List<Evenement> events) {
        for (Evenement e : events) {
            if (e.getTitre() != null) e.setTitre(e.getTitre().trim());
            if (e.getLieu() != null) e.setLieu(e.getLieu().trim());
            if (e.getTypeEvenement() != null) e.setTypeEvenement(TypeEvenement.valueOf(e.getTypeEvenement().name().trim()));
        }
        return events;
    }

    /**
     * Parse la réponse JSON du service de recommandation
     */
    private List<Evenement> parseRecommendedEvents(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> rawList;
        List<Evenement> result = new ArrayList<>();

        try {
            rawList = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> map : rawList) {
                Evenement e = new Evenement();
                if (map.containsKey("idEvenement")) e.setIdEvenement(Long.parseLong(map.get("idEvenement").toString()));
                e.setTitre((String) map.getOrDefault("titre", ""));
                e.setLieu((String) map.getOrDefault("lieu", ""));
                e.setDescription((String) map.getOrDefault("description", ""));
                e.setImage((String) map.getOrDefault("image", ""));
                if (map.containsKey("capaciteMax")) e.setCapaciteMax(Integer.parseInt(map.get("capaciteMax").toString()));
                if (map.containsKey("typeEvenement")) e.setTypeEvenement(TypeEvenement.valueOf(map.get("typeEvenement").toString()));
                if (map.containsKey("etatEvent")) e.setEtatEvent(EtatEvent.valueOf(map.get("etatEvent").toString()));
                if (map.containsKey("dateEvenement")) e.setDateEvenement(LocalDateTime.parse(map.get("dateEvenement").toString()));
                if (map.containsKey("dateFin")) e.setDateFin(LocalDateTime.parse(map.get("dateFin").toString()));
                result.add(e);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du parsing des recommandations : " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}