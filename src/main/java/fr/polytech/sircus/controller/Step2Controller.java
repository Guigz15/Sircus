package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.ModifySeqPopUp;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.utils.ImportMetaSeqXML;
import fr.polytech.sircus.utils.ImportSeqXML;
import fr.polytech.sircus.utils.ItemSequence;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.xml.sax.SAXException;
import javafx.beans.binding.Bindings;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

/**
 * Class Etape2AdminController : manages the metasequences settings page of the Admin
 */
public class Step2Controller implements Initializable {
    @FXML
    private Button importMetaButtonUser;
    @FXML
    private BorderPane adminMetaButtons;
    @FXML
    private ListView<MetaSequence> metaListView;
    @FXML
    private Button addMetaButton;
    @FXML
    private Button removeMetaButton;
    @FXML
    private Button renameMetaButton;
    @FXML
    private Button importMetaButton;
    @FXML
    private Button exportMetaButton;
    @FXML
    private BorderPane adminSeqButtons;
    @FXML
    private Button importSeqButtonUser;
    @FXML
    public ListView<ItemSequence> seqListView;
    @FXML
    private Button addSeqButton;
    @FXML
    private Button removeSeqButton;
    @FXML
    private Button modifySeqButton;
    @FXML
    private Button importSeqButton;
    @FXML
    private Button exportSeqButton;
    @FXML
    private Text statsTitle;
    @FXML
    private GridPane statsForMeta;
    @FXML
    private Label nbSeqLabelForMeta;
    @FXML
    private Label nbMediaLabelForMeta;
    @FXML
    private Label nbInterstimLabelForMeta;
    @FXML
    private Label totalDurationLabelForMeta;
    @FXML
    private GridPane statsForSeq;
    @FXML
    private Label nbMediaLabelForSeq;
    @FXML
    private Label nbInterstimLabelForSeq;
    @FXML
    private Label totalDurationLabelForSeq;
    @FXML
    private Button doMixButton;
    @FXML
    private HBox mixBoxButtons;
    @FXML
    private Button startMixButton;
    @FXML
    private Button cancelMixButton;
    @FXML
    private Text previewText;
    @FXML
    private PreviewTimeline previewTimeline;
    @FXML
    private Button previous;
    @FXML
    private Button next;

    //DataFormat use for drag and drop sequences in listView.
    public static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    //index of last meta-sequence selected. Is set with -1 if there are no selected meta-sequence
    int index_Selected_MetaSequence = -1;

    //index of last sequence selected. Is set with -1 if there are no selected sequence
    int index_Selected_Sequence = -1;

    /**
     * Function initialize which dynamically ends the listViews initialization
     * and enable corresponding buttons when metasequences or sequences are selected
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* initialize button to mix randomly the list of sequences depends if we are connected on admin mode */
        if (SircusApplication.adminConnected) {
            importMetaButtonUser.setVisible(false);
            adminMetaButtons.setVisible(true);
            importSeqButtonUser.setVisible(false);
            adminSeqButtons.setVisible(true);
            doMixButton.setVisible(true);
        }

        if (SircusApplication.dataSircus.getMetaSequencesList().size() == 0) {
            next.setDisable(true);
        }

