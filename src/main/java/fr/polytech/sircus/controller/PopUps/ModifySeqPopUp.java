package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.Etape2AdminController;
import fr.polytech.sircus.controller.PreviewTimeline;
import fr.polytech.sircus.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
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
import java.util.*;

/**
 * Controller handling the management of a sequence modification
 */
public class ModifySeqPopUp {

    //DataFormat use for drag and drop sequences in listView. Don't make it final because it make error
    private DataFormat SERIALIZED_MIME_TYPE = Etape2AdminController.SERIALIZED_MIME_TYPE;

    @Setter
    @Getter
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

    //last index of the selected media
    private int lastIndexSelectedMedia;



    /**
     * Initialize the controller and its attributes, then adding functionality to each component.
     */
    public void init() {

        this.mediaTable.setEditable(true);

        this.sequenceName.setText(this.sequence.getName());

        this.mediaTableColumnName.setCellValueFactory(new PropertyValueFactory<>("filename"));
        this.mediaTableColumnName.setStyle("-fx-alignment: CENTER;");

        this.mediaTableColumnDuration.setStyle("-fx-alignment: CENTER;");
        this.mediaTableColumnDuration.setCellValueFactory(cellData -> {
            String formattedDuration = cellData.getValue().getDuration().toString()
                    .replaceAll("[^0-9.,]", "");
            return new SimpleStringProperty(formattedDuration);
        });
        this.mediaTableColumnDuration.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setDuration(Duration.ofSeconds(Long.parseLong(event.getNewValue())));
        });
        this.mediaTableColumnDuration.setCellFactory(TextFieldTableCell.forTableColumn());

        Callback<TableColumn<AbstractMedia, String>, TableCell<AbstractMedia, String>> cellFactoryOption = new Callback<>() {
            @Override
            public TableCell<AbstractMedia, String> call(final TableColumn<AbstractMedia, String> param) {
                TableCell<AbstractMedia,String> tableCell = new TableCell<AbstractMedia,String>() {
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
                            if (mediaTable.getItems().get(getIndex()) instanceof Media) {
                                Media parentMedia = (Media) mediaTable.getItems().get(getIndex());
                                if (parentMedia.getInterstim() == null) {
                                    tableViewAddButton.setOnMouseClicked(event ->
                                    {
                                        try {
                                            FileChooser fileChooserInterstim = new FileChooser();
                                            fileChooserInterstim.setTitle("Open file (interstim)");
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

                                            Interstim newInterstim = new Interstim(interstimFile.getName(), Duration.ofSeconds(1), TypeMedia.PICTURE, parentMedia);

                                            mediaTable.getItems().add(mediaTable.getItems().indexOf(parentMedia), newInterstim);
                                            mediaTable.refresh();
                                        } catch (Exception e) {
                                            System.out.print("Aucun fichier sélectionné.");
                                        }
                                    });
                                } else { // The media has an Interstim or is one
                                    tableViewAddButton.setDisable(true);
                                }
                            }

                            // Delete button
                            tableViewDeleteButton.setOnAction(event ->
                            {
                                if (mediaTable.getItems().get(getIndex()) instanceof Interstim) {
                                    Interstim interstim = (Interstim) mediaTable.getItems().get(getIndex());
                                    mediaTable.getItems().remove(interstim);
                                    listMediaPlusInterstim.remove(interstim);
                                    interstim.getMedia().setInterstim(null);
                                } else {
                                    Media media = (Media) mediaTable.getItems().get(getIndex());
                                    if (media.getInterstim() != null) {
                                        mediaTable.getItems().remove(media.getInterstim());
                                        listMediaPlusInterstim.remove(media.getInterstim());
                                        media.setInterstim(null);
                                    }
                                    mediaTable.getItems().remove(media);
                                    listMediaPlusInterstim.remove(media);
                                }
                                mediaTable.refresh();
                            });

                            // Option button
                            tableViewOptionButton.setOnAction(event ->
                            {
                                lastIndexSelectedMedia = getIndex();
                                openModifyViewForMedia();
                            });

                            if (getTableRow().getItem() instanceof Interstim) {
                                getTableRow().setStyle("-fx-background-color: #e6f2ff; -fx-text-background-color: black;");
                                tableViewAddButton.setDisable(true);
                            } else {
                                getTableRow().setStyle("-fx-text-background-color: black;");
                                tableViewAddButton.setDisable(false);
                            }

                            setGraphic(hBox);
                        }
                        setText(null);
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
                                Interstim interstim = (Interstim) getTableView().getItems().get(getIndex());
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

        this.mediaTable.setOnMouseClicked(new EventHandler<MouseEvent>() { //click
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){ // double click
                    lastIndexSelectedMedia = mediaTable.getSelectionModel().getSelectedIndex();
                    openModifyViewForMedia();
                }
            }
        });

        this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));

        this.mediaTable.setRowFactory(event ->{
            TableRow<AbstractMedia> row = new TableRow<>();

            //----------------------------------------------------------------------------------//
            //                        Drag and Drop behaviour for media                         //
            //----------------------------------------------------------------------------------//
            row.setOnDragDetected(dragEvent ->
            {
                if (! row.isEmpty()) {
                    if(row.getItem() instanceof Media ) {
                        Integer index = row.getIndex();
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
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        dragEvent.consume();
                    }
                }
            });

            row.setOnDragDropped(dragEvent -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {

                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Media draggedMedia = (Media) mediaTable.getItems().remove(draggedIndex);

                    if (draggedMedia.getInterstim() != null) {
                        mediaTable.getItems().remove(draggedMedia.getInterstim());
                        listMediaPlusInterstim.remove(draggedMedia.getInterstim());
                        listMediaPlusInterstim.remove(draggedMedia);
                    }

                    sequence.getListMedias().remove(draggedMedia);
                    int dropIndex ;
                    // the index of new media in sequence in order to add to the right place
                    int dropIndexInSequence ;

                    if (row.isEmpty()) {
                        dropIndex = mediaTable.getItems().size() ;
                        dropIndexInSequence = sequence.getListMedias().size();
                    } else {
                        dropIndex = row.getIndex();
                        dropIndexInSequence = sequence.getListMedias().indexOf(row.getItem());
                    }

                    sequence.getListMedias().add(dropIndexInSequence, draggedMedia);
                    //We add media
                    listMediaPlusInterstim.add(dropIndex,draggedMedia);
                    //we add interstim at good place if there is present
                    if (draggedMedia.getInterstim() != null) {
                        listMediaPlusInterstim.add(dropIndex ,draggedMedia.getInterstim());
                    }

                    dragEvent.setDropCompleted(true);
                    this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
                    dragEvent.consume();
                }
            });
            return row;
        });

        this.previewTimeline.addListMedia(this.sequence.getListMedias());
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
        if (clickedButton.get() == ButtonType.FINISH) {
            if (controller.getNewMedia() != null) {
                sequence.addMedia(controller.getNewMedia());
                constructMediaInterstimList();
                mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
            }
        }
    }

    /**
     * Methode wich open the pop up for modify sequence.
     */
    private void openModifyViewForMedia() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_media_popup.fxml"));
            DialogPane dialogPane = fxmlLoader.load();
            ModifyMediaPopUp controller = fxmlLoader.getController();
            controller.getResizeImage().setSelected(mediaTable.getItems().get(lastIndexSelectedMedia).isResizable());
            controller.getBackgroundColor().setValue(mediaTable.getItems().get(lastIndexSelectedMedia).getBackgroundColor());

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Modification du média");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(this.mediaTable.getScene().getWindow());

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.FINISH) {
                mediaTable.getItems().get(lastIndexSelectedMedia).setResizable(controller.getResizeImage().isSelected());
                mediaTable.getItems().get(lastIndexSelectedMedia).setBackgroundColor(controller.getBackgroundColor().getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
