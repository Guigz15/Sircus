package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.Step2Controller;
import fr.polytech.sircus.controller.PreviewTimeline;
import fr.polytech.sircus.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
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
import java.util.List;
import java.util.Optional;

/**
 * Controller handling the management of a sequence modification
 */
public class ModifySeqPopUp {

    //DataFormat use for drag and drop sequences in listView. Don't make it final because it make error
    private DataFormat SERIALIZED_MIME_TYPE = Step2Controller.SERIALIZED_MIME_TYPE;
    @Setter @Getter
    private Sequence sequence;
    @FXML
    @Getter
    private TextField sequenceName;
    @FXML
    private Button addMediaToSeq;
    @FXML
    private PreviewTimeline previewTimeline;

    /** Media table */
    @FXML
    private TableView<AbstractMedia> mediaTable;

    /** Media table: column for locking */
    @FXML
    private TableColumn<AbstractMedia, String> mediaTableColumnLock;

    /** Media table: name column */
    @FXML
    private TableColumn<AbstractMedia, String> mediaTableColumnName;

    /** Media table: duration column */
    @FXML
    private TableColumn<AbstractMedia, String> mediaTableColumnDuration;

    /** Media table: options column (edit buttons) */
    @FXML
    private TableColumn<AbstractMedia, String> mediaTableColumnOptions;

    /** List of media and cross-stimulus media */
    private List<AbstractMedia> listMediaPlusInterstim = new ArrayList<>();

    private final String MEDIAS_PATH = "medias/";

    /** Last index of the selected media in table */
    private int indexSelectedMediaInTable;




