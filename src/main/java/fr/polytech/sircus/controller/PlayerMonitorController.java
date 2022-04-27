package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Result;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

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
     * Constructor of PlayerMonitorController
     *
     * @param owner the window which created the monitor
     * @param result the result object to be filled
     */
    public PlayerMonitorController(Window owner, Result result) {
        this.result = result;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/player_monitor.fxml"));
            Parent rootParent = fxmlLoader.load();
            Stage monitorStage = new Stage();
            monitorStage.setMinWidth(920);
            monitorStage.setMinHeight(620);
            monitorStage.setTitle("Moniteur");
            monitorStage.initOwner(owner);
            monitorStage.setScene(new Scene(rootParent));
            monitorStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
