package fr.polytech.sircus.controller;

import fr.polytech.sircus.model.Result;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

/**
 * Manages the interface used when the exam is in progress (player_monitor)
 */
public class PlayerMonitorController {

    @FXML
    private GridPane playerMonitor;

    @FXML
    private TextArea commentTextArea;

    /**
     * The Result to fill
     */
    private Result result;

    /**
     * Default constructor
     */
    public PlayerMonitorController(){
        this.result = new Result();
    }

    /**
     * Save a comment written in the comment section with the time
     */
    @FXML
    private void addComment(){
        this.result.addComment(this.commentTextArea.getText());
        this.commentTextArea.clear();
    }
}
