package tn.esprit.pidevspringboot.Service.Nutrition;

import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import java.util.List;

public interface IRendezVousServices {
    List<RendezVous> retrieveAllRendezVous();
    RendezVous retrieveRendezVous(Long idRendezVous);
    RendezVous addRendezVous(RendezVous rendezVous);
    RendezVous updateRendezVous(RendezVous rendezVous);
    RendezVous archiveRendezVous(Long idRendezVous);
    void updateStatutRendezVous(Long id, String statut);
    List<RendezVous> retrieveArchivedRendezVous();
    RendezVous restoreRendezVous(Long idRendezVous);

}
