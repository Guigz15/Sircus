package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.ModifySeqPopUp;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.utils.Item;
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
    private ListView<String> metaListView;
    @FXML
    public ListView<Item> seqListView;
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

    @Getter @Setter
    private boolean aboutToMix;

    //DataFormat use for drag and drop sequences in listView.
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    //list of meta-sequences storage
    private List<MetaSequence> listMetaSequence;

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
        }else{
            doMixButton.setVisible(true);
            doMixButton.setDisable(false);
        }

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
                index_Selected_MetaSequence = metaListView.getSelectionModel().getSelectedIndex();
                MetaSequence currentMetaSequence = listMetaSequence.get(index_Selected_MetaSequence);
                previewTimeline.removeAllMedia();
                previewTimeline.addMetaSequence(currentMetaSequence);

                //Defined the list of sequences.
                seqListView.getItems().clear();
                seqListView.setItems(FXCollections.observableList(getAllItemInCurrentMetaSequence()));
            }
        });

        /* initialize sequences listView */
        seqListView.setStyle("-fx-font-size: 14pt;");

        //Defined action when the Sequence element selected is changed.
        seqListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observableValue, Item s, Item t1) {
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


        //Set the CellFactory we definied defined above. Moreover, we defined behaviours to do when we drag and drop a Sequence.
        seqListView.setCellFactory((ListView<Item> param) -> {
            ListCell<Item> listCellForSequence = new ListCell<Item>() {
                private CheckBox checkBox;
                @Override
                public void updateItem(Item item, boolean empty) {
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
                            Item draggedSequence = seqListView.getItems().get(draggedIndex);

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
                            MetaSequence currentMetaSequence = listMetaSequence.get(index_Selected_MetaSequence);
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
                    MetaSequence currentMetaSequence = listMetaSequence.get(index_Selected_MetaSequence);
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
     * Get all item storage in the current Meta Sequence which is selected
     * @return the list of items
     */
    private List<Item> getAllItemInCurrentMetaSequence(){
        List<Item> allItem = new ArrayList<>();
        if(index_Selected_MetaSequence >= 0) {
            for (Sequence sequence : listMetaSequence.get(index_Selected_MetaSequence).getSequencesList()) {
                Item newItem = new Item(sequence.getName(), sequence.getLock(), sequence);
                allItem.add(newItem);
            }
        }
        return allItem;
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
}