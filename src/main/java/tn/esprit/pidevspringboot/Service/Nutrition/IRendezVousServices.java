package tn.esprit.pidevspringboot.Service.Nutrition;

import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import java.util.List;

public interface IRendezVousServices {
    List<RendezVous> retrieveAllRendezVous();
    RendezVous retrieveRendezVous(Long idRendezVous);
    RendezVous addRendezVous(RendezVous rendezVous);
    RendezVous updateRendezVous(RendezVous rendezVous);
}
