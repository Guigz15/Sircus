package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Objet permettant de représenter une sequence
 */
public class Sequence implements Serializable {

    /**
     * Numéro de version de la classe, nécessaire pour l'interface Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Nom de la sequence
     */
    @Getter @Setter
    private String name;

    /**
     * Duree de la sequence
     */
    @Setter
    private Duration duration;

    /**
     * Liste des medias de la sequence
     */
    @Getter @Setter
    private List<Media> listMedias;

    /**
     * Booleen verrouille
     */
    @Getter @Setter
    private Boolean verr;

    /**
     * Constructeur de l'objet sequence
     * @param name nom de la sequence
     */
    public Sequence(String name){
        this.name = name;
        this.duration = Duration.ZERO;
        this.listMedias = new ArrayList<>();
        this.verr = true;
    }

    /**
     * Constructeur de l'objet sequence par copie
     * @param sequence Sequence a copier
     */
    public Sequence(Sequence sequence){
        this.name = sequence.getName();
        this.duration = sequence.getDuration();
        this.listMedias = sequence.getListMedias();
        this.verr = sequence.getVerr();
    }

    /**
     * Retourne la duree de la sequence
     * @return duration la duree
     */
    public Duration getDuration() {
        Duration duration = Duration.ofSeconds(0);
        for(int index = 0; index < listMedias.size(); index++){
            duration = duration.plus(listMedias.get(index).getDuration());
            if(listMedias.get(index).getInterStim() != null){
                duration = duration.plus(listMedias.get(index).getInterStim().getDuration());
            }
        }
        return duration;
    }

    /**
     * Ajoute un media a la liste des medias de la sequence
     * @param media le media a ajouter
     */
    public void addMedia(Media media)
    {
        this.listMedias.add ( media );
        this.setDuration(this.getDuration());
    }

    /**
     * Supprime un media a la liste des medias de la sequence
     * @param media le media a supprimer
     */
    public void remMedia(Media media)
    {
        if ( this.listMedias.remove ( media ) ) {
            this.setDuration(this.getDuration());
        }
    }

    /**
     * Surcharge de la methode toString
     * @return name le nom
     */
    public String toString() { return name; }
}
