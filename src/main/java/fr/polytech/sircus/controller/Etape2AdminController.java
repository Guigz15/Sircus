package fr.polytech.sircus.controller;

import com.itextpdf.text.Meta;
import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.ModifySeqPopUp;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.utils.ItemSequence;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Class Etape2AdminController : manages the metasequences settings page of the Admin
 */
public class Etape2AdminController implements Initializable {
    @FXML
    private ListView<MetaSequence> metaListView;
    @FXML
    public ListView<ItemSequence> seqListView;
    //TODO Demandé s'ils veulent garder le boutton modifier ou juste un click sur une meta-sequence pour la renommer suffit.
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
    private Button addMetaButton;
    @FXML
    private Button addSeqButton;
    @FXML
    private Button previous;
    @FXML
    private Button next;
    @FXML
    private PreviewTimeline previewTimeline;

    @Getter @Setter
    private boolean aboutToMix;

    //DataFormat use for drag and drop sequences in listView.
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    //index of last meta-sequence selected. Is set with -1 if there are no selected meta-sequence
    int index_Selected_MetaSequence = -1;

    /**
     * Function initialize which dynamically ends the listViews initialization
     * and enable corresponding buttons when metasequences or sequences are selected
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* initialize button to mix randomly the list of sequences depends if we are connected on admin mode */
        if(!SircusApplication.adminConnected){
            doMixButton.setVisible(false);
            doMixButton.setDisable(true);
            //hide modification buttons
            renameMetaButton.setVisible(false);
            exportMetaButton.setVisible(false);
            removeMetaButton.setVisible(false);
            addMetaButton.setVisible(false);
            exportSeqButton.setVisible(false);
            modifySeqButton.setVisible(false);
            removeSeqButton.setVisible(false);
            addSeqButton.setVisible(false);
        }else{
            doMixButton.setVisible(true);
            doMixButton.setDisable(false);
            addMetaButton.setDisable(false);
        }

