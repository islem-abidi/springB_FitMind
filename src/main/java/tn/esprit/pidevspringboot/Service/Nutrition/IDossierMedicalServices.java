package tn.esprit.pidevspringboot.Service.Nutrition;

import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;
import java.util.List;

public interface IDossierMedicalServices {
        List<DossierMedical> retrieveAllDossiers();
        DossierMedical retrieveDossier(Long idDossier);
        DossierMedical addDossier(DossierMedical dossierMedical);
        DossierMedical updateDossier(DossierMedical dossierMedical);
        DossierMedical archiveDossier(Long idDossier);
        DossierMedical updateRdvRecommande(Long idDossier, boolean rdvRecommande);
        DossierMedical restoreDossier (Long idDossier);
        List<DossierMedical> retrieveArchivedDossiers();
}
