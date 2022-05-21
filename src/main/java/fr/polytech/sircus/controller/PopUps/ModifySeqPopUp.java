package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.MetaSequenceController;
import fr.polytech.sircus.controller.PreviewTimeline;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Callback;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

/**
 * Controller handling the management of a sequence modification
 */
public class ModifySeqPopUp {
    @Setter
    private Sequence sequence;
    @FXML
    private TextField sequenceName;
    @FXML
    private Button addMediaToSeq;
    @FXML
    private PreviewTimeline previewTimeline;

    /** Media table */
    @FXML
    private TableView<Media> mediaTable;

    /** Media table: column for locking */
    @FXML
    private TableColumn<Media, String> mediaTableColumnLock;

    /** Media table: name column */
    @FXML
    private TableColumn<Media, String> mediaTableColumnName;

    /** Media table: duration column */
    @FXML
    private TableColumn<Media, String> mediaTableColumnDuration;

    /** Media table: options column (edit buttons) */
    @FXML
    private TableColumn<Media, String> mediaTableColumnOptions;

    /** List of media and cross-stimulus media */
    private List<Media> listMediaPlusInterstim = new ArrayList<>();

    private final String MEDIAS_PATH = "medias/";

    /** The add file button object: allows the selection of a file for a media */
    //private final FileChooser fileChooserMedia;

    /** The add file button object: allows the selection of a file for a cross-stimulus */
    //private FileChooser fileChooserInterstim;

    /** Button to save the changes in the sequence */
    @FXML
    private Button saveAddMediaSeq;

    /** Cancel button, closes the sequence modification pop-up */
    @FXML
    private Button cancelAddMediaSeq;


    /** Object listener created in MetaSequenceController */
    private MetaSequenceController.ModificationSequenceListener listener;



