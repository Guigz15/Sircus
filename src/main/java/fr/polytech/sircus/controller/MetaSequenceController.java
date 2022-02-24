package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.addSeqPopUp;
import fr.polytech.sircus.controller.PopUps.modifyMetaSeqPopUp;
import fr.polytech.sircus.controller.PopUps.modifySeqPopUp;
import fr.polytech.sircus.model.Internals.ObservableMetaSequenceSet;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;

/**
 * Meta-sequence window controller
 */
public class MetaSequenceController {

    private final FontIcon addIcon = new FontIcon("fa-plus");
    private final FontIcon checkIcon = new FontIcon("fa-check");
    private final FontIcon pauseIcon = new FontIcon("fa-pause");
    private final FontIcon playIcon = new FontIcon("fa-play");

    // Meta-sequences manager
    private final ObservableMetaSequenceSet metaSequences = new ObservableMetaSequenceSet();

    // File browser managers
    private final FileChooser fileChooserMedia = new FileChooser();
    private final FileChooser fileChooserInterstim = new FileChooser();

    // UI components
    @FXML
    private ComboBox<MetaSequence> metaSeqComboBox;
    @FXML
    private ProgressBar metaSeqProgressBar;
    @FXML
    private TextField metaSeqAddName;

    @FXML
    private Button metaSeqAdd;
    @FXML
    private Button metaSeqAddQuit;
    @FXML
    private Button metaSeqOption;
    @FXML
    private Button metaSeqPlay;

    @FXML
    private TableView<Sequence> metaSeqTable;
    @FXML
    private TableColumn<Sequence, String> metaSeqTableColumnName;
    @FXML
    private TableColumn<Sequence, String> metaSeqTableColumnDuration;
    @FXML
    private TableColumn<Sequence, String> metaSeqTableColumnOption;
    @FXML
    private TableColumn<Sequence, String> metaSeqTableColumnLock;

    // Intern state manager
    private int addState;

    // Viewer manager
    private ViewerController viewer;
    private Boolean viewerPlayingState;

    // Methods

    /**
     * Main constructor
     */
    public MetaSequenceController() {
        this.addState = 0;
        this.viewer = null;
        this.viewerPlayingState = true;
    }

    /**
     * Initialize the meta-sequence window
     */
    @FXML
    private void initialize() {
        // Meta-sequence selector
        metaSeqComboBox.setItems(metaSequences);

        // Read serialized data
        metaSequences.addAll(SircusApplication.dataSircus.getMetaSequencesList());

        metaSeqComboBox.getSelectionModel().select(0);

        // Table
        metaSeqTableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        metaSeqTableColumnDuration.setCellValueFactory(cellData -> {
            String formattedDuration = cellData.getValue().getDuration().toString()
                    .replace("PT", "")
                    .replace("M", "m")
                    .replace("S", "s");
            return new SimpleStringProperty(formattedDuration);
        });
        metaSeqTableColumnOption.setCellValueFactory(new PropertyValueFactory<>("name"));
        metaSeqTableColumnLock.setCellValueFactory(new PropertyValueFactory<>("name"));

        Callback<TableColumn<Sequence, String>, TableCell<Sequence, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Sequence, String> call(final TableColumn<Sequence, String> param) {
                return new TableCell<>() {
                    final Button tableViewOptionButton = new Button("");
                    final Button tableViewDeleteButton = new Button("");
                    final HBox hBox = new HBox(tableViewOptionButton, tableViewDeleteButton);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            final FontIcon cogIcon = new FontIcon("fa-cog");
                            final FontIcon delIcon = new FontIcon("fa-trash");

                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);

                            tableViewOptionButton.setGraphic(cogIcon);
                            tableViewDeleteButton.setGraphic(delIcon);

                            tableViewOptionButton.setOnMouseClicked(event ->
                            {
                                Sequence sequence = getTableView().getItems().get(getIndex());
                                modifySeqInMetaSeq(sequence);
                            });

                            tableViewDeleteButton.setOnAction(event ->
                            {
                                MetaSequence metaSequence = metaSeqComboBox.getValue();
                                metaSequence.getListSequences().remove(getTableView().getItems().get(getIndex()));
                                metaSeqTable.setItems(FXCollections.observableList(metaSequence.getListSequences()));
                            });

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        Callback<TableColumn<Sequence, String>, TableCell<Sequence, String>> cellFactoryLock = new Callback<>() {
            @Override
            public TableCell<Sequence, String> call(final TableColumn<Sequence, String> param) {
                return new TableCell<>() {
                    final CheckBox tableViewCheckBoxLock = new CheckBox("");
                    final HBox hBox = new HBox(tableViewCheckBoxLock);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);

                            Sequence sequence = getTableView().getItems().get(getIndex());

                            tableViewCheckBoxLock.setSelected(sequence.getLock());

                            tableViewCheckBoxLock.setOnAction(event ->
                                    sequence.setLock(tableViewCheckBoxLock.isSelected()));

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };
        metaSeqTableColumnOption.setCellFactory(cellFactory);
        metaSeqTableColumnLock.setCellFactory(cellFactoryLock);
        metaSeqTable.getColumns().clear();
        metaSeqTable.getColumns().addAll(
                Arrays.asList(metaSeqTableColumnLock,
                        metaSeqTableColumnName,
                        metaSeqTableColumnDuration,
                        metaSeqTableColumnOption));
        metaSeqTable.setItems(FXCollections.observableList(metaSequences.get(0).getListSequences()));

        // Add meta-sequences
        metaSeqAdd.setGraphic(addIcon);
        metaSeqAddName.setVisible(false);
        metaSeqAddName.setManaged(false);

        metaSeqAddQuit.setVisible(false);
        metaSeqAddQuit.setManaged(false);

        // Progress bar
        long metaSequenceDuration = computeTotalTime();

        metaSeqProgressBar.setProgress(0);
        // Called every second
        Timeline updateProgressBar = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        event -> {
                            if (viewer != null && viewer.getTimeline() != null) {
                                metaSeqProgressBar.setProgress((float) (Math.floor((viewer.getTimeline().getCurrentTime().toSeconds() / metaSequenceDuration) * 100) / 100));
                            }
                        }));
        updateProgressBar.setCycleCount(Timeline.INDEFINITE);
        updateProgressBar.play();
    }

