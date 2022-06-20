package fr.polytech.sircus.model;

import fr.polytech.sircus.SircusApplication;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

    @Getter @Setter
    private Duration minDuration;

    @Getter @Setter
    private Duration maxDuration;

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
        this.minDuration = Duration.ZERO;
        this.maxDuration = Duration.ZERO;
        this.sequencesList = new ArrayList<>();
    }


    /**
     * Adds a sequence to the meta sequence and adds its duration to the meta sequence duration.
     *
     * @param sequence The sequence to add.
     */
    public void addSequence(Sequence sequence) {
        this.sequencesList.add(sequence);
        this.minDuration = this.minDuration.plus(sequence.getMinDuration());
        this.maxDuration = this.maxDuration.plus(sequence.getMaxDuration());
    }


    /**
     * Removes a sequence from the meta sequence and removes duration to the meta sequence duration.
     *
     * @param sequence The sequence to remove.
     */
    public void removeSequence(Sequence sequence) {
        if (this.sequencesList.remove(sequence)) {
            this.minDuration = this.minDuration.minus(sequence.getMinDuration());
            this.maxDuration = this.maxDuration.minus(sequence.getMaxDuration());
        }
    }


    /**
     *  Compute the duration of the meta sequence and its sequences.
     */
    public void computeDurations(){
        Duration minDuration = Duration.ofSeconds(0);
        Duration maxDuration = Duration.ofSeconds(0);
        for (Sequence sequence : sequencesList) {
            sequence.computeDurations();
            minDuration = minDuration.plus(sequence.getMinDuration());
            maxDuration = maxDuration.plus(sequence.getMaxDuration());
        }

        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }


    /**
     * Setter for the List of sequences.
     *
     * @param sequenceList The list of sequences to set.
     */
    public void setSequencesList(List<Sequence> sequenceList){
        this.sequencesList = sequenceList;
        this.computeDurations();
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
        return Objects.equals(getName(), that.getName()) && Objects.equals(getMinDuration(), that.getMinDuration())
                && Objects.equals(getMaxDuration(), that.getMaxDuration()) && Objects.equals(getSequencesList(), that.getSequencesList());
    }

    /**
     * Convert a metaSequence to XML
     * @return XML
     */
    public String toXML(){
        String XML = "<metaSequence>\n" +
                "<name>" + name.replace(" ", "%20") + "</name>\n" +
                "<minDuration>" + minDuration + "</minDuration>\n" +
                "<maxDuration>" + maxDuration + "</maxDuration>\n" +
                "<listSequence>\n";
        for (Sequence sequence : sequencesList) {
            XML += "<sequence>" + sequence.getName().replace(" ", "%20") + ".xml</sequence>\n";

            File file = new File(SircusApplication.dataSircus.getPath().getSeqPath() + sequence.getName() + ".xml");
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + sequence.toXML());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        XML += "</listSequence>\n" +
                "</metaSequence>\n";
        return XML;
    }
}
