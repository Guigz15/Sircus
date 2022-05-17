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
public class Sequence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String name;

    @Getter
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
        this.duration = Duration.ZERO;
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
        this.duration = sequence.getDuration();
        this.listMedias = sequence.getListMedias();
        this.lock = sequence.getLock();
    }

    /**
     * Add a media to this sequence
     *
     * @param media Media to add
     */
    public void addMedia(Media media) {
        this.listMedias.add(media);
        this.duration = this.duration.plus(media.getDuration());
        //computeDuration();
    }

    /**
     * Remove a media from this sequence
     *
     * @param media Media to remove
     */
    public void removeMedia(Media media) {
        if (this.listMedias.remove(media)) {
            //computeDuration();
            this.duration = this.duration.minus(media.getDuration());
        }
    }

    /**
     *  Compute the duration of the sequence.
     */
    public void computeDuration(){
        Duration duration = Duration.ofSeconds(0);
        for (Media listMedia : listMedias) {
            duration = duration.plus(listMedia.getDuration());
            if (listMedia.getInterStim() != null) {
                duration = duration.plus(listMedia.getInterStim().getDuration());
            }
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
        return Objects.equals(getName(), sequence.getName()) && Objects.equals(getDuration(), sequence.getDuration()) && Objects.equals(getListMedias(), sequence.getListMedias());
    }

    /**
     * Convert a sequence to XML
     * @return XML
     */
    public String toXML(){
        //TODO replace the space in string by %20
        String XML = "<sequence name=\"" + name + "\" duration=\"" + duration
                + "\" lock=\"" + lock + "\">\n" + "<listMedia>\n";
        for(int i=0; i<listMedias.size(); i++){
            XML += listMedias.get(i).toXML();
        }
        XML += "</listMedia>\n" +
                "</sequence>\n";
        return XML;
    }
}
