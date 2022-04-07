package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Manages the interface used when the exam is in progress (player_monitor)
 */
public class PlayerMonitorController {

    @FXML
    private GridPane playerMonitor;

    @FXML
    private TextArea commentTextArea;


    /**
     * Default constructor
     */
    public PlayerMonitorController(){}

    /**
     * Constructor of PlayerMonitorController
     *
     * @param owner the window which created the monitor
     */
    public PlayerMonitorController(Window owner) {
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
     * Save a comment written in the comment section with the date
     */
    @FXML
    private void addComment(){
        System.out.println(this.commentTextArea.getText());
    }
}
