package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Objet permettant de représenter une meta sequence
 */
public class MetaSequence implements Serializable {

    /**
     * Numéro de version de la classe, nécessaire pour l'interface Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Nom de la meta sequence
     */
    @Getter @Setter
    private String name;

    /**
     * Duree de la meta sequence
     */
    @Getter @Setter
    private Duration duration;

    /**
     * Liste des sequences contenues dans la meta sequence
     */
    @Getter @Setter
    private List<Sequence> listSequences;

    /**
     * Constructeur par défaut de l'objet meta sequence
     */
    public MetaSequence()
        {
        this.name = "MetaSequence";
        this.duration = Duration.ZERO;
        }

    /**
     * Constructeur avec parametre de l'objet meta sequence
     * @param name nom de la meta sequence
     */
    public MetaSequence(String name) {
        this.name          = name;
        this.duration      = Duration.ZERO;
        this.listSequences = new ArrayList<>();
    }

    /**
     * Verifie si deux meta sequences sont egales
     * @param object la meta sequence a comparer
     * @return boolean vrai si egal faux sinon
     */
    public boolean equals ( Object object )
        {
        if ( object instanceof MetaSequence )
            {
            return this.name.toUpperCase().equals ( (( MetaSequence ) object).name.toUpperCase () );
            }
        if ( object instanceof String )
            {
            return this.name.toUpperCase().equals ( ( ( String ) object ).toUpperCase () );
            }
        return false;
        }

    /**
     * Surcharge de la methode toString
     * @return name le nom
     */
    public String toString() { return name; }

    /**
     * Ajoute une sequence a la meta sequence et modifie en consequence la duree
     * @param sequence la sequence a ajouter
     */
    public void addSequence(Sequence sequence)
        {
        this.listSequences.add ( sequence );
        this.duration = this.duration.plus ( sequence.getDuration ());
        }

    /**
     * Supprime une sequence de la meta sequence et modifie en consequence la duree
     * @param sequence la sequence a supprimer
     */
    public void remSequence(Sequence sequence)
        {
        if ( this.listSequences.remove ( sequence ) )
            {
            this.duration = this.duration.minus ( sequence.getDuration () );
            }
        }
}
