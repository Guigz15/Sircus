package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
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

    //******************************************************************************************************************
    // Composants UI
    //******************************************************************************************************************

    /**
     * Label du nom du média à ajouter
     */
    @FXML
    private Label nameNewMedia;

    /**
     * Champ de texte de la durée du média à ajouter
     */
    @FXML
    private TextField durationField;

    /**
     * Fichier pour le média à ajouter
     */
    @FXML
    private File newFileMedia;

    /**
     * Champ texte du nom de l'inter-stimulation à ajouter
     */
    @FXML
    private Label nameNewInterstim;

    /**
     * Fichier pour l'inter-stimulation du média à ajouter
     */
    @FXML
    private File newFileInterstim;

    /**
     * Bouton annulant l'ajout du média
     */
    @FXML
    private Button addMediaCancel;

    /**
     * Bouton validant l'ajout du média
     */
    @FXML
    private Button addMediaSave;

    /**
     * Bouton d'ajout d'un fichier pour le média à ajouter
     */
    @FXML
    private Button addMediaFile;

    /**
     * Objet du bouton d'ajout de fichier : permet la sélection du fichier pour le média à ajouter
     */
    @FXML
    private FileChooser fileChooserMedia;

    /**
     * Bouton d'ajout d'un fichier pour l'inter-stimulation du média à ajouter
     */
    @FXML
    private Button addInterstimFile;

    /**
     * Objet du bouton d'ajout fichier : permet la sélection du fichier pour l'inter-stimulation du média à ajouter
     */
    @FXML
    private FileChooser fileChooserInterstim;

    /**
     * Bouton radio sélectionnant l'ajout d'un nouveau média
     */
    @FXML
    private RadioButton addNewMedia;

    /**
     * Bouton radio sélectionnant la copie d'un média existant
     */
    @FXML
    private RadioButton addCopyMedia;

    /**
     * ComboBox representant l'ensemble des médias
     */
    @FXML
    private ComboBox<Media> nameListMedias;


    //******************************************************************************************************************
    // Gestionnaires sequences
    //******************************************************************************************************************

    /**
     * Liste des médias
     */
    private ObservableList<Media> listMedias = null;

    /**
     * Séquence à laquelle sera ajouté le média
     */
    private Sequence sequence = null;

    /**
     * Pop-up ajout de média
     */
    private Stage popUpStage = null;

    /**
     * Event listener de modification de la séquence provenant du controller modifySeqPopUp
     */
    private modifySeqPopUp.SequenceModificationListener listener = null;


    //******************************************************************************************************************
    //   ###    ###   #   #   ####  #####  ####   #   #   ###   #####   ###   ####    ####
    //  #   #  #   #  ##  #  #        #    #   #  #   #  #   #    #    #   #  #   #  #
    //  #      #   #  # # #   ###     #    ####   #   #  #        #    #   #  ####    ###
    //  #   #  #   #  #  ##      #    #    #   #  #   #  #   #    #    #   #  #   #      #
    //   ###    ###   #   #  ####     #    #   #   ###    ###     #     ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Constructeur du contrôleur de la pop-up d'ajout d'un média à une séquence et de ses composantes
     * @param owner fenêtre principale
     * @param listMedias liste des médias de la séquence
     * @param sequence la séquence dans laquelle on ajoute un média
     * @param listener event listener de modification de la séquence provenant du controller modifySeqPopUp
     */
    public addMediaPopUp(Window owner, ObservableList<Media> listMedias, Sequence sequence,
                         modifySeqPopUp.SequenceModificationListener listener, FileChooser fileChooserMedia,
                         FileChooser fileChooserInterstim) {

        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_media_popup.fxml"));
        fxmlLoader.setController(this);

        this.fileChooserMedia = fileChooserMedia;
        this.fileChooserInterstim = fileChooserInterstim;

        try {
            this.sequence   = sequence;
            this.listMedias = listMedias;
            this.listener = listener;

            this.fileChooserMedia.setTitle("Open file (media)");
            this.fileChooserMedia.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image and video Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.mp4")
            );

            this.fileChooserInterstim.setTitle("Open file (interstim)");
            this.fileChooserInterstim.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            Scene dialogScene = new Scene(fxmlLoader.load());
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(200); //170 (+30 hauteur de l'entête de la fenêtre sur windows)
            dialog.setMinWidth(290); //280 (+10 largeur de la fenêtre sur windows)
            dialog.setTitle("Ajout Media à " + this.sequence.getName());

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //******************************************************************************************************************
    //      #  #####  #   #         #####  #   #  #   #   ###   #####  #   ###   #   #   ####
    //      #  #       # #          #      #   #  ##  #  #   #    #    #  #   #  ##  #  #
    //      #  ###      #           ###    #   #  # # #  #        #    #  #   #  # # #   ###
    //  #   #  #       # #          #      #   #  #  ##  #   #    #    #  #   #  #  ##      #
    //   ###   #      #   #         #       ###   #   #   ###     #    #   ###   #   #  ####
    //******************************************************************************************************************


    /**
     * Méthode d'initialisation du controleur et de ses attributs puis ajoute des fonctionnalités à chaque composant
     */
    @FXML
    private void initialize() {
        this.nameListMedias.setItems(this.listMedias);
        this.nameListMedias.getSelectionModel().select (0);
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
        this.addMediaCancel.setOnMouseClicked (mouseEvent -> cancelAddMedia());
        this.addMediaSave.setOnMouseClicked (mouseEvent -> {
            try {
                addMediaToSeq();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Méthode permettant la sélection d'un fichier pour l'inter-stimualtion du média dans l'ordinateur de l'utilisateur
     */
    private void selectInterstimFile() {
        this.newFileInterstim = this.fileChooserInterstim.showOpenDialog(this.popUpStage);

        try {
            if (this.newFileInterstim.isFile()) {
                this.nameNewInterstim.setText(this.newFileInterstim.getName());
            }
        } catch (Exception e) {
            System.out.print("Aucun média sélectionné.");
        }
    }

    /**
     * Méthode permettant la sélection d'un fichier pour le média dans l'ordinateur de l'utilisateur
     */
    private void selectMediaFile() {
        this.newFileMedia = this.fileChooserMedia.showOpenDialog(this.popUpStage);

        try {
            if (this.newFileMedia.isFile()) {
                this.nameNewMedia.setText(this.newFileMedia.getName());
                checkNameNewMediaFilled();
            }
        } catch (Exception e) {
            System.out.print("Aucun média sélectionné.");
        }
    }

    /**
     * Méthode qui vérifie si un fichier a été sélectionné et si une durée a été remplie
     * si oui active le bouton de sauvegarde
     * si non le désactive
     */
    private void checkNameNewMediaFilled() {
        if(this.addNewMedia.isSelected()) {
            this.addMediaSave.setDisable(
                    this.nameNewMedia.getText().length() <= 0 || this.durationField.getText().length() <= 0
            );
        }
    }

    /**
     * Méthode gérant l'évenement de sélection de la checkbox de sélection d'un nouveau média
     */
    private void selectAddNewMedia() {
        if(addCopyMedia.isSelected()) {
            this.addCopyMedia.fire();
        }
        this.nameListMedias.setDisable(true);
        this.addMediaFile.setDisable(false);
        this.addInterstimFile.setDisable(false);
        this.durationField.setDisable(false);

        checkNameNewMediaFilled();
    }

    /**
     * Méthode gérant l'évenement de sélection de la checkbox de sélection de la copie d'un média
     */
    private void selectAddCopyMedia() {
        if(this.addNewMedia.isSelected()) {
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
     * Méthode d'ajout d'un média à la séquence
     * @throws IOException si problème lié à la sélection d'un fichier
     */
    private void addMediaToSeq() throws IOException {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.sequence.getName () + " ?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES)
        {
            if (this.addNewMedia.isSelected()) {
                if (this.newFileMedia.exists()) {

                    Path path = Paths.get(this.newFileMedia.getPath());
                    OutputStream os = new FileOutputStream("medias/" + this.newFileMedia.getName());
                    Files.copy(path,os);

                    if (this.newFileInterstim != null) {
                        if (this.newFileInterstim.isFile()) {
                            Path path2 = Paths.get(this.newFileInterstim.getPath());
                            OutputStream os2 = new FileOutputStream("medias/" + this.newFileInterstim.getName());
                            Files.copy(path2,os2);
                        }
                    }

                    String extension = "";
                    int i = this.newFileMedia.getPath().lastIndexOf('.');
                    if (i > 0) {
                        extension = this.newFileMedia.getPath().substring(i+1);
                    }

                    TypeMedia typeMedia;
                    if (extension.equals("mp4")) {
                        typeMedia = TypeMedia.VIDEO;
                    }
                    else {
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
            }
            else {
                Media copiedMedia = new Media((Media) this.nameListMedias.getSelectionModel().getSelectedItem());
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
     * Méthode de fermeture de la pop-up
     */
    private void cancelAddMedia() {
        this.popUpStage.close();
    }
}