    /**
     * Change the selected meta-sequence from the comboBox
     */
    @FXML
    private void switchMetaSeq() {
        if (metaSeqComboBox.getValue() != null) {
            metaSeqTable.setItems(FXCollections.observableList(metaSeqComboBox.getValue().getListSequences()));
        }
    }

    /**
     * Add a new meta-sequence to the list
     */
    @FXML
    private void addMetaSeq() {
        if (this.addState != 0) {
            MetaSequence newMetaSequence = new MetaSequence(metaSeqAddName.getText());
            metaSequences.add(newMetaSequence);
            metaSeqComboBox.getSelectionModel().select(metaSequences.size() - 1);
            SircusApplication.dataSircus.saveMetaSeq(newMetaSequence);
        }
        toggleMetaSeqOptions();
    }

    /**
     * Close the add meta-sequence window
     */
    @FXML
    private void quitAddMetaSeq() {
        toggleMetaSeqOptions();
    }

    /**
     * Open the modification popup of a meta-sequence
     */
    @FXML
    private void modifyMetaSeq() {
        ModificationMetaSeqListener modificationMetaSeqListener = newMetaSequence ->
        {
            this.metaSeqComboBox.getSelectionModel().clearSelection();
            this.metaSeqComboBox.getSelectionModel().select(newMetaSequence);
        };

        new modifyMetaSeqPopUp(this.metaSeqComboBox.getScene().getWindow(),
                this.metaSequences,
                this.metaSeqComboBox.getSelectionModel().getSelectedItem(),
                modificationMetaSeqListener);
    }

    /**
     * Open the adding popup of a sequence to a meta-sequence
     */
    @FXML
    private void addSeqToMetaSeq() {
        ModificationMetaSeqListener addListener = newMetaSequence ->
                this.metaSeqTable.setItems(FXCollections.observableList(newMetaSequence.getListSequences()));

        new addSeqPopUp(this.metaSeqComboBox.getScene().getWindow(),
                FXCollections.observableList(
                        this.metaSeqComboBox.getSelectionModel().getSelectedItem().getListSequences()),
                this.metaSeqComboBox.getSelectionModel().getSelectedItem(),
                addListener);
    }

    /**
     * Create a popup to edit a sequence
     *
     * @param sequence The sequence to be edited
     */
    @FXML
    private void modifySeqInMetaSeq(Sequence sequence) {
        ModificationSequenceListener listener = seq ->
                this.metaSeqTable.refresh();

        new modifySeqPopUp(
                this.metaSeqComboBox.getScene().getWindow(),
                FXCollections.observableList(sequence.getListMedias()),
                sequence,
                listener,
                this.fileChooserMedia,
                this.fileChooserInterstim);
    }

    /**
     * Check if the name of the new meta-sequence is not empty
     */
    @FXML
    private void checkMetaSeqName() {
        metaSeqAdd.setDisable(metaSequences.findName(metaSeqAddName.getText()));
    }

