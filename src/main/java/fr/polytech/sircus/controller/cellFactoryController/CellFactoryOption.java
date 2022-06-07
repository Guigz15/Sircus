package fr.polytech.sircus.controller.cellFactoryController;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.ModifyMediaPopUp;
import fr.polytech.sircus.model.AbstractMedia;
import fr.polytech.sircus.model.Interstim;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.TypeMedia;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
import java.util.List;
import java.util.Optional;

public class CellFactoryOption implements Callback<TableColumn<AbstractMedia, String>, TableCell<AbstractMedia, String>> {

    private TableView<AbstractMedia> mediaTable;

    private List<AbstractMedia> listMediaPlusInterstim;

    private final String MEDIAS_PATH = "medias/";

    public CellFactoryOption(TableView<AbstractMedia> mediaTable, List<AbstractMedia> listMediaPlusInterstim) {
        this.mediaTable = mediaTable;
        this.listMediaPlusInterstim = listMediaPlusInterstim;
    }

    @Override
    public TableCell<AbstractMedia, String> call(TableColumn<AbstractMedia, String> abstractMediaStringTableColumn) {
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
                    if (mediaTable.getItems().get(getIndex()) instanceof Media) {
                        Media parentMedia = (Media) mediaTable.getItems().get(getIndex());
                        if (parentMedia.getInterstim() == null) {
                            tableViewAddButton.setOnMouseClicked(event ->
                            {
                                try {
                                    FileChooser fileChooserInterstim = new FileChooser();
                                    fileChooserInterstim.setTitle("Open file (interstim)");
                                    fileChooserInterstim.getExtensionFilters().addAll(
                                            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
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

                                    Interstim newInterstim = new Interstim(
                                            interstimFile.getName(),
                                            Duration.ofSeconds(1),
                                            TypeMedia.PICTURE,
                                            parentMedia
                                    );

                                    mediaTable.getItems().add(mediaTable.getItems().indexOf(parentMedia), newInterstim);
                                    mediaTable.refresh();
                                } catch (Exception e) {
                                    System.out.print("Aucun fichier selectionné.");
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
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_media_popup.fxml"));
                            DialogPane dialogPane = fxmlLoader.load();
                            ModifyMediaPopUp controller = fxmlLoader.getController();
                            controller.getResizeImage().setSelected(mediaTable.getItems().get(getIndex()).isResizable());
                            controller.getBackgroundColor().setValue(mediaTable.getItems().get(getIndex()).getBackgroundColor());

                            Dialog<ButtonType> dialog = new Dialog<>();
                            dialog.setDialogPane(dialogPane);
                            dialog.setTitle("Modification du média");
                            dialog.initModality(Modality.WINDOW_MODAL);
                            dialog.initOwner(tableViewOptionButton.getScene().getWindow());
                            Optional<ButtonType> clickedButton = dialog.showAndWait();
                            if (clickedButton.get() == ButtonType.FINISH) {
                                mediaTable.getItems().get(getIndex()).setResizable(controller.getResizeImage().isSelected());
                                mediaTable.getItems().get(getIndex()).setBackgroundColor(controller.getBackgroundColor().getValue());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    if (getTableRow().getItem() instanceof Interstim) {
                        getTableRow().setStyle("-fx-background-color: #e6f2ff; -fx-text-background-color: black;");
                        tableViewAddButton.setDisable(true);
                    } else {
                        getTableRow().setStyle("-fx-text-background-color: black;");
                    }

                    setGraphic(hBox);
                }
                setText(null);
            }
        };
    }
}


