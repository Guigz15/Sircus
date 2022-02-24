package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

//TODO: check if this class is useful because it looks like a clone of the MetaSequence class.
/**
 * Class used to store the results gathered by a meta sequence.
 */
public class Result {
    @Getter
    @Setter
    private String metaSequenceName;

    @Getter
    @Setter
    private Duration duration;

    @Getter
    @Setter
    private List<Sequence> sequencesList;

    /**
     * Default constructor of the Result class.
     */
    public Result() {
    }

    /**
     * Full constructor of the Result class.
     *
     * @param metaSequenceName of the use meta sequence.
     * @param duration of the used meta sequence.
     * @param sequencesList of the used sequences.
     */
    public Result(String metaSequenceName, Duration duration, List<Sequence> sequencesList) {
        this.metaSequenceName = metaSequenceName;
        this.duration = duration;
        this.sequencesList = sequencesList;
    }
}