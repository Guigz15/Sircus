package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;

/**
 * Objet permettant de représenter un media (image ou video)
 */
public class Media implements Serializable {

    /**
     * Numéro de version de la classe, nécessaire pour l'interface Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Nom du media
     */
    @Getter @Setter
    private String name;

    /**
     * Nom du fichier
     */
    @Getter @Setter
    private String filename;

    /**
     * Duree d'affichage du media
     */
    @Getter @Setter
    private Duration duration;

    /**
     * Type du media (image ou video)
     */
    @Getter @Setter
    private TypeMedia type;

    /**
     * Interstim apres chaque media
     */
    @Getter @Setter
    private Media interStim;

    /**
     * Booleen verrouille
     */
    @Getter @Setter
    private Boolean verr;

    /**
     * Constructeur de l'objet Media
     * @param name Nom du media
     * @param filename nom du fichier
     * @param duration Duree du media
     * @param type Type du media
     */
    public Media(String name, String filename, Duration duration, TypeMedia type, Media interStim) {
        this.name = name;
        this.filename = filename;
        this.duration = duration;
        this.type = type;
        this.interStim = interStim;
        this.verr = true;
    }

    /**
     * Constructeur de l'objet Media par copie
     * @param media media a copier
     */
    public Media(Media media) {
        this.name = media.getName();
        this.filename = media.getFilename();
        this.duration = media.getDuration();
        this.type = media.getType();
        this.interStim = media.getInterStim();
        this.verr = media.getVerr();
    }

    /**
     * Surcharge de la methode toString
     * @return name le nom
     */
    public String toString() { return name; }
}
