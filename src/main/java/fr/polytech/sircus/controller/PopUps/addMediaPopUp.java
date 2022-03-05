package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import fr.polytech.sircus.model.PathMedia;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class addMediaPopUp {

    /**
     * FileChooser to choose the file used as a media.
     */
    @FXML
    private final FileChooser fileChooserMedia;

    /**
     * FileChooser to choose the file shown after the media (interstim).
     */
    @FXML
    private final FileChooser fileChooserInterstim;

    /**
     * Label of the name of the selected media.
     */
    @FXML
    private Label nameNewMedia;

    /**
     * Text field to input the duration of the media.
     */
    @FXML
    private TextField durationField;

    /**
     * File of the selected media.
     */
    @FXML
    private File newFileMedia;

    /**
     * Label of the name of the selected interstim.
     */
    @FXML
    private Label nameNewInterstim;

    /**
     * File of the selected interstim.
     */
    @FXML
    private File newFileInterstim;

    /**
     * Button to cancel the addition of a media.
     */
    @FXML
    private Button addMediaCancel;

    /**
     * Button to save the new media.
     */
    @FXML
    private Button addMediaSave;

    /**
     * Button to choose the file used as a media.
     */
    @FXML
    private Button addMediaFile;

    /**
     * Button to choose the file shown after the media (interstim).
     */
    @FXML
    private Button addInterstimFile;

    /**
     * Radio button used to create a new media.
     */
    @FXML
    private RadioButton addNewMedia;

    /**
     * Radio button used to copy an existing media.
     */
    @FXML
    private RadioButton addCopyMedia;

    /**
     * ComboBox containing all the existing medias.
     */
    @FXML
    private ComboBox<Media> nameListMedias;

    /**
     * List of the medias of the mother sequence.
     */
    private ObservableList<Media> listMedias = null;

    /**
     * Sequence in which the media will be added.
     */
    private Sequence sequence = null;

    /**
     * The popup to add a media.
     */
    private Stage popUpStage = null;

    /**
     * Event listener checking for sequence's modification from the modifySeqPopUp controller.
     */
    private modifySeqPopUp.SequenceModificationListener listener = null;

    /**
     * Constructor of the popup's controller to add a media to a sequence.
     *
     * @param owner      main window.
     * @param listMedias list of the medias of the sequence.
     * @param sequence   the sequence in which the media will be added.
     * @param listener   vent listener checking for sequence's modification from the modifySeqPopUp controller.
     */
    public addMediaPopUp(Window owner, ObservableList<Media> listMedias, Sequence sequence,
                         modifySeqPopUp.SequenceModificationListener listener, FileChooser fileChooserMedia,
                         FileChooser fileChooserInterstim) {

        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_media_popup.fxml"));
        fxmlLoader.setController(this);

        PathMedia paths = SircusApplication.dataSircus.getPath();
        String pathUse;
        this.fileChooserMedia = fileChooserMedia;
        this.fileChooserInterstim = fileChooserInterstim;

        try {
            this.sequence = sequence;
            this.listMedias = listMedias;
            this.listener = listener;

            if(paths.isCustomPath() == true || paths.getLastPath() == null){
                pathUse = paths.getDefaultPath();
            } else {
                pathUse = paths.getLastPath();
            }

            this.fileChooserMedia.setTitle("Open file (media)");
            this.fileChooserMedia.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image and video Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.mp4")
            );
            this.fileChooserMedia.setInitialDirectory(
                    new File(pathUse)
            );

            this.fileChooserInterstim.setTitle("Open file (interstim)");
            this.fileChooserInterstim.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
            this.fileChooserInterstim.setInitialDirectory(
                    new File(pathUse)
            );

            Scene dialogScene = new Scene(fxmlLoader.load());
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(200); //170 (+30 height of the window header on the Windows OS)
            dialog.setMinWidth(290); //280 (+10 width of the window header on the Windows OS)
            dialog.setTitle("Ajout Media à " + this.sequence.getName());

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the controller and its attributes.
     */
    @FXML
    private void initialize() {
        this.nameListMedias.setItems(this.listMedias);
        this.nameListMedias.getSelectionModel().select(0);
        this.nameListMedias.setDisable(true);
        this.addMediaSave.setDisable(true);
        this.addMediaFile.setDisable(true);

        this.addNewMedia.setSelected(true);
        selectAddNewMedia();

        this.addInterstimFile.setOnMouseClicked(mouseEvent -> selectInterstimFile());
        this.durationField.setOnKeyReleased(keyEvent -> checkNameNewMediaFilled());
        this.nameListMedias.setOnMouseClicked(mouseEvent -> checkNameNewMediaFilled());
        this.addCopyMedia.setOnMouseClicked(mouseEvent -> selectAddCopyMedia());
        this.addNewMedia.setOnMouseClicked(mouseEvent -> selectAddNewMedia());
        this.addMediaFile.setOnMouseClicked(mouseEvent -> selectMediaFile());
        this.addMediaCancel.setOnMouseClicked(mouseEvent -> cancelAddMedia());
        this.addMediaSave.setOnMouseClicked(mouseEvent -> {
            try {
                addMediaToSeq();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Allows to pick a file used as the interstim in the user's computer.
     */
    private void selectInterstimFile() {
        this.newFileInterstim = this.fileChooserInterstim.showOpenDialog(this.popUpStage);

        try {
            if (this.newFileInterstim.isFile()) {
                this.nameNewInterstim.setText(this.newFileInterstim.getName());

                String newPath = Paths.get(newFileInterstim.getPath()).getParent().toString();
                SircusApplication.dataSircus.getPath().setLastPath(newPath);
                if(SircusApplication.dataSircus.getPath().isCustomPath() == false){
                    this.fileChooserMedia.setInitialDirectory(
                            new File(newPath)
                    );
                    this.fileChooserInterstim.setInitialDirectory(
                            new File(newPath)
                    );
                }
            }
        } catch (Exception e) {
            System.out.print("Aucun média sélectionné.");
        }
    }

    /**
     * Allows to pick a file used as the media in the user's computer.
     */
    private void selectMediaFile() {
        this.newFileMedia = this.fileChooserMedia.showOpenDialog(this.popUpStage);

        try {
            if (this.newFileMedia.isFile()) {
                this.nameNewMedia.setText(this.newFileMedia.getName());
                checkNameNewMediaFilled();

                String newPath = Paths.get(newFileMedia.getPath()).getParent().toString();
                SircusApplication.dataSircus.getPath().setLastPath(newPath);
                if(SircusApplication.dataSircus.getPath().isCustomPath() == false){
                    this.fileChooserMedia.setInitialDirectory(
                            new File(newPath)
                    );
                    this.fileChooserInterstim.setInitialDirectory(
                            new File(newPath)
                    );
                }
            }
        } catch (Exception e) {
            System.out.print("Aucun média sélectionné.");
        }
    }

    /**
     * Checks if a file has been selected for the media and if a duration has been set.
     * If true, activates the button to save and quit.
     * Disables it otherwise.
     */
    private void checkNameNewMediaFilled() {
        if (this.addNewMedia.isSelected()) {
            this.addMediaSave.setDisable(
                    this.nameNewMedia.getText().length() <= 0 || this.durationField.getText().length() <= 0
            );
        }
    }

    /**
     * Enables the widgets to add a new media if the radio button to create a new media is selected.
     * Disables the other widgets.
     */
    private void selectAddNewMedia() {
        if (addCopyMedia.isSelected()) {
            this.addCopyMedia.fire();
        }
        this.nameListMedias.setDisable(true);
        this.addMediaFile.setDisable(false);
        this.addInterstimFile.setDisable(false);
        this.durationField.setDisable(false);

        checkNameNewMediaFilled();
    }

    /**
     * Enables the widgets to copy a media if the radio button to copy a media is selected.
     * Disables the other widgets.
     */
    private void selectAddCopyMedia() {
        if (this.addNewMedia.isSelected()) {
            this.addNewMedia.fire();
        }

        if (this.sequence.getListMedias().size() > 0) {
            this.addMediaSave.setDisable(false);
        }
        this.nameListMedias.setDisable(false);
        this.addMediaFile.setDisable(true);
        this.addInterstimFile.setDisable(true);
        this.durationField.setDisable(true);
    }

    /**
     * Adds a media to the sequence.
     *
     * @throws IOException if there is a problem relative to the file selection.
     */
    private void addMediaToSeq() throws IOException {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.sequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            if (this.addNewMedia.isSelected()) {
                if (this.newFileMedia.exists()) {

                    Path path = Paths.get(this.newFileMedia.getPath());
                    OutputStream os = new FileOutputStream("medias/" + this.newFileMedia.getName());
                    Files.copy(path, os);

                    if (this.newFileInterstim != null) {
                        if (this.newFileInterstim.isFile()) {
                            Path path2 = Paths.get(this.newFileInterstim.getPath());
                            OutputStream os2 = new FileOutputStream("medias/" + this.newFileInterstim.getName());
                            Files.copy(path2, os2);
                        }
                    }

                    String extension = "";
                    int i = this.newFileMedia.getPath().lastIndexOf('.');
                    if (i > 0) {
                        extension = this.newFileMedia.getPath().substring(i + 1);
                    }

                    TypeMedia typeMedia;
                    if (extension.equals("mp4")) {
                        typeMedia = TypeMedia.VIDEO;
                    } else {
                        typeMedia = TypeMedia.PICTURE;
                    }

                    Media newMedia = new Media(
                            this.nameNewMedia.getText(),
                            this.nameNewMedia.getText(),
                            Duration.ofSeconds(Integer.parseInt(this.durationField.getText())),
                            typeMedia,
                            null
                    );

                    if (this.newFileInterstim != null) {
                        Media newInterstim = new Media(
                                this.nameNewInterstim.getText(),
                                this.nameNewInterstim.getText(),
                                Duration.ofSeconds(1),
                                TypeMedia.PICTURE,
                                null
                        );

                        newMedia.setInterStim(newInterstim);
                    }

                    this.sequence.addMedia(newMedia);
                }
            } else {
                Media copiedMedia = new Media(this.nameListMedias.getSelectionModel().getSelectedItem());
                if (copiedMedia.getInterStim() != null) {
                    copiedMedia.setInterStim(new Media(copiedMedia.getInterStim()));
                }
                this.sequence.addMedia(copiedMedia);
            }

            this.listener.onModified(this.sequence);
            this.popUpStage.close();
        }
    }

    /**
     * Closes the popup.
     */
    private void cancelAddMedia() {
        this.popUpStage.close();
    }
}
