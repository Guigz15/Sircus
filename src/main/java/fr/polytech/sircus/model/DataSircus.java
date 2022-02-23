package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Objet permettant de contenir l'ensemble des meta sequences pour simplifier la serialisation
 */
public class DataSircus implements Serializable {

    /**
     * Numéro de version de la classe, nécessaire pour l'interface Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Liste des meta sequences de l'application
     */
    @Getter
    @Setter
    private List<MetaSequence> metaSequencesList;

    /**
     * Liste des lieux d'examen
     */
    @Getter
    @Setter
    private List<String> locationsList;

    /**
     * Constructeur de l'objet
     */
    public DataSircus() {
        this.metaSequencesList = new ArrayList<>();
    }

    /**
     * Ajoute un lieu a la liste des lieux
     *
     * @param location nouveau lieu a ajouter
     */
    public void setLieuxList(String location) {
        if (!this.locationsList.contains(location)) {
            this.locationsList.add(location);
        }
    }

    /**
     * Ajoute une meta sequence a la liste des meta sequences
     *
     * @param metaSequence la nouvelle meta sequence
     */
    public void saveMetaSeq(MetaSequence metaSequence) {
        this.metaSequencesList.add(metaSequence);
    }
}
