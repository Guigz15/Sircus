package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.utils.ImportMetaSeqXML;
import fr.polytech.sircus.utils.ImportSeqXML;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

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
    private Button importMetaButton;
    @FXML
    private Button modifySeqButton;
    @FXML
    private Button exportSeqButton;
    @FXML
    private Button importSeqButton;
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
                removeMetaButton.setDisable(false);

                if(SircusApplication.adminConnected){
                    exportMetaButton.setDisable(false);
                }

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
                removeSeqButton.setDisable(false);

                if(SircusApplication.adminConnected){
                    exportSeqButton.setDisable(false);
                }

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

    }

    /**
     * Export a sequence to XML format, for admin only
     */
    @FXML
    private void exportSeq(){
        // export is only available for admin
        if(SircusApplication.adminConnected){
            Sequence seq = seqListView.getSelectionModel().getSelectedItem();
            File file = new File(SircusApplication.dataSircus.getPath().getSeqPath() + seq.getName() + ".xml");
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + seq.toXML());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Export a metaSequence to XML format, for admin only
     */
    @FXML
    private void exportMeta(){
        // export is only available for admin
        if(SircusApplication.adminConnected){
            int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
            MetaSequence meta = listMetaSequence.get(index_Selected_MetaSequence);
            File file = new File(SircusApplication.dataSircus.getPath().getMetaPath() + meta.getName() + ".xml");
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + meta.toXML());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Import a sequence from a XML file
     */
    @FXML
    private void importSeq(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer sequence");
        fileChooser.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getSeqPath()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showOpenDialog(importSeqButton.getScene().getWindow());
        if (file != null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                // import and parse the file
                SAXParser saxParser = factory.newSAXParser();
                ImportSeqXML handler = new ImportSeqXML();
                saxParser.parse(file, handler);
                int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                listMetaSequence.get(index_Selected_MetaSequence).addSequence(handler.getSeq());

                // refresh list of sequence
                seqListView.getItems().clear();
                for (Sequence sequence : listMetaSequence.get(index_Selected_MetaSequence).getSequencesList()) {
                    seqListView.getItems().add(sequence);
                }

                // refresh preview timeline
                MetaSequence currentMetaSequence = listMetaSequence.get(index_Selected_MetaSequence);
                previewTimeline.removeAllMedia();
                previewTimeline.addMetaSequence(currentMetaSequence);
            }
            catch (ParserConfigurationException | SAXException | IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void importMeta(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer metaSequence");
        fileChooser.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getMetaPath()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showOpenDialog(importMetaButton.getScene().getWindow());
        if (file != null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                // import and parse the file
                SAXParser saxParser = factory.newSAXParser();
                ImportMetaSeqXML handler = new ImportMetaSeqXML();
                saxParser.parse(file, handler);
                SircusApplication.dataSircus.getMetaSequencesList().add(handler.getMeta());

                // refresh list of metaSequence
                this.listMetaSequence = SircusApplication.dataSircus.getMetaSequencesList();
                metaListView.getItems().clear();
                for (MetaSequence metaSequence : listMetaSequence) {
                    metaListView.getItems().add(metaSequence.getName());
                }
            }
            catch (ParserConfigurationException | SAXException | IOException e)
            {
                e.printStackTrace();
            }
        }
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