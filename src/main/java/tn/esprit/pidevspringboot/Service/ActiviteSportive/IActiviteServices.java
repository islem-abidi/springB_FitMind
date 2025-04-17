package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;

import java.util.List;

public interface IActiviteServices  {

    public List<Activite> readAllActivite();
    public Activite readActivitee(long idA);
    public Activite addActivite(Activite activite);
    public Activite updateActivite(Activite activite);
    public void deleteActivite(long idA);

    List<Activite> getActivitesTendances();

    List<Activite> getActivitesTendancesAvecToutes();
}