    /**
     * Initialize the controller and its attributes, then adding functionality to each component.
     */
    public void init() {
        this.mediaTable.setEditable(true);
        this.sequenceName.setText(this.sequence.getName());

        this.mediaTableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.mediaTableColumnName.setStyle("-fx-alignment: CENTER;");

        this.mediaTableColumnDuration.setStyle("-fx-alignment: CENTER;");
        this.mediaTableColumnDuration.setCellValueFactory(cellData -> {
            String formattedDuration = cellData.getValue().getDuration().toString()
                    .replace("PT", "")
                    .replace("M", "m")
                    .replace("S", "s");
            return new SimpleStringProperty(formattedDuration);
        });
        this.mediaTableColumnDuration.setCellFactory(TextFieldTableCell.forTableColumn());

        Callback<TableColumn<Media, String>, TableCell<Media, String>> cellFactoryOption = new Callback<>() {
            @Override
            public TableCell<Media, String> call(final TableColumn<Media, String> param) {
                return new TableCell<>() {
                    final Button tableViewAddButton = new Button("");
                    final Button tableViewDeleteButton = new Button("");
                    final Button tableViewOptionButton = new Button("");
                    final HBox hBox = new HBox(tableViewAddButton, tableViewDeleteButton, tableViewOptionButton);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {

                            final FontIcon addIcon = new FontIcon("fa-plus");
                            final FontIcon delIcon = new FontIcon("fa-trash");
                            final FontIcon optionIcon = new FontIcon("fa-cog");

                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);

                            tableViewAddButton.setGraphic(addIcon);
                            tableViewAddButton.setTooltip(new Tooltip("Ajouter un interstim"));
                            tableViewDeleteButton.setGraphic(delIcon);
                            tableViewOptionButton.setGraphic(optionIcon);

                            // Add button
                            // If the media doesn't have an Interstim and isn't one
                            if (getTableView().getItems().get(getIndex()).getInterStim() == null &&
                                    !getTableView().getItems().get(getIndex()).getIsInterstim()) {
                                tableViewAddButton.setOnMouseClicked(event ->
                                {
                                    try {
                                        FileChooser fileChooserInterstim = new FileChooser();
                                        fileChooserInterstim.setTitle("Open file (interstim)");
                                        fileChooserInterstim.getExtensionFilters().addAll(
                                                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
                                        );
                                        File newInterstim = fileChooserInterstim.showOpenDialog(tableViewAddButton.getScene().getWindow());

                                        Path path = Paths.get(newInterstim.getPath());
                                        String absoluteMediaPath = new File(MEDIAS_PATH).getAbsolutePath();

                                        // We compare the absolute path of the "medias" directory with the new media's one.
                                        // If they are not the same directory, we copy the new media to the application's "medias" directory.
                                        if (!absoluteMediaPath.equals(path.toString().split("\\\\" + newInterstim.getName())[0])) {
                                            OutputStream os = new FileOutputStream(MEDIAS_PATH + newInterstim.getName());
                                            Files.copy(path, os);
                                        }

                                        Media newMedia = new Media(
                                                newInterstim.getName(),
                                                newInterstim.getName(),
                                                Duration.ofSeconds(1),
                                                TypeMedia.PICTURE,
                                                null
                                        );
                                        newMedia.setIsInterstim(true); // Indicate that the new media is an interstim

                                        getTableView().getItems().get(getIndex()).setInterStim(newMedia);

                                        constructMediaInterstimList();
                                        mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                        mediaTable.refresh();
                                    } catch (Exception e) {
                                        System.out.print("Aucun fichier selectionné.");
                                    }
                                });
                            } else { // The media has an Interstim or is one
                                tableViewAddButton.setDisable(true);
                            }

                            // Delete button
                            tableViewDeleteButton.setOnAction(event ->
                            {
                                if (getTableView().getItems().get(getIndex()).getIsInterstim()) {
                                    // +1 to get the parent media of the interstim
                                    // I didn't find a better way to do it
                                    getTableView().getItems().get(getIndex()+1).setInterStim(null);
                                }
                                else {
                                    sequence.removeMedia(getTableView().getItems().get(getIndex()));
                                }
                                constructMediaInterstimList();
                                mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                mediaTable.refresh();
                            });

                            // Option button
                            tableViewOptionButton.setOnAction(event ->
                            {
                                try {
                                    FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_media_popup.fxml"));
                                    DialogPane dialogPane = fxmlLoader.load();
                                    ModifyMediaPopup controller = fxmlLoader.getController();

                                    Dialog<ButtonType> dialog = new Dialog<>();
                                    dialog.setDialogPane(dialogPane);
                                    dialog.setTitle("Modification du média");
                                    dialog.initModality(Modality.WINDOW_MODAL);
                                    dialog.initOwner(tableViewOptionButton.getScene().getWindow());

                                    Optional<ButtonType> clickedButton = dialog.showAndWait();
                                    if (clickedButton.get() == ButtonType.FINISH) {
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            try {
                                if (getTableRow().getItem().getIsInterstim())
                                    getTableRow().setStyle("-fx-background-color: #e6f2ff; -fx-text-background-color: black;");
                            } catch (NullPointerException e) {
                                getTableRow().setStyle("-fx-text-background-color: black;");
                            }

                            /*if (getTableRow().getItem().getIsInterstim()) {
                                getTableRow().setStyle("-fx-background-color: #e6f2ff; -fx-text-background-color: black;");
                            } else {
                                getTableRow().setStyle("-fx-background-color: #b3d9ff; -fx-text-background-color: black;");
                            }*/

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        Callback<TableColumn<Media, String>, TableCell<Media, String>> cellFactoryLock = new Callback<>() {
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

                            tableViewVerrCheckBox.setOnAction(event -> media.setLock(tableViewVerrCheckBox.isSelected()));

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        this.mediaTableColumnLock.setCellFactory(cellFactoryLock);
        this.mediaTableColumnOptions.setCellFactory(cellFactoryOption);

        constructMediaInterstimList();
        this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));

        this.previewTimeline.addListMedia(this.sequence.getListMedias());

        /*this.cancelAddMediaSeq.setOnMouseClicked(mouseEvent -> cancelAddSeq());
        this.saveAddMediaSeq.setOnMouseClicked(mouseEvent -> saveMediasToSeq());
        this.addMediaToSeq.setOnMouseClicked(mouseEvent -> addMediaToSeq());
        this.titleSequenceLabel.setOnKeyPressed(mouseEvent -> {
            KeyCode keyCode = mouseEvent.getCode();
            if (keyCode.equals(KeyCode.ENTER)) {
                modifySequenceName();
            }
        });*/
    }

    /**
     * Media list builder and cross-stimulus
     */
    private void constructMediaInterstimList() {

        this.listMediaPlusInterstim.clear();

        for (int i = 0; i < this.sequence.getListMedias().size(); i++) {
            if (this.sequence.getListMedias().get(i).getInterStim() != null) {
                this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i).getInterStim());
            }

            this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i));
        }
    }

    @FXML
    private void addMedia() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_media_popup.fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        AddMediaPopUp controller = fxmlLoader.getController();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Ajouter un média");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(addMediaToSeq.getScene().getWindow());

        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.get() == ButtonType.FINISH) {
        }
    }

    /**
     * Method modifying the name of the sequence
     */
    /*private void modifySequenceName() {
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
    }*/


    /**
     * Method adding media to the sequence
     */
    /*@FXML
    private void addMediaToSeq() {
        SequenceModificationListener listener = sequence -> {
            constructMediaInterstimList();
            this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
            this.mediaTable.refresh();
        };

        new AddMediaPopUp(
                this.saveAddMediaSeq.getScene().getWindow(),
                FXCollections.observableList(this.sequence.getListMedias()),
                this.sequence,
                listener,
                fileChooserMedia,
                fileChooserInterstim
        );
    }*/

    /**
     * Method saving any modification made on the sequence and close the pop-up
     */
    /*@FXML
    private void saveMediasToSeq() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.sequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            this.sequence.setName(this.titleSequenceLabel.getText());
            this.sequence.computeDuration();//.setDuration(sequence.getDuration());
            this.listener.onModified(this.sequence);
            this.popUpStage.close();
        }
    }*/

    /**
     * Event listener of a sequence modification
     */
    /*public interface SequenceModificationListener extends EventListener {
        void onModified(Sequence sequence);
    }*/

    /**
     * Event listener of a media modification
     */
    /*public interface MediaModificationListener extends EventListener {
        void onModified(Media media);
    }*/
}
