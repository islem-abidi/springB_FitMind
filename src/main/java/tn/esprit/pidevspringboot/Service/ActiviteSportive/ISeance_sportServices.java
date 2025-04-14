package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Seance_sport;

import java.util.List;

public interface ISeance_sportServices {

    public List<Seance_sport> readAllSeance_sport();
    public Seance_sport readSeance_sport(long idS);
    public Seance_sport addSeance_sport(Seance_sport seance_sport);
    public Seance_sport updateSeance_sport(Seance_sport seance_sport);
    public void deleteSeance_sport(long idS);

    List<Seance_sport> getSeancesByActivite(Long id);
}