    /**
     * Create a randomized list of Media from a meta-sequence
     *
     * @param metaSeqName The name of the meta-sequence to use
     * @return a randomized list of Media
     */
    @FXML
    private ArrayList<Media> randomiseMetaSequence(String metaSeqName) {
        MetaSequence Sequence = metaSequences.get(0);
        for (MetaSequence metaSequence : metaSequences) {
            if (metaSequence.getName().equals(metaSeqName)) {
                Sequence = metaSequence;
            }
        }

        ArrayList<Media> randomizedMediaList = new ArrayList<>();
        ArrayList<fr.polytech.sircus.model.Sequence> seqList = new ArrayList<>(Sequence.getListSequences());
        int randInt;

        for (int z = 0; z < seqList.size(); z += 1) {
            if (!seqList.get(z).getLock()) {
                randInt = (int) (Math.random() * (((seqList.size() - 1)) + 1));

                while (seqList.get(randInt).getLock()) {
                    randInt += 1;
                    if (randInt > (seqList.size() - 1)) {
                        randInt = 0;
                    }
                }

                seqList.set(z, seqList.get(randInt));
                seqList.set(randInt, seqList.get(z));
            }
        }

        for (fr.polytech.sircus.model.Sequence sequence : seqList) {
            ArrayList<Media> cloneList = new ArrayList<>(sequence.getListMedias());

            for (int i = 0; i < cloneList.size(); i += 1) {
                if (!sequence.getListMedias().get(i).getLock()) {
                    randInt = (int) (Math.random() * (((cloneList.size() - 1)) + 1));

                    while (sequence.getListMedias().get(randInt).getLock()) {
                        randInt += 1;
                        if (randInt > cloneList.size() - 1) {
                            randInt = 0;
                        }
                    }

                    cloneList.set(i, cloneList.get(randInt));
                    cloneList.set(randInt, cloneList.get(i));
                }
            }
            randomizedMediaList.addAll(cloneList);
        }
        return randomizedMediaList;
    }

    /**
     * Play or pause the viewer
     */
    @FXML
    private void togglePlay() {
        if (viewer != null) {
            // If Play button is displayed, click it calls appropriate function of ViewerController
            // Also change displayed icon and state variable
            if (viewerPlayingState) {
                viewer.playViewer();
                metaSeqPlay.setGraphic(pauseIcon);
                viewerPlayingState = false;
            } else {
                // If Pause button is displayed, click it calls appropriate function of ViewerController
                // Also change displayed icon and state variable
                viewer.pauseViewer();
                metaSeqPlay.setGraphic(playIcon);
                viewerPlayingState = true;
            }
        }
        // If no viewer is opened when Play is clicked, create one
        else {
            viewer = new ViewerController(this.metaSeqComboBox.getScene().getWindow(), metaSeqComboBox.getValue(), this);
        }
    }

    /**
     * Go to the previous media in the viewer
     */
    @FXML
    private void backward() {
        if (viewer != null) {
            viewer.prevMedia();
        }
    }

    /**
     * Go to the next media in the viewer
     */
    @FXML
    private void forward() {
        if (viewer != null) {
            viewer.nextMedia();
        }
    }

    /**
     * Close the viewer
     */
    public void closeViewer() {
        viewer = null;
        viewerPlayingState = true;
        metaSeqPlay.setGraphic(playIcon);
    }

    /**
     * Display or hide the option buttons of the meta-sequence
     */
    private void toggleMetaSeqOptions() {
        if (this.addState == 0) {
            metaSeqAdd.setGraphic(checkIcon);
            metaSeqAdd.setDisable(true);

            metaSeqAddName.setVisible(true);
            metaSeqAddName.setManaged(true);

            metaSeqAddQuit.setVisible(true);
            metaSeqAddQuit.setManaged(true);

            metaSeqOption.setVisible(false);
            metaSeqOption.setManaged(false);

            this.addState++;
        } else {
            metaSeqAdd.setDisable(false);
            metaSeqAdd.setGraphic(addIcon);

            metaSeqAddName.setText("");
            metaSeqAddName.setVisible(false);
            metaSeqAddName.setManaged(false);

            metaSeqAddQuit.setVisible(false);
            metaSeqAddQuit.setManaged(false);

            metaSeqOption.setVisible(true);
            metaSeqOption.setManaged(true);

            this.addState = 0;
        }
    }

    /**
     * Give the total duration of a meta-sequence
     *
     * @return The duration of a meta-sequence in seconds
     */
    private long computeTotalTime() {
        long totalTime = 0;
        MetaSequence metaSequence = this.metaSeqComboBox.getValue();

        for (Sequence sequence : metaSequence.getListSequences()) {
            for (Media media : sequence.getListMedias()) {
                totalTime += media.getDuration().getSeconds();
            }
        }
        return totalTime;
    }

    // Event interfaces

    public interface ModificationMetaSeqListener extends EventListener {
        void onModified(MetaSequence newMetaSequence);
    }

    public interface ModificationSequenceListener extends EventListener {
        void onModified(Sequence newSequence);
    }
}


