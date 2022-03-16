package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a meta sequence.
 */
public class MetaSequence implements Serializable {
    /**
     * Version number of the class used by the Serializable interface.
     */
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Duration duration;

    @Getter
    @Setter
    private List<Sequence> sequencesList;

    /**
     * Default constructor of the MetaSequence class.
     */
    public MetaSequence() {
        this.name = "MetaSequence";
        this.duration = Duration.ZERO;
    }

    /**
     * Alternative constructor for the MetaSequence class.
     *
     * @param name of the meta sequence.
     */
    public MetaSequence(String name) {
        this.name = name;
        this.duration = Duration.ZERO;
        this.sequencesList = new ArrayList<>();
    }

    /**
     * Checks if two meta sequences are equal.
     *
     * @param object the meta sequence to compare with.
     * @return boolean true if the meta sequences are the same, false otherwise.
     */
    public boolean equals(Object object) {
        if (object instanceof MetaSequence) {
            return this.name.equalsIgnoreCase(((MetaSequence) object).name);
        }
        if (object instanceof String) {
            return this.name.equalsIgnoreCase(((String) object));
        }
        return false;
    }

    /**
     * Adds a sequence to the meta sequence and adds its duration to the meta sequence duration.
     *
     * @param sequence The sequence to add.
     */
    public void addSequence(Sequence sequence) {
        this.sequencesList.add(sequence);
        this.duration = this.duration.plus(sequence.getDuration());
    }

    /**
     * Removes a sequence from the meta sequence and removes duration to the meta sequence duration.
     *
     * @param sequence The sequence to remove.
     */
    public void remSequence(Sequence sequence) {
        if (this.sequencesList.remove(sequence)) {
            this.duration = this.duration.minus(sequence.getDuration());
        }
    }

    /**
     * Overrides the toString method.
     *
     * @return The name of the meta sequence.
     */
    public String toString() {
        return name;
    }
}
