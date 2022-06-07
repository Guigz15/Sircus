package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * Represent a comment written in the monitor
 */
public class Comment {
    @Getter @Setter
    private String comment;

    @Getter @Setter
    private LocalTime time;

    /**
     * Create a comment with a specific time
     * @param comment The content of the comment
     * @param time The time of the comment
     */
    public Comment(String comment, LocalTime time){
        this.comment = comment;
        this.time = time;
    }

    /**
     * Create a comment with current time
     * @param comment The content of the comment
     */
    public Comment(String comment){
        this.comment = comment;
        this.time = LocalTime.now();
    }
}
