package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to store the results gathered by a meta sequence.
 */
public class Result {
    @Getter @Setter
    private String metaSequenceName;

    @Getter @Setter
    private Duration duration;

    @Getter @Setter
    private List<Sequence> sequencesList;

    @Getter @Setter
    private List<Comment> comments;

    /**
     * Default constructor of the Result class.
     */
    public Result() {
        this.comments = new ArrayList<>();
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
        this.comments = new ArrayList<>();
    }

    /**
     * Add a new comment to the result
     * @param comment The content of the comment
     */
    public void addComment(String comment){
        this.comments.add(new Comment(comment));
    }
}