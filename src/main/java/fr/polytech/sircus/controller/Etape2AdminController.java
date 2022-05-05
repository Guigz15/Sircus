package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.utils.MetaSequenceContainer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Class Etape2AdminController : manages the metasequences settings page of the Admin
 */
public class Etape2AdminController implements Initializable {
    @FXML
    private ListView<String> metaListView;
    @FXML
    public ListView<String> seqListView;
    @FXML
    private Button renameMetaButton;
    @FXML
    private Button exportMetaButton;
    @FXML
    private Button modifySeqButton;
    @FXML
    private Button exportSeqButton;
    @FXML
    private Button removeMetaButton;
    @FXML
    private Button removeSeqButton;
    @FXML
    private Button doMixButton;
    @FXML
    private Button startMixButton;
    @FXML
    private Button cancelMixButton;
    @FXML
    private Button previous;
    @FXML
    private Button next;
    @FXML
    private PreviewTimeline previewTimeline;



    /* initialize listViews */
    String [] metasequences = {"Metaséquence 1", "Metaséquence 2", "Metaséquence 3", "Metaséquence 4", "Metaséquence 5",
            "Metaséquence 6", "Metaséquence 7", "Metaséquence 8", "Metaséquence 9", "Metaséquence 10", "Metaséquence 11"};
    String [] sequences = {"Séquence 1", "Séquence 2", "Séquence 3", "Séquence 4", "Séquence 5",
            "Séquence 6", "Séquence 7", "Séquence 8", "Séquence 9", "Séquence 10", "Séquence 11"};

    /**
     * Function initialize which dynamically ends the listViews initialization
     * and enable corresponding buttons when metasequences or sequences are selected
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* initialize metasequences listView */
        metaListView.getItems().addAll(metasequences);

        previewTimeline.addMetaSequence(new MetaSequenceContainer().getMetaSequences().get(0));
        metaListView.setStyle("-fx-font-size: 14pt;");
        metaListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                renameMetaButton.setDisable(false);
                exportMetaButton.setDisable(false);
                removeMetaButton.setDisable(false);
            }

        });

        /* initialize sequences listView */
        seqListView.getItems().addAll(sequences);
        seqListView.setStyle("-fx-font-size: 14pt;");
        seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                modifySeqButton.setDisable(false);
                exportSeqButton.setDisable(false);
                removeSeqButton.setDisable(false);
            }

        });

        //----------------------------------------------------------------------//

        seqListView.setCellFactory(CheckBoxListCell.forListView((Callback<String, ObservableValue<Boolean>>) item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                //TODO lock the selected sequences in order that they don't mix with others
                // when you push the mix button
            });
            return observable;
        }));

        /* set doMixButton proprieties */
        doMixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancelMixButton.setVisible(true);
                cancelMixButton.setDisable(false);
                startMixButton.setVisible(true);
                startMixButton.setDisable(false);
                doMixButton.setVisible(false);
                doMixButton.setDisable(true);
            }
        });

        /* set cancelMixButton proprieties */
        cancelMixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancelMixButton.setVisible(false);
                cancelMixButton.setDisable(true);
                startMixButton.setVisible(false);
                startMixButton.setDisable(true);
                doMixButton.setVisible(true);
                doMixButton.setDisable(false);
            }
        });

        /* set startMixButton proprieties */
        startMixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancelMixButton.setVisible(false);
                cancelMixButton.setDisable(true);
                startMixButton.setVisible(false);
                startMixButton.setDisable(true);
                doMixButton.setVisible(true);
                doMixButton.setDisable(false);
            }
        });

    }

    @FXML
    private void previousPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/main_window.fxml")));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) previous.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void nextPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/player_monitor.fxml")));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) next.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}