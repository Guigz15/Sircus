package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * Manages the interface used when the exam is in progress (player_monitor)
 */
public class PlayerMonitorController {

    // Manage the stage of the media
    private Stage monitorStage = null;

    /**
     * Constructor of PlayerMonitorController
     *
     * @param owner the window which created the monitor
     */
    public PlayerMonitorController(Window owner){
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/player_monitor.fxml")));
        fxmlLoader.setController(this);

        try {
            Scene monitorScene = new Scene(fxmlLoader.load(), 920, 620);
            monitorStage = new Stage();
            monitorStage.initOwner(owner);
            monitorStage.setMinWidth(920);
            monitorStage.setMinHeight(620);
            monitorStage.setScene(monitorScene);
            monitorStage.setTitle("Moniteur");
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

    }
}
