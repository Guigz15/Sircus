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

    //The duration of the whole experiment. Corresponds to the sum of all the duration of the Meta-Sequences
    @Getter @Setter
    private Duration duration;

    @Getter @Setter
    private List<MetaSequence> metaSequencesList;

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
     * @param duration of the whole experiment.
     * @param metaSequencesList of the used meta sequence.
     */
    public Result(Duration duration, List<MetaSequence> metaSequencesList) {
        this.duration = duration;
        this.metaSequencesList = metaSequencesList;
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