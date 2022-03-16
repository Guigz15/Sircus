package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.MetaSequenceController;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

/**
 * Controller handling the management of a sequence modification
 */
public class modifySeqPopUp {

    /** The add file button object: allows the selection of a file for a media */
    private final FileChooser fileChooserMedia;

    /** The add file button object: allows the selection of a file for a cross-stimulus */
    private FileChooser fileChooserInterstim;

    /** Button to save the changes in the sequence */
    @FXML
    private Button saveAddMediaSeq;

    /** Cancel button, closes the sequence modification pop-up */
    @FXML
    private Button cancelAddMediaSeq;

    /** Button to add media to the sequence */
    @FXML
    private Button addMediaToSeq;

    /** Text field for the name of the sequence to be modified */
    @FXML
    private TextField titleSequenceLabel;

    /** Media table */
    @FXML
    private TableView<Media> mediaTable;

    /** Media table: column for locking */
    @FXML
    private TableColumn<Media, String> mediaTableColumnVerrouillage;

    /** Media table: name column */
    @FXML
    private TableColumn<Media, String> mediaTableColumnName;

    /** Media table: duration column */
    @FXML
    private TableColumn<Media, String> mediaTableColumnDuration;

    /** Media table: options column (edit buttons) */
    @FXML
    private TableColumn<Media, String> mediaTableColumnOption;

    /** Sequence to be modified */
    private Sequence sequence = null;

    /** Modification pop-up */
    private Stage popUpStage = null;

    /** Object listener created in MetaSequenceController */
    private MetaSequenceController.ModificationSequenceListener listener;

    /** List of media and cross-stimulus media */
    private List<Media> listMediaPlusInterstim = null;

    /** Temporary object used to store media (useful when creating media by copying) */
    private Media tempMedia = null;

