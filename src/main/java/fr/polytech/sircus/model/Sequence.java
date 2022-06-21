package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a sequence
 */
public class Sequence implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private Duration minDuration;

    @Getter @Setter
    private Duration maxDuration;

    @Getter @Setter
    private Duration duration;

    @Getter @Setter
    private List<Media> listMedias;

    @Getter @Setter
    private Boolean lock;

    /**
     * The constructor
     *
     * @param name Sequence's name
     */
    public Sequence(String name) {
        this.name = name;
        this.minDuration = Duration.ofSeconds(0);
        this.maxDuration = Duration.ofSeconds(0);
        this.duration = Duration.ofSeconds(0);
        this.listMedias = new ArrayList<>();
        this.lock = true;
    }

    /**
     * The copy constructor
     *
     * @param sequence Sequence to copy
     */
    public Sequence(Sequence sequence) {
        this.name = sequence.getName();
        this.minDuration = sequence.getMinDuration();
        this.maxDuration = sequence.getMaxDuration();
        this.listMedias = new ArrayList<>();
        for (Media media : sequence.getListMedias())
            this.listMedias.add(new Media(media));
        this.lock = sequence.getLock();
    }

    /**
     * Add a media to this sequence
     *
     * @param media Media to add
     */
    public void addMedia(Media media) {
        this.listMedias.add(media);
        this.minDuration = this.minDuration.plus(media.getMinDuration());
        this.maxDuration = this.maxDuration.plus(media.getMaxDuration());

        if (media.getInterstim() != null) {
            this.minDuration = this.minDuration.plus(media.getInterstim().getMinDuration());
            this.maxDuration = this.maxDuration.plus(media.getInterstim().getMaxDuration());
        }
    }

    /**
     * Remove a media from this sequence
     *
     * @param media Media to remove
     */
    public void removeMedia(Media media) {
        if (this.listMedias.remove(media)) {
            this.minDuration = this.minDuration.minus(media.getMinDuration());
            this.maxDuration = this.maxDuration.minus(media.getMaxDuration());

            if (media.getInterstim() != null) {
                this.minDuration = this.minDuration.minus(media.getInterstim().getMinDuration());
                this.maxDuration = this.maxDuration.minus(media.getInterstim().getMaxDuration());
            }
        }
    }

    /**
     *  Compute the min max duration of the sequence.
     */
    public void computeMinMaxDurations(){
        Duration minDuration = Duration.ofSeconds(0);
        Duration maxDuration = Duration.ofSeconds(0);

        for (Media media : listMedias) {
            minDuration = minDuration.plus(media.getMinDuration());
            maxDuration = maxDuration.plus(media.getMaxDuration());
            if (media.getInterstim() != null) {
                minDuration = minDuration.plus(media.getInterstim().getMinDuration());
                maxDuration = maxDuration.plus(media.getInterstim().getMaxDuration());
            }
        }

        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    /**
     *  Compute the duration of the sequence.
     */
    public void computeDuration(){
        Duration duration = Duration.ofSeconds(0);

        for (Media media : listMedias) {
            duration = duration.plus(media.getDuration());
            if (media.getInterstim() != null)
                duration = duration.plus(media.getInterstim().getDuration());
        }

        this.duration = duration;
    }

    /**
     * Override the method toString to display only the name
     *
     * @return Sequence's name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if two sequences are equals.
     *
     * @param o the sequence to compare with.
     * @return boolean true if the sequences are the same, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sequence)) return false;
        Sequence sequence = (Sequence) o;
        return Objects.equals(getName(), sequence.getName()) && Objects.equals(getMinDuration(), sequence.getMinDuration())
                && Objects.equals(getMaxDuration(), sequence.getMaxDuration()) && Objects.equals(getListMedias(), sequence.getListMedias());
    }

    /**
     * Convert a sequence to XML
     * @return XML
     */
    public String toXML(){
        String XML = "<sequence name=\"" + name.replace(" ", "%20") + "\" minDuration=\"" + minDuration
                + "\" maxDuration=\"" + maxDuration + "\" lock=\"" + lock + "\">\n" + "<listMedia>\n";
        for(int i=0; i<listMedias.size(); i++){
            XML += listMedias.get(i).toXML();
        }
        XML += "</listMedia>\n" +
                "</sequence>\n";
        return XML;
    }

    @Override
    public Sequence clone() {
        try {
            Sequence clone = (Sequence) super.clone();
            clone.listMedias = List.copyOf(this.listMedias);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