        /* initialize metasequences listView */
        metaListView.setStyle("-fx-font-size: 14pt;");
        metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));

        //Defined action when the MetaSequence element selected is changed.
        metaListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerMetaSequence());
        //Defined action when we pressed "delete" key. This delete current meta-sequence
        metaListView.setOnKeyReleased((KeyEvent keyEvent) -> {
            //if we pressed delete and we have selected a mete-sequence and only if we are connected as admin
            if (keyEvent.getCode() == KeyCode.DELETE && SircusApplication.adminConnected && index_Selected_MetaSequence >=0) {
                SircusApplication.dataSircus.getMetaSequencesList().remove(index_Selected_MetaSequence);
                metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));
            }
        });
        /* initialize sequences listView */
        seqListView.setStyle("-fx-font-size: 14pt;");

        //Defined action when the Sequence element selected is changed.
        seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemSequence>() {
            @Override
            public void changed(ObservableValue<? extends ItemSequence> observableValue, ItemSequence s, ItemSequence t1) {

                //allow the buttons to change Sequence only if connect as admin
                if(SircusApplication.adminConnected) {
                    modifySeqButton.setDisable(false);
                    exportSeqButton.setDisable(false);
                    removeSeqButton.setDisable(false);
                    addSeqButton.setDisable(false);
                    modifySeqButton.setVisible(true);
                    exportSeqButton.setVisible(true);
                    removeSeqButton.setVisible(true);
                    addSeqButton.setVisible(true);
                }else{
                    modifySeqButton.setDisable(true);
                    exportSeqButton.setDisable(true);
                    removeSeqButton.setDisable(true);
                    addSeqButton.setDisable(true);
                }
                //get the new medias lists to display it on screen
                int index_Selected_Sequence = seqListView.getSelectionModel().getSelectedIndex();
                int index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                MetaSequence currentMetaSeq = metaListView.getItems().get(index_Selected_MetaSequence);
                if(index_Selected_MetaSequence >= 0 && index_Selected_Sequence >=0) {
                    Sequence currentSequence = currentMetaSeq.getSequencesList().get(index_Selected_Sequence);
                    previewTimeline.setMediaList(currentSequence.getListMedias());
                }
            }
        });

        metaListView.setEditable(true);

        //----------------------------------------------------------------------//
        //                          Drag and Drop behaviour                     //
        //----------------------------------------------------------------------//

        /** Drag and Drop for meta-sequence ListView **/

        metaListView.setCellFactory((ListView<MetaSequence> param) -> {

            /*For textField ListView check the link : https://stackoverflow.com/questions/35963888/how-to-create-a-listview-of-complex-objects-and-allow-editing-a-field-on-the-obj */
            ListCell<MetaSequence> listCellForMetaSequence = new ListCell<>(){

                private TextField textField = new TextField() ;

                {
                    //defined action when we click on textfield to edit meta-sequence name
                    textField.setOnAction(e -> {
                        //we commit the change only if we are connected as admin
                        if(SircusApplication.adminConnected) commitEdit(getItem());
                        else cancelEdit();
                    });
                    textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                        //if we click on escape we cancel the editing
                        if (e.getCode() == KeyCode.ESCAPE) {
                            cancelEdit();
                        }
                    });

                }

                @Override
                protected void updateItem(MetaSequence metaSequence, boolean empty) {
                    super.updateItem(metaSequence, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else if (isEditing() && SircusApplication.adminConnected) {
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


            //defined OnDragDetected event :
            listCellForMetaSequence.setOnDragDetected( ( MouseEvent event ) ->
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
                    if (listCellForMetaSequence.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
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
                    metaListView.getSelectionModel().select(dropIndex);
                    SircusApplication.dataSircus.getMetaSequencesList().add(dropIndex,draggedMetaSeq);
                    event.setDropCompleted(true);
                    event.consume();

                }
            });

            listCellForMetaSequence.setOnDragDone(DragEvent::consume);
            return listCellForMetaSequence;
        });


        /** Drag and Drop for sequence ListView **/

        //Set the CellFactory we definied defined above. Moreover, we defined behaviours to do when we drag and drop a Sequence.
        seqListView.setCellFactory((ListView<ItemSequence> param) -> {
            ListCell<ItemSequence> listCellForSequence = new ListCell<ItemSequence>() {
                private CheckBox checkBox;
                @Override
                public void updateItem(ItemSequence item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!(empty || item == null)) {
                        if(!SircusApplication.adminConnected){
                            getCheckBox().setVisible(false);
                        }else {
                            getCheckBox().setVisible(true);
                            getCheckBox().setSelected(item.isOn());
                            if (isAboutToMix()) {
                                getCheckBox().setDisable(false);
                            } else {
                                getCheckBox().setDisable(true);
                            }
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
                                if(oldValue == false && newValue ==true) {
                                    //if the item is not lock
                                    if (!getItem().getSequence().getLock()) {
                                        getCheckBox().setSelected(newValue);
                                        getItem().getSequence().setLock(newValue);
                                        getItem().setOn(newValue);
                                    }
                                }
                                //oldValue = true && newValue = false -> we set Items unlock
                                else if(oldValue == true && newValue == false){
                                    //if the item is lock
                                    if (getItem().getSequence().getLock()) {
                                        getCheckBox().setSelected(newValue);
                                        getItem().getSequence().setLock(newValue);
                                        getItem().setOn(newValue);
                                    }
                                }
                            }
                        });
                    }
                    return checkBox;
                }
            };



            //defined OnDragDetected event :
            listCellForSequence.setOnDragDetected( ( MouseEvent event ) ->
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
                    if (listCellForSequence.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            listCellForSequence.setOnDragDropped(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                            int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                            //we remove the old Seque to move to the new place.
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
                            currentMetaSequence.getSequencesList().add(dropIndex,draggedSequence.getSequence());
                            event.setDropCompleted(true);
                            seqListView.getSelectionModel().select(dropIndex);
                            event.consume();

                        }
            });

            listCellForSequence.setOnDragDone(DragEvent::consume);
        return listCellForSequence;
        });


        /* set addMetaSequence proprieties to add meta-sequence */
        addMetaButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String newNameMetaSequence = "Nouvelle MetaSequence " + (metaListView.getItems().size() + 1);
                MetaSequence newMetaSequence = new MetaSequence(newNameMetaSequence);
                SircusApplication.dataSircus.getMetaSequencesList().add(newMetaSequence);
                metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));
                //Defined action when the MetaSequence element selected is changed.
                metaListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerMetaSequence());
            }
        });

        /* set removeMetaSequence proprieties to remove meta-sequence */
        removeMetaButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //if we have selected a meta-sequence
                if(index_Selected_MetaSequence >=0){
                    SircusApplication.dataSircus.getMetaSequencesList().remove(index_Selected_MetaSequence);
                    metaListView.setItems(FXCollections.observableList(getAllItemMetaSequence()));
                }
            }
        });

        /* set doMixButton proprieties */
        doMixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setAboutToMix(true);
                refreshPage();
            }
        });

        /* set cancelMixButton proprieties */
        cancelMixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setAboutToMix(false);
                refreshPage();
            }
        });

        /* set startMixButton proprieties */
        startMixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setAboutToMix(false);
                if(index_Selected_MetaSequence >= 0) {
                    //get the current Meta-Sequence.
                    MetaSequence currentMetaSequence = metaListView.getItems().get(index_Selected_MetaSequence);
                    //copy the list
                    List<Sequence> copySequence = new ArrayList<>(currentMetaSequence.getSequencesList());
                    //create Map to store the fixed sequences.
                    HashMap<Sequence,Integer> fixedSequence = new HashMap<>();
                    //find fixed sequences
                    for (int indiceSequence = 0; indiceSequence < copySequence.size(); indiceSequence++) {
                        Sequence currentSequence = copySequence.get(indiceSequence);
                        if (currentSequence.getLock()) {
                            fixedSequence.put(currentSequence,indiceSequence);
                            currentMetaSequence.getSequencesList().remove(currentSequence);
                        }
                    }
                    //Shuffle the list of sequences
                    Collections.shuffle(currentMetaSequence.getSequencesList());

                    //We sort the hashmap to insert elements in ascending order (on the index of insertion)
                    fixedSequence = sortByValue(fixedSequence);
                    //restore fixed sequences
                    fixedSequence.forEach((sequence, integer) -> {
                        currentMetaSequence.getSequencesList().add(integer,sequence);
                    });
                }
                refreshPage();
            }
        });


        modifySeqButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_seq_popup.fxml"));
                    DialogPane dialogPane = fxmlLoader.load();
                    ModifySeqPopUp controller = fxmlLoader.getController();
                    controller.setSequence(seqListView.getSelectionModel().getSelectedItem().getSequence());
                    controller.init();

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

    /**
     * function that refreshes the page : refreshes mix buttons and the seqListView objects
     * to fix guidelines of the mix process
     *
     * @return
     */
    public void refreshPage(){
        if (isAboutToMix()){
            cancelMixButton.setVisible(true);
            cancelMixButton.setDisable(false);
            startMixButton.setVisible(true);
            startMixButton.setDisable(false);
            doMixButton.setVisible(false);
            doMixButton.setDisable(true);
        }
        else {
            cancelMixButton.setVisible(false);
            cancelMixButton.setDisable(true);
            startMixButton.setVisible(false);
            startMixButton.setDisable(true);
            doMixButton.setVisible(true);
            doMixButton.setDisable(false);
        }
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
     * @return the itemsSequence of items
     */
    private List<ItemSequence> getAllItemInCurrentMetaSequence(){
        List<ItemSequence> allItemSequence = new ArrayList<>();
        if(index_Selected_MetaSequence >= 0) {
            for (Sequence sequence : metaListView.getItems().get(index_Selected_MetaSequence).getSequencesList()) {
                ItemSequence newItemSequence = new ItemSequence(sequence.getName(), sequence.getLock(), sequence);
                allItemSequence.add(newItemSequence);
            }
        }
        return allItemSequence;
    }

    /**
     * Get all item storage in the current list of Meta-sequence
     * @return the list of itemsMetaSequence
     */
    private List<MetaSequence> getAllItemMetaSequence(){
        List<MetaSequence> allItemMetaSequence = new ArrayList<>();

        for (MetaSequence metaSequence : SircusApplication.dataSircus.getMetaSequencesList()) {
            allItemMetaSequence.add(metaSequence);
        }

        return allItemMetaSequence;
    }

    /**
     * Method which allow to sort a HashMap by values
     * @param hm
     * @return
     */
    public static HashMap<Sequence, Integer>sortByValue(HashMap<Sequence, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Sequence, Integer> > list
                = new LinkedList<Map.Entry<Sequence, Integer> >(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                (i1,
                 i2) -> i1.getValue().compareTo(i2.getValue()));

        // put data from sorted list to hashmap
        HashMap<Sequence, Integer> temp
                = new LinkedHashMap<Sequence, Integer>();
        for (Map.Entry<Sequence, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private class ChangeListenerMetaSequence implements ChangeListener<MetaSequence> {

        @Override
        public void changed(ObservableValue<? extends MetaSequence> observableValue, MetaSequence metaSequence, MetaSequence metaSequenceSelected) {
            //allow the buttons to change meta-sequence only if connect as admin
            if(SircusApplication.adminConnected) {
                renameMetaButton.setDisable(false);
                exportMetaButton.setDisable(false);
                removeMetaButton.setDisable(false);
                addMetaButton.setDisable(false);
                addMetaButton.setVisible(true);
                renameMetaButton.setVisible(true);
                exportMetaButton.setVisible(true);
                removeMetaButton.setVisible(true);
            }else{
                addMetaButton.setDisable(true);
                renameMetaButton.setDisable(true);
                exportMetaButton.setDisable(true);
                removeMetaButton.setDisable(true);
            }

            //get the new medias lists to display it on screen
            index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();

            previewTimeline.removeAllMedia();
            //if the selected meta-sequence is not null we display the previewTimeLine
            if(metaSequenceSelected != null)previewTimeline.addMetaSequence(metaSequenceSelected);

            //Defined the list of sequences.
            seqListView.getItems().clear();
            seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
        }
    }
}