package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private Duration duration;

    @Getter
    private List<Sequence> sequencesList;


    /**
     * Default constructor of the MetaSequence class.
     */
    public MetaSequence() {
        this("MetaSequence");
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
    public void removeSequence(Sequence sequence) {
        if (this.sequencesList.remove(sequence)) {
            this.duration = this.duration.minus(sequence.getDuration());
        }
    }


    /**
     *  Compute the duration of the meta sequence and its sequences.
     */
    public void computeDuration(){
        Duration duration = Duration.ofSeconds(0);
        for (Sequence sequence : sequencesList) {
            sequence.computeDuration();
            duration = duration.plus(sequence.getDuration());
        }

        this.duration = duration;
    }


    /**
     * Setter for the List of sequences.
     *
     * @param sequenceList The list of sequences to set.
     */
    public void setSequencesList(List<Sequence> sequenceList){
        this.sequencesList = sequenceList;
        this.computeDuration();
    }


    /**
     * Overrides the toString method.
     *
     * @return The name of the meta sequence.
     */
    @Override
    public String toString() {
        return name;
    }


    /**
     * Checks if two meta sequences are equals.
     *
     * @param o the meta sequence to compare with.
     * @return boolean true if the meta sequences are the same, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaSequence)) return false;
        MetaSequence that = (MetaSequence) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDuration(), that.getDuration()) && Objects.equals(getSequencesList(), that.getSequencesList());
    }
}
