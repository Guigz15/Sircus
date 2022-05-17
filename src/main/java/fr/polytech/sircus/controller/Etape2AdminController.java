package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.ModifySeqPopUp;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import javafx.beans.value.ChangeListener;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Class Etape2AdminController : manages the metasequences settings page of the Admin
 */
public class Etape2AdminController implements Initializable {
    @FXML
    private ListView<String> metaListView;
    @FXML
    public ListView<Sequence> seqListView;
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

    //list of meta-sequences storage
    private List<MetaSequence> listMetaSequence;


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
        this.listMetaSequence = SircusApplication.dataSircus.getMetaSequencesList();
        for (MetaSequence metaSequence : listMetaSequence) {
            metaListView.getItems().add(metaSequence.getName());
        }

        metaListView.setStyle("-fx-font-size: 14pt;");

        //Defined action when the MetaSequence element selected is changed.
        metaListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                renameMetaButton.setDisable(false);
                exportMetaButton.setDisable(false);
                removeMetaButton.setDisable(false);
                //get the new medias lists to display it on screen
                int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                MetaSequence currentMetaSequence = listMetaSequence.get(index_Selected_MetaSequence);
                previewTimeline.removeAllMedia();
                previewTimeline.addMetaSequence(currentMetaSequence);

                //Defined the list of sequences.
                seqListView.getItems().clear();
                for (Sequence sequence : listMetaSequence.get(index_Selected_MetaSequence).getSequencesList()) {
                    seqListView.getItems().add(sequence);
                }
            }

        });

        /* initialize sequences listView */
        seqListView.setStyle("-fx-font-size: 14pt;");

        //Defined action when the Sequence element selected is changed.
        seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Sequence>() {
            @Override
            public void changed(ObservableValue<? extends Sequence> observableValue, Sequence s, Sequence t1) {
                //allow the buttons to change Sequence
                modifySeqButton.setDisable(false);
                exportSeqButton.setDisable(false);
                removeSeqButton.setDisable(false);

                //get the new medias lists to display it on screen
                int index_Selected_Sequence = seqListView.getSelectionModel().getSelectedIndex();
                int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                if(index_Selected_MetaSequence >= 0 && index_Selected_Sequence >=0) {
                    Sequence currentSequence = listMetaSequence.get(index_Selected_MetaSequence).getSequencesList().get(index_Selected_Sequence);
                    previewTimeline.removeAllMedia();
                    previewTimeline.addListMedia(currentSequence.getListMedias());
                }


            }

        });

        //----------------------------------------------------------------------//


        //set action when checkboxe is clicked.
        seqListView.setCellFactory(CheckBoxListCell.forListView(new Callback<Sequence, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Sequence item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                if(SircusApplication.adminConnected) {
                    if(item.getLock()){
                        observable.setValue(Boolean.TRUE);
                    }
                    observable.addListener((obs, wasSelected, isNowSelected) -> {
                        //If user select the chechbox
                        if (isNowSelected) {
                            item.setLock(Boolean.TRUE);
                            observable.setValue(Boolean.TRUE);
                        } else {
                            observable.setValue(Boolean.FALSE);
                            item.setLock(Boolean.FALSE);
                        }
                    });
                }
                return observable;
            }
            
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

        modifySeqButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_seq_popup.fxml"));
                    DialogPane dialogPane = fxmlLoader.load();
                    ModifySeqPopUp controller = fxmlLoader.getController();

                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Modification de séquence");
                    dialog.initModality(Modality.WINDOW_MODAL);
                    dialog.initOwner(modifySeqButton.getScene().getWindow());

                    Optional<ButtonType> clickedButton = dialog.showAndWait();
                    if (clickedButton.get() == ButtonType.FINISH) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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