    /**
     * Initialize the controller and its attributes, then adding functionality to each component.
     */
    public void init() {

        this.mediaTable.setEditable(true);

        this.sequenceName.setText(this.sequence.getName());
        this.sequenceName.textProperty().addListener((observableValue, oldValue, newValue) -> sequence.setName(newValue));

        this.mediaTableColumnName.setCellValueFactory(new PropertyValueFactory<>("filename"));
        this.mediaTableColumnName.setStyle("-fx-alignment: CENTER;");

        this.mediaTableColumnDuration.setStyle("-fx-alignment: CENTER;");
        this.mediaTableColumnDuration.setCellFactory(TextFieldTableCell.forTableColumn());
        this.mediaTableColumnDuration.setCellValueFactory(cellData -> {
            String formattedDuration = cellData.getValue().getMinDuration().toString().replaceAll("[^\\d\\-]" , "") + "-"
                    + cellData.getValue().getMaxDuration().toString().replaceAll("[^\\d\\-]" , "");
            return new SimpleStringProperty(formattedDuration);
        });
        this.mediaTableColumnDuration.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setMinDuration(Duration.ofSeconds(Long.parseLong(event.getNewValue().split("\\-")[0])));
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setMaxDuration(Duration.ofSeconds(Long.parseLong(event.getNewValue().split("\\-")[1])));
        });

        Callback<TableColumn<AbstractMedia, String>, TableCell<AbstractMedia, String>> cellFactoryOption = new Callback<>() {
            @Override
            public TableCell<AbstractMedia, String> call(final TableColumn<AbstractMedia, String> param) {
                TableCell<AbstractMedia,String> tableCell = new TableCell<>() {
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
                            tableViewAddButton.setStyle("-fx-background-color: mediumseagreen");
                            tableViewDeleteButton.setGraphic(delIcon);
                            tableViewDeleteButton.setStyle("-fx-background-color: #f87167");
                            tableViewOptionButton.setGraphic(optionIcon);
                            tableViewOptionButton.setStyle("-fx-background-color: darkorange");

                            // Add button
                            // If the media doesn't have an Interstim and isn't one
                            if (mediaTable.getItems().get(getIndex()) instanceof Media) {
                                Media parentMedia = (Media) mediaTable.getItems().get(getIndex());
                                if (parentMedia.getInterstim() == null) {
                                    tableViewAddButton.setOnMouseClicked(event ->
                                    {
                                        try {
                                            FileChooser fileChooserInterstim = new FileChooser();
                                            fileChooserInterstim.setTitle("Open file (interstim)");
                                            if (SircusApplication.dataSircus.getPath().isCustomPath()) {
                                                fileChooserInterstim.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getLastPath()));
                                            } else {
                                                fileChooserInterstim.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getDefaultPath()));
                                            }
                                            fileChooserInterstim.getExtensionFilters().addAll(
                                                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
                                            );
                                            File interstimFile = fileChooserInterstim.showOpenDialog(tableViewAddButton.getScene().getWindow());

                                            Path path = Paths.get(interstimFile.getPath());
                                            String absoluteMediaPath = new File(MEDIAS_PATH).getAbsolutePath();

                                            // We compare the absolute path of the "medias" directory with the new media's one.
                                            // If they are not the same directory, we copy the new media to the application's "medias" directory.
                                            if (!absoluteMediaPath.equals(path.toString().split("\\\\" + interstimFile.getName())[0])) {
                                                OutputStream os = new FileOutputStream(MEDIAS_PATH + interstimFile.getName());
                                                Files.copy(path, os);
                                            }

                                            Interstim newInterstim = new Interstim(interstimFile.getName(), Duration.ofSeconds(1), Duration.ofSeconds(1), TypeMedia.PICTURE, parentMedia);

                                            mediaTable.getItems().add(mediaTable.getItems().indexOf(parentMedia), newInterstim);
                                            mediaTable.refresh();
                                        } catch (Exception e) {
                                            System.out.print("Aucun fichier sélectionné.");
                                        }
                                    });
                                    tableViewAddButton.setDisable(false);
                                } else { // The media has an Interstim or is one
                                    tableViewAddButton.setDisable(true);
                                }
                            }

                            // Delete button
                            tableViewDeleteButton.setOnAction(event ->
                            {
                                if (getTableRow().getItem() instanceof Interstim) {
                                    Interstim interstim = (Interstim) getTableRow().getItem();
                                    mediaTable.getItems().remove(interstim);
                                    interstim.getMedia().setInterstim(null);
                                } else {
                                    Media media = (Media) getTableRow().getItem();
                                    if (media.getInterstim() != null) {
                                        mediaTable.getItems().remove(media.getInterstim());
                                        media.setInterstim(null);
                                    }
                                    mediaTable.getItems().remove(media);
                                    sequence.getListMedias().remove(media);
                                }
                            });

                            // Option button
                            tableViewOptionButton.setOnAction(event ->
                            {
                                indexSelectedMediaInTable = getIndex();
                                openModifyViewForMedia();
                            });

                            if (getTableRow().getItem() instanceof Interstim) {
                                getTableRow().setStyle("-fx-background-color: #e6f2ff; -fx-text-background-color: black;");
                                tableViewAddButton.setDisable(true);
                            } else
                                getTableRow().setStyle("-fx-text-background-color: black;");

                            setGraphic(hBox);
                        }
                        setText(null);

                        previewTimeline.setSequence(sequence);
                    }
                };

                return tableCell;
            }
        };

        Callback<TableColumn<AbstractMedia, String>, TableCell<AbstractMedia, String>> cellFactoryLock = new Callback<>() {
            @Override
            public TableCell<AbstractMedia, String> call(final TableColumn<AbstractMedia, String> param) {
                return new TableCell<>() {
                    final CheckBox tableViewVerrCheckBox = new CheckBox("");
                    final HBox hBox = new HBox(tableViewVerrCheckBox);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty ) {
                            setGraphic(null);
                        } else {
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);

                            if (getTableView().getItems().get(getIndex()) instanceof Interstim) {
                                tableViewVerrCheckBox.setSelected(true);
                                tableViewVerrCheckBox.setDisable(true);
                            } else {
                                Media currentMedia = (Media) getTableView().getItems().get(getIndex());
                                tableViewVerrCheckBox.setDisable(false);
                                tableViewVerrCheckBox.setSelected(currentMedia.isLocked());
                                tableViewVerrCheckBox.setOnAction(event -> {
                                    currentMedia.setLocked(tableViewVerrCheckBox.isSelected());
                                });
                            }

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

        this.mediaTable.setRowFactory(event -> {
            TableRow<AbstractMedia> row = new TableRow<>();

            //----------------------------------------------------------------------------------//
            //                        Drag and Drop behaviour for media                         //
            //----------------------------------------------------------------------------------//
            row.setOnDragDetected(dragEvent ->
            {
                if (!row.isEmpty()) {
                    if(row.getItem() instanceof Media) {
                        int index = row.getIndex();
                        Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                        db.setDragView(row.snapshot(null, null));
                        ClipboardContent cc = new ClipboardContent();
                        cc.put(SERIALIZED_MIME_TYPE, index);
                        db.setContent(cc);
                        dragEvent.consume();
                    }
                }
            });

            row.setOnDragOver(dragEvent -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        Media draggedMedia = (Media) mediaTable.getItems().get((Integer) db.getContent(SERIALIZED_MIME_TYPE));
                        if (draggedMedia.getInterstim() == null || row.getIndex() != mediaTable.getItems().indexOf(draggedMedia.getInterstim())) {
                            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                            dragEvent.consume();
                        }
                    }
                }
            });

            row.setOnDragDropped(dragEvent -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Media draggedMedia = (Media) mediaTable.getItems().remove(draggedIndex);

                    if (draggedMedia.getInterstim() != null)
                        mediaTable.getItems().remove(draggedMedia.getInterstim());

                    sequence.getListMedias().remove(draggedMedia);

                    int dropIndexInSequence;

                    if (row.isEmpty()) {
                        dropIndexInSequence = sequence.getListMedias().size();
                    } else {
                        if (row.getItem() instanceof Media)
                            dropIndexInSequence = sequence.getListMedias().indexOf((Media) row.getItem());
                        else
                            dropIndexInSequence = sequence.getListMedias().indexOf(((Interstim) row.getItem()).getMedia());
                    }

                    sequence.getListMedias().add(dropIndexInSequence, draggedMedia);

                    constructMediaInterstimList();

                    dragEvent.setDropCompleted(true);
                    this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
                    dragEvent.consume();
                }
            });
            return row;
        });
    }

    /**
     * Media list builder and cross-stimulus
     */
    private void constructMediaInterstimList() {

        this.listMediaPlusInterstim.clear();

        for (int i = 0; i < this.sequence.getListMedias().size(); i++) {
            if (this.sequence.getListMedias().get(i).getInterstim() != null) {
                this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i).getInterstim());
            }

            this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i));
        }
    }

    @FXML
    private void addMedia() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_media_popup.fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        AddMediaPopUp controller = fxmlLoader.getController();
        controller.getMediasList().setItems(FXCollections.observableList(sequence.getListMedias()));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Ajouter un média");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(addMediaToSeq.getScene().getWindow());

        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.isPresent()) {
            if (clickedButton.get() == ButtonType.FINISH) {
                if (controller.getNewMedia() != null) {
                    sequence.addMedia(controller.getNewMedia());
                    constructMediaInterstimList();
                    mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
                }
            }
        }
    }

    /**
     * Method which open the pop-up for modify sequence.
     */
    private void openModifyViewForMedia() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_media_popup.fxml"));
            DialogPane dialogPane = fxmlLoader.load();
            ModifyMediaPopUp controller = fxmlLoader.getController();
            controller.getResizeImage().setSelected(mediaTable.getItems().get(indexSelectedMediaInTable).isResizable());
            controller.getBackgroundColor().setValue(mediaTable.getItems().get(indexSelectedMediaInTable).getBackgroundColor());

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Modification du média");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(this.mediaTable.getScene().getWindow());

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.isPresent()) {
                if (clickedButton.get() == ButtonType.FINISH) {
                    AbstractMedia abstractMediaToModified = mediaTable.getItems().get(indexSelectedMediaInTable);
                    abstractMediaToModified.setResizable(controller.getResizeImage().isSelected());
                    abstractMediaToModified.setBackgroundColor(controller.getBackgroundColor().getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