    /**
     * Constructor of the controller of the sequence modification pop-up and its components
     *
     * @param owner      Main window
     * @param listMedias List of media contained in the sequence
     * @param sequence   The sequence to be modified
     * @param listener   The event listener of sequence modification from MetaSequenceController
     */
    public modifySeqPopUp(Window owner, ObservableList<Media> listMedias, Sequence sequence,
                          MetaSequenceController.ModificationSequenceListener listener, FileChooser fileChooserMedia,
                          FileChooser fileChooserInterstim) {

        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_seq_popup.fxml"));
        fxmlLoader.setController(this);

        this.fileChooserMedia = fileChooserMedia;
        this.fileChooserInterstim = fileChooserInterstim;

        try {
            this.sequence = sequence;
            this.listener = listener;
            this.listMediaPlusInterstim = new ArrayList<>();
            constructMediaInterstimList();

            this.fileChooserInterstim = new FileChooser();
            this.fileChooserInterstim.setTitle("Open file (interstim)");
            this.fileChooserInterstim.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            Scene dialogScene = new Scene(fxmlLoader.load(), 1000, 500);
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(250); //220 (+30 height of the window's header on Windows)
            dialog.setMinWidth(585); //575 (+10 width of the window's header on Windows)
            dialog.setTitle("Modifier la séquence : " + this.sequence.getName());

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Media list builder and cross-stimulus
     */
    private void constructMediaInterstimList() {

        this.listMediaPlusInterstim.clear();

        for (int i = 0; i < this.sequence.getListMedias().size(); i++) {
            this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i));

            if (this.sequence.getListMedias().get(i).getInterStim() != null) {
                this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i).getInterStim());
            }
        }
    }

    /**
     * Method of initializing the controller and its attributes, then adding functionality to each component.
     */
    @FXML
    private void initialize() {

        this.titleSequenceLabel.setText(this.sequence.getName());
        this.mediaTableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        this.mediaTableColumnDuration.setCellValueFactory(cellData -> {
            String formattedDuration = cellData.getValue().getDuration().toString()
                    .replace("PT", "")
                    .replace("M", "m")
                    .replace("S", "s");
            return new SimpleStringProperty(formattedDuration);
        });

        Callback<TableColumn<Media, String>, TableCell<Media, String>> cellFactoryOption = new Callback<>() {
            @Override
            public TableCell<Media, String> call(final TableColumn<Media, String> param) {
                return new TableCell<>() {
                    final Button tableViewOptionButton = new Button("");
                    final Button tableViewDeleteButton = new Button("");
                    final Button tableViewAddButton = new Button("");
                    final HBox hBox = new HBox(tableViewAddButton, tableViewOptionButton, tableViewDeleteButton);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Boolean interstim = Boolean.FALSE;
                            if (tempMedia != null) {
                                if (getTableRow().getItem() == tempMedia.getInterStim()) {
                                    interstim = Boolean.TRUE;
                                }
                            }
                            tempMedia = getTableRow().getItem();

                            final FontIcon addIcon = new FontIcon("fa-plus");
                            final FontIcon cogIcon = new FontIcon("fa-cog");
                            final FontIcon delIcon = new FontIcon("fa-trash");

                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);

                            tableViewAddButton.setGraphic(addIcon);
                            tableViewOptionButton.setGraphic(cogIcon);
                            tableViewDeleteButton.setGraphic(delIcon);

                            tableViewOptionButton.setOnMouseClicked(event ->
                            {
                                Media media = getTableView().getItems().get(getIndex());
                                modifyMediaInSeq(media);
                            });


                            if (interstim) {
                                tableViewAddButton.setDisable(true);

                                tableViewDeleteButton.setOnAction(event ->
                                {
                                    for (Media media : listMediaPlusInterstim) {
                                        if (media.getInterStim() == getTableView().getItems().get(getIndex())) {
                                            media.setInterStim(null);

                                        }
                                    }

                                    constructMediaInterstimList();
                                    mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                    mediaTable.refresh();
                                });

                                getTableRow().setStyle("-fx-background-color : #e6f2ff");
                            } else {
                                tableViewDeleteButton.setOnAction(event ->
                                {
                                    sequence.removeMedia(getTableView().getItems().get(getIndex()));
                                    constructMediaInterstimList();
                                    mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                    mediaTable.refresh();
                                });

                                if (getTableView().getItems().get(getIndex()).getInterStim() == null) {
                                    tableViewAddButton.setOnMouseClicked(event ->
                                    {
                                        try {
                                            File newInterstim = fileChooserInterstim.showOpenDialog(popUpStage);
                                            Path path = Paths.get(newInterstim.getPath());
                                            OutputStream os = new FileOutputStream("medias/" + newInterstim.getName());
                                            Files.copy(path, os);

                                            Media newMedia = new Media(
                                                    newInterstim.getName(),
                                                    newInterstim.getName(),
                                                    Duration.ofSeconds(1),
                                                    TypeMedia.PICTURE,
                                                    null
                                            );

                                            getTableView().getItems().get(getIndex()).setInterStim(newMedia);

                                            constructMediaInterstimList();
                                            mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                            mediaTable.refresh();
                                        } catch (Exception e) {
                                            System.out.print("Aucun fichier selectionné.");
                                        }
                                    });
                                } else {
                                    tableViewAddButton.setDisable(true);
                                }

                                getTableRow().setStyle("-fx-background-color : #b3d9ff");
                            }

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        Callback<TableColumn<Media, String>, TableCell<Media, String>> cellFactoryVerr = new Callback<>() {
            @Override
            public TableCell<Media, String> call(final TableColumn<Media, String> param) {
                return new TableCell<>() {
                    final CheckBox tableViewVerrCheckBox = new CheckBox("");
                    final HBox hBox = new HBox(tableViewVerrCheckBox);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);
                            Media media = getTableView().getItems().get(getIndex());

                            tableViewVerrCheckBox.setSelected(media.getLock());

                            tableViewVerrCheckBox.setOnAction(event ->
                                    media.setLock(tableViewVerrCheckBox.isSelected()));

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        this.mediaTableColumnOption.setCellFactory(cellFactoryOption);
        this.mediaTableColumnVerrouillage.setCellFactory(cellFactoryVerr);

        this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));

        this.cancelAddMediaSeq.setOnMouseClicked(mouseEvent -> cancelAddSeq());
        this.saveAddMediaSeq.setOnMouseClicked(mouseEvent -> saveMediasToSeq());
        this.addMediaToSeq.setOnMouseClicked(mouseEvent -> addMediaToSeq());
        this.titleSequenceLabel.setOnKeyPressed(mouseEvent -> {
            KeyCode keyCode = mouseEvent.getCode();
            if (keyCode.equals(KeyCode.ENTER)) {
                modifySequenceName();
            }
        });
    }

    /**
     * Method of modifying the name of the sequence
     */
    private void modifySequenceName() {
        if (!Objects.equals(this.sequence.getName(), this.titleSequenceLabel.getText())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Etes-vous sûr de vouloir renommer la séquence en " + this.titleSequenceLabel.getText() + " ?",
                    ButtonType.YES,
                    ButtonType.NO);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                this.sequence.setName(this.titleSequenceLabel.getText());
                this.popUpStage.setTitle("Modifier la séquence : " + this.sequence.getName());
                this.listener.onModified(this.sequence);
            }
        }
    }


    /**
     * Method of adding media to the sequence
     */
    @FXML
    private void addMediaToSeq() {
        SequenceModificationListener listener = sequence -> {
            constructMediaInterstimList();
            this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
            this.mediaTable.refresh();
        };

        new addMediaPopUp(
                this.saveAddMediaSeq.getScene().getWindow(),
                FXCollections.observableList(this.sequence.getListMedias()),
                this.sequence,
                listener,
                fileChooserMedia,
                fileChooserInterstim
        );
    }


    /**
     * Method of modifying media in the sequence
     *
     * @param media Media to be modified
     */
    @FXML
    private void modifyMediaInSeq(Media media) {
        SequenceModificationListener listener1 = sequence -> {
            constructMediaInterstimList();
            this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
            this.mediaTable.refresh();
        };

        MediaModificationListener listener2 = temp -> this.mediaTable.refresh();

        new modifyMediaPopUp(
                this.saveAddMediaSeq.getScene().getWindow(),
                this.sequence,
                media,
                listener1,
                listener2
        );
    }

    /**
     * Method of closing the sequence modification pop-up
     */
    @FXML
    private void cancelAddSeq() {
        this.popUpStage.close();
    }

    /**
     * Method to save any modifications made on the sequence and close the pop-up.
     */
    @FXML
    private void saveMediasToSeq() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.sequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            this.sequence.setName(this.titleSequenceLabel.getText());
            this.sequence.setDuration(sequence.getDuration());
            this.listener.onModified(this.sequence);
            this.popUpStage.close();
        }
    }

    /**
     * Event listener of a sequence modification
     */
    public interface SequenceModificationListener extends EventListener {
        void onModified(Sequence sequence);
    }

    /**
     * Event listener of a media modification
     */
    public interface MediaModificationListener extends EventListener {
        void onModified(Media media);
    }
}