        /* initialize metasequences listView */
        metaListView.setStyle("-fx-font-size: 14pt;");
        metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));

        //Defined action when the MetaSequence element selected is changed.
        metaListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerMetaSequence());

        /* initialize sequences listView */
        seqListView.setStyle("-fx-font-size: 14pt;");

        //Defined action when the Sequence element selected is changed.
        seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerSequence());

        renameMetaButton.disableProperty().bind(Bindings.createBooleanBinding(() -> metaListView.getSelectionModel().isEmpty(), metaListView.getSelectionModel().getSelectedItems()));
        renameMetaButton.setOnAction(actionEvent -> {
            metaListView.setEditable(true);
            metaListView.edit(metaListView.getSelectionModel().getSelectedIndex());
            metaListView.setEditable(false);
        });
        removeMetaButton.disableProperty().bind(Bindings.createBooleanBinding(() -> metaListView.getSelectionModel().isEmpty(), metaListView.getSelectionModel().getSelectedItems()));
        exportMetaButton.disableProperty().bind(Bindings.createBooleanBinding(() -> metaListView.getSelectionModel().isEmpty(), metaListView.getSelectionModel().getSelectedItems()));

        modifySeqButton.disableProperty().bind(Bindings.createBooleanBinding(() -> seqListView.getSelectionModel().isEmpty(), seqListView.getSelectionModel().getSelectedItems()));
        removeSeqButton.disableProperty().bind(Bindings.createBooleanBinding(() -> seqListView.getSelectionModel().isEmpty(), seqListView.getSelectionModel().getSelectedItems()));
        exportSeqButton.disableProperty().bind(Bindings.createBooleanBinding(() -> seqListView.getSelectionModel().isEmpty(), seqListView.getSelectionModel().getSelectedItems()));

        //---------------------------------------------------------------------------------------//
        //                          Drag and Drop behaviour for meta-sequence                    //
        //---------------------------------------------------------------------------------------//

        metaListView.setCellFactory((ListView<MetaSequence> param) -> {

            /*For textField ListView check the link : https://stackoverflow.com/questions/35963888/how-to-create-a-listview-of-complex-objects-and-allow-editing-a-field-on-the-obj */
            ListCell<MetaSequence> listCellForMetaSequence = new ListCell<>() {

                private final TextField textField = new TextField();

                {
                    //if we are not connected as admin we can't edit textfield
                    if (!SircusApplication.adminConnected) textField.setDisable(true);
                    else{
                        //defined action when we click on textfield to edit meta-sequence name
                        textField.setOnAction(e -> {
                            //we commit the change only if we are connected as admin
                            commitEdit(getItem());

                        });
                        textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                            //if we click on escape we cancel the editing
                            if (e.getCode() == KeyCode.ESCAPE) {
                                cancelEdit();
                            }
                        });
                    }

                }

                @Override
                protected void updateItem(MetaSequence metaSequence, boolean empty) {

                    super.updateItem(metaSequence, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else if (isEditing() && SircusApplication.adminConnected) {
                        textField.setText(metaSequence.getName());
                        setText(null);
                        setGraphic(textField);
                    } else {
                        setText(metaSequence.getName());
                        setGraphic(null);
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();
                    textField.setText(getItem().getName());
                    setText(null);
                    setGraphic(textField);
                    textField.selectAll();
                    textField.requestFocus();
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getItem().getName());
                    setGraphic(null);
                }

                @Override
                public void commitEdit(MetaSequence metaSequence) {
                    metaSequence.setName(textField.getText());
                    super.commitEdit(metaSequence);
                    setText(textField.getText());
                    setGraphic(null);
                }
            };

            //----------------------------------------------------------------------------------//
            //                     Drag and Drop behaviour for meta-sequence                    //
            //----------------------------------------------------------------------------------//

            //defined OnDragDetected event :
            listCellForMetaSequence.setOnDragDetected((MouseEvent event) ->
            {
                int index_Selected_MetaSeq = listCellForMetaSequence.getIndex();
                if (index_Selected_MetaSeq == -1 || !SircusApplication.adminConnected) {
                    return;
                }
                Dragboard db = listCellForMetaSequence.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.put(SERIALIZED_MIME_TYPE, index_Selected_MetaSeq); //serialize item
                db.setContent(cc);
                event.consume();
            });

            //defined OnDragOver event :
            listCellForMetaSequence.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    //if db has a serialized objet.
                    if (listCellForMetaSequence.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            listCellForMetaSequence.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    //we remove the old meta-Sequence to move to the new place.
                    MetaSequence draggedMetaSeq = metaListView.getItems().get(draggedIndex);
                    SircusApplication.dataSircus.getMetaSequencesList().remove(draggedIndex);

                    metaListView.getItems().remove(draggedIndex);
                    int dropIndex;

                    if (listCellForMetaSequence.isEmpty()) {
                        dropIndex = metaListView.getItems().size();
                    } else {
                        dropIndex = listCellForMetaSequence.getIndex();
                    }
                    // we add the new meta-sequence at the good place.
                    metaListView.getItems().add(dropIndex, draggedMetaSeq);
                    SircusApplication.dataSircus.getMetaSequencesList().add(dropIndex, draggedMetaSeq);
                    metaListView.getSelectionModel().select(dropIndex);
                    event.setDropCompleted(true);
                    event.consume();

                }
            });

            listCellForMetaSequence.setOnDragDone(DragEvent::consume);
            return listCellForMetaSequence;
        });

        //-------------------------------------------------------------------------------------//
        //                  Add and remove behaviours for meta-sequence                        //
        //-------------------------------------------------------------------------------------//

        /* set addMetaSequence proprieties to add meta-sequence */
        addMetaButton.setOnAction(actionEvent -> {
            String newNameMetaSequence = "Nouvelle MetaSequence " + (metaListView.getItems().size() + 1);
            MetaSequence newMetaSequence = new MetaSequence(newNameMetaSequence);
            SircusApplication.dataSircus.getMetaSequencesList().add(newMetaSequence);
            metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));
            //Defined action when the MetaSequence element selected is changed.
            metaListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerMetaSequence());

            if (next.disableProperty().get()) {
                next.setDisable(false);
            }
        });

        /* set removeMetaSequence proprieties to remove meta-sequence */
        removeMetaButton.setOnAction(actionEvent -> {
            //if we have selected a meta-sequence
            if (index_Selected_MetaSequence >= 0) {
                SircusApplication.dataSircus.getMetaSequencesList().remove(index_Selected_MetaSequence);
                metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));

                if (SircusApplication.dataSircus.getMetaSequencesList().size() == 0) {
                    next.setDisable(true);
                }
            }
        });

        //Defined action when we pressed "delete" key. This delete current meta-sequence
        metaListView.setOnKeyReleased((KeyEvent keyEvent) -> {
            //if we pressed delete and we have selected a mete-sequence and only if we are connected as admin
            if ((keyEvent.getCode() == KeyCode.DELETE) && SircusApplication.adminConnected && (index_Selected_MetaSequence >= 0)) {
                SircusApplication.dataSircus.getMetaSequencesList().remove(index_Selected_MetaSequence);
                metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));

                if (SircusApplication.dataSircus.getMetaSequencesList().size() == 0) {
                    next.setDisable(true);
                }
            }
        });

        //----------------------------------------------------------------------------------//
        //                          Drag and Drop behaviour for sequence                    //
        //----------------------------------------------------------------------------------//

        //Set the CellFactory we defined above. Moreover, we defined behaviours to do when we drag and drop a Sequence.
        seqListView.setCellFactory((ListView<ItemSequence> param) -> {
            ListCell<ItemSequence> listCellForSequence = new ListCell<>() {
                private CheckBox checkBox;

                @Override
                public void updateItem(ItemSequence item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!(empty || item == null)) {
                        if (!SircusApplication.adminConnected) {
                            getCheckBox().setVisible(false);
                        } else {
                            getCheckBox().setVisible(true);
                            getCheckBox().setSelected(item.isOn());
                            getCheckBox().setDisable(!mixBoxButtons.isVisible());
                            setGraphic(getCheckBox());
                        }
                        setText(item.getName());
                    } else {
                        setGraphic(null);
                        setText(null);
                    }
                }

                private CheckBox getCheckBox() {
                    if (checkBox == null) {
                        checkBox = new CheckBox();

                        //define action to do when checkbox is selected
                        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
                            if (getItem() != null) {
                                //oldValue = false && newValue = true -> we set Items lock
                                if (!oldValue && newValue) {
                                    //if the item is not lock
                                    if (!getItem().getSequence().getLock()) {
                                        //get the item of the sequence
                                        metaListView.getItems().get(index_Selected_MetaSequence).getSequencesList().get(getIndex()).setLock(true);
                                        getCheckBox().setSelected(true);
                                        getItem().setOn(true);
                                    }
                                }
                                //oldValue = true && newValue = false -> we set Items unlock
                                else if (oldValue && !newValue) {
                                    //if the item is lock
                                    if (getItem().getSequence().getLock()) {
                                        getCheckBox().setSelected(false);
                                        metaListView.getItems().get(index_Selected_MetaSequence).getSequencesList().get(getIndex()).setLock(false);
                                        getItem().setOn(false);
                                    }
                                }
                            }
                        });
                    }
                    return checkBox;
                }
            };

            //defined OnDragDetected event :
            listCellForSequence.setOnDragDetected((MouseEvent event) ->
            {
                int index_Selected_Sequence = listCellForSequence.getIndex();
                if (index_Selected_Sequence == -1 || !SircusApplication.adminConnected) {
                    return;
                }
                Dragboard db = listCellForSequence.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.put(SERIALIZED_MIME_TYPE, index_Selected_Sequence); //serialize item
                db.setContent(cc);
                event.consume();
            });

            //defined OnDragOver event :
            listCellForSequence.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    //if db has a serialized objet.
                    if (listCellForSequence.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            listCellForSequence.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    //we remove the old Sequence to move to the new place.
                    ItemSequence draggedSequence = seqListView.getItems().get(draggedIndex);

                    seqListView.getItems().remove(draggedIndex);
                    int dropIndex;

                    if (listCellForSequence.isEmpty()) {
                        dropIndex = seqListView.getItems().size();
                    } else {
                        dropIndex = listCellForSequence.getIndex();
                    }
                    // we add the new sequence at the good place.
                    seqListView.getItems().add(dropIndex, draggedSequence);

                    //we swap the element in meta-sequence
                    int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                    MetaSequence currentMetaSequence = metaListView.getItems().get(index_Selected_MetaSequence);
                    //add new sequence to the right place
                    currentMetaSequence.getSequencesList().remove(draggedIndex);
                    currentMetaSequence.getSequencesList().add(dropIndex, draggedSequence.getSequence());
                    event.setDropCompleted(true);
                    seqListView.getSelectionModel().select(dropIndex);
                    event.consume();

                }
            });

            listCellForSequence.setOnDragDone(DragEvent::consume);
            return listCellForSequence;
        });

        //Open modifying sequence popup when we double click on sequence
        seqListView.setOnMouseClicked(event ->
        {
            if(event.getClickCount() == 2 && SircusApplication.adminConnected) {
                openModifyPopUpForSequence();
            }
        });

        //------------------------------------------------------------------//
        //            Add and remove behaviours for sequence                //
        //------------------------------------------------------------------//

        /* set doMixButton properties */
        doMixButton.setOnAction(event -> {
            doMixButton.setVisible(false);
            mixBoxButtons.setVisible(true);
            refreshPage();
        });

        /* set cancelMixButton properties */
        cancelMixButton.setOnAction(event -> {
            mixBoxButtons.setVisible(false);
            doMixButton.setVisible(true);
            refreshPage();
        });

        /* set startMixButton proprieties */
        startMixButton.setOnAction(event -> {
            mixBoxButtons.setVisible(false);
            doMixButton.setVisible(true);
            if (index_Selected_MetaSequence >= 0) {
                //get the current Meta-Sequence.
                MetaSequence currentMetaSequence = metaListView.getItems().get(index_Selected_MetaSequence);
                //copy the list
                List<Sequence> copySequence = new ArrayList<>(currentMetaSequence.getSequencesList());
                //create Map to store the fixed sequences.
                HashMap<Sequence, Integer> fixedSequence = new HashMap<>();
                //find fixed sequences
                for (int indiceSequence = 0; indiceSequence < copySequence.size(); indiceSequence++) {
                    Sequence currentSequence = copySequence.get(indiceSequence);
                    if (currentSequence.getLock()) {
                        fixedSequence.put(currentSequence, indiceSequence);
                        currentMetaSequence.getSequencesList().remove(currentSequence);
                    }
                }
                //Shuffle the list of sequences
                Collections.shuffle(currentMetaSequence.getSequencesList());

                //We sort the hashmap to insert elements in ascending order (on the index of insertion)
                fixedSequence = sortByValue(fixedSequence);
                //restore fixed sequences
                fixedSequence.forEach((sequence, integer) -> currentMetaSequence.getSequencesList().add(integer, sequence));
            }
            refreshPage();
        });

        /*defined action to do when we update a sequence */
        modifySeqButton.setOnAction(actionEvent -> openModifyPopUpForSequence());

        /*defined action to do when we add a sequence */
        addSeqButton.setOnAction(actionEvent -> {
            String newNameSequence = "Nouvelle Sequence " + (seqListView.getItems().size() + 1);
            Sequence newSequence = new Sequence(newNameSequence);
            if (index_Selected_MetaSequence >= 0)
                SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence).getSequencesList().add(newSequence);
            seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
            //Defined action when the Sequence element selected is changed.
            seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerSequence());
        });

        /*defined action to do when we remove a sequence */
        removeSeqButton.setOnAction(actionEvent -> {
            //if we have selected a meta-sequence
            if (index_Selected_Sequence >= 0 && index_Selected_MetaSequence >= 0) {
                SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence).getSequencesList().remove(index_Selected_Sequence);
                seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
            }
        });

        //Defined action when we pressed "delete" key. This delete current sequence
        seqListView.setOnKeyReleased((KeyEvent keyEvent) -> {
            //if we pressed delete and we have selected a mete-sequence and only if we are connected as admin
            if ((keyEvent.getCode() == KeyCode.DELETE) && SircusApplication.adminConnected && (index_Selected_Sequence >= 0) && (index_Selected_MetaSequence >= 0)) {
                SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence).getSequencesList().remove(index_Selected_Sequence);
                seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
            }
        });

    }


    /**
     * This method allows to open pop for modifying a sequence
     */
    private void openModifyPopUpForSequence(){
        try {
            if(seqListView.getSelectionModel().getSelectedItem() != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_seq_popup.fxml"));
                DialogPane dialogPane = fxmlLoader.load();
                ModifySeqPopUp controller = fxmlLoader.getController();
                Sequence copy = new Sequence(seqListView.getSelectionModel().getSelectedItem().getSequence());
                controller.setSequence(copy);
                controller.init();

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Modification de séquence");
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.initOwner(modifySeqButton.getScene().getWindow());

                Optional<ButtonType> clickedButton = dialog.showAndWait();

                if (clickedButton.get() == ButtonType.FINISH) {
                    Sequence oldSequence = SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence).getSequencesList().get(index_Selected_Sequence);
                    SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence).getSequencesList().add(index_Selected_Sequence, controller.getSequence());
                    SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence).getSequencesList().remove(oldSequence);
                }
                // the code below allows you to update listview.
                seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
                //Defined action when the MetaSequence element selected is changed.
                seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerSequence());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Export a sequence to XML format, for admin only
     */
    @FXML
    private void exportSeq() {
        // export is only available for admin
        if (SircusApplication.adminConnected) {
            // get selected sequence
            Sequence seq = seqListView.getSelectionModel().getSelectedItem().getSequence();
            File file = new File(SircusApplication.dataSircus.getPath().getSeqPath() + seq.getName() + ".xml");
            try {
                // the sequence is transform to xml in the class by toXML()
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
    private void exportMeta() {
        // export is only available for admin
        if (SircusApplication.adminConnected) {
            // get selected metaSequence
            int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
            MetaSequence meta = metaListView.getItems().get(index_Selected_MetaSequence);
            File file = new File(SircusApplication.dataSircus.getPath().getMetaPath() + meta.getName() + ".xml");
            try {
                // the metaSequence is transform to xml in the class by toXML()
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
    private void importSeq() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer sequence");
        fileChooser.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getSeqPath()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showOpenDialog(importSeqButton.getScene().getWindow());
        if (file != null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                // the custom parse is handled by ImportSeqXML class
                SAXParser saxParser = factory.newSAXParser();
                ImportSeqXML handler = new ImportSeqXML();
                saxParser.parse(file, handler);
                // add the new sequence in the list
                int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                metaListView.getItems().get(index_Selected_MetaSequence).addSequence(handler.getSeq());

                // refresh list of sequence
                refreshPage();

                // refresh preview timeline
                MetaSequence currentMetaSequence = metaListView.getItems().get(index_Selected_MetaSequence);
                previewTimeline.removeAllMedia();
                previewTimeline.addMetaSequence(currentMetaSequence);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Import a metaSequence from a XML file
     */
    @FXML
    private void importMeta() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer metaSequence");
        fileChooser.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getMetaPath()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showOpenDialog(importMetaButton.getScene().getWindow());
        if (file != null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                // the custom parse is handled by ImportMetaSeqXML class
                SAXParser saxParser = factory.newSAXParser();
                ImportMetaSeqXML handler = new ImportMetaSeqXML();
                saxParser.parse(file, handler);
                // add the new metaSequence in the list
                SircusApplication.dataSircus.getMetaSequencesList().add(handler.getMeta());
                metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * function that refreshes the page : seqListView objects to fix guidelines of the mix process
     */
    public void refreshPage() {
        //Defined the list of sequences. This fill with new sequences.
        seqListView.getItems().clear();
        seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
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

    /**
     * Get all itemsSequence storage in the current Meta Sequence which is selected
     *
     * @return the itemsSequence of items
     */
    private List<ItemSequence> getAllItemInCurrentMetaSequence() {
        List<ItemSequence> allItemSequence = new ArrayList<>();
        if (index_Selected_MetaSequence >= 0) {
            for (Sequence sequence : metaListView.getItems().get(index_Selected_MetaSequence).getSequencesList()) {
                ItemSequence newItemSequence = new ItemSequence(sequence.getName(), sequence.getLock(), sequence);
                allItemSequence.add(newItemSequence);
            }
        }
        return allItemSequence;
    }

    /**
     * Get all item storage in the current list of Meta-sequence
     *
     * @return the list of itemsMetaSequence
     */
    private List<MetaSequence> getAllItemMetaSequence() {

        return new ArrayList<>(SircusApplication.dataSircus.getMetaSequencesList());
    }

    /**
     * Method which allow to sort a HashMap by values
     *
     * @param hm The HashMap that we want to sort by value
     * @return HashMap sorted by value
     */
    public static HashMap<Sequence, Integer> sortByValue(HashMap<Sequence, Integer> hm) {
        // Create a list from elements of HashMap
        LinkedList<Entry<Sequence, Integer>> list = new LinkedList<>(hm.entrySet());

        // Sort the list using lambda expression
        list.sort(Map.Entry.comparingByValue());

        // put data from sorted list to hashmap
        HashMap<Sequence, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Sequence, Integer> aa : list) temp.put(aa.getKey(), aa.getValue());
        return temp;
    }

    /**
     * Set the information section about the selected meta-sequence
     */
    public void setMetaSeqStats() {
        MetaSequence metaSeq = SircusApplication.dataSircus.getMetaSequencesList().get(index_Selected_MetaSequence);
        statsTitle.setText("Statistiques de la méta-séquence");
        statsForSeq.setVisible(false);
        statsForMeta.setVisible(true);

        nbSeqLabelForMeta.setText(Integer.toString(metaSeq.getSequencesList().size()));

        int nbMedias = 0;
        int nbInterstims = 0;
        for (Sequence seq : metaSeq.getSequencesList()) {
            nbMedias += seq.getListMedias().size();
            for(int i=0; i<seq.getListMedias().size(); i++){
                if(seq.getListMedias().get(i).getInterstim() != null){
                    nbInterstims++;
                }
            }
        }
        nbMediaLabelForMeta.setText(Integer.toString(nbMedias));
        nbInterstimLabelForMeta.setText(Integer.toString(nbInterstims));

        long seconds = metaSeq.getDuration().getSeconds();
        totalDurationLabelForMeta.setText(
                String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60)));
    }

    /**
     * Set the information section about the selected sequence
     */
    public void setSeqStats() {
        Sequence sequence = getAllItemMetaSequence().get(index_Selected_MetaSequence).getSequencesList().get(index_Selected_Sequence);
        statsTitle.setText("Statistiques de la séquence");
        statsForMeta.setVisible(false);
        statsForSeq.setVisible(true);

        int nbMedias = sequence.getListMedias().size();
        int nbInterstims = 0;
        for(int i=0; i<sequence.getListMedias().size(); i++){
            if(sequence.getListMedias().get(i).getInterstim() != null){
                nbInterstims++;
            }
        }
        nbMediaLabelForSeq.setText(Integer.toString(nbMedias));
        nbInterstimLabelForSeq.setText(Integer.toString(nbInterstims));

        long seconds = sequence.getDuration().getSeconds();
        totalDurationLabelForSeq.setText(
                String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60)));
    }

    /**
     * Class which implement actions to do when MetaSequence listView change item.
     */
    private class ChangeListenerMetaSequence implements ChangeListener<MetaSequence> {

        @Override
        public void changed(ObservableValue<? extends MetaSequence> observableValue, MetaSequence metaSequence, MetaSequence metaSequenceSelected) {
            //get the new medias lists to display it on screen
            index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();

            previewTimeline.removeAllMedia();
            //if the selected meta-sequence is not null we display the previewTimeLine
            if (metaSequenceSelected != null) previewTimeline.addMetaSequence(metaSequenceSelected);

            //Defined the list of sequences.
            seqListView.getItems().clear();
            seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
            if (index_Selected_MetaSequence >= 0) {
                setMetaSeqStats();
                previewText.setText("Prévisualisation de la métaséquence");
            }
        }
    }

    /**
     * Class which implement actions to do when Sequence listView change item.
     */
    private class ChangeListenerSequence implements ChangeListener<ItemSequence> {

        @Override
        public void changed(ObservableValue<? extends ItemSequence> observableValue, ItemSequence s, ItemSequence t1) {
            //get the new medias lists to display it on screen
            index_Selected_Sequence = seqListView.getSelectionModel().getSelectedIndex();
            if ((0 <= index_Selected_Sequence) && (0 <= index_Selected_MetaSequence)) {
                MetaSequence currentMetaSeq = metaListView.getItems().get(index_Selected_MetaSequence);
                Sequence currentSequence = currentMetaSeq.getSequencesList().get(index_Selected_Sequence);
                previewTimeline.setMediaList(currentSequence.getListMedias());

                setSeqStats();

                previewText.setText("Prévisualisation de la séquence");
            }
        }
    }
}