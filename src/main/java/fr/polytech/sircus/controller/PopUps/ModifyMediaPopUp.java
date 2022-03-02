package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class ModifyMediaPopUp {
    //******************************************************************************************************************
    // Composants UI
    //******************************************************************************************************************

    /**
     * Champ texte du nouveau nom du media
     */
    @FXML
    private TextField newMediaNameField;

    /**
     * Champ texte de la nouvelle durée
     */
    @FXML
    private TextField newMediaDurationField;

    /**
     * Bouton supprimant le media
     */
    @FXML
    private Button modifyMediaDelete;

    /**
     * Bouton annulant la modification de la meta sequence
     */
    @FXML
    private Button modifyMediaCancel;

    /**
     * Bouton sauvegardant la modification de la meta sequence
     */
    @FXML
    private Button modifyMediaSave;

    //******************************************************************************************************************

    //******************************************************************************************************************
    // Gestionnaires medias
    //******************************************************************************************************************

    /**
     * Sequence à laquelle appartient le media
     */
    private Sequence sequence = null;

    /**
     * Media à modifier
     */
    private Media media = null;

    /**
     * Pop up de modification
     */
    private Stage popUpStage = null;

    /**
     * Event listener provenant du controller modifySeqPopUp
     */
    private ModifySeqPopUp.SequenceModificationListener listener1 = null;
    private ModifySeqPopUp.MediaModificationListener listener2 = null;

    //******************************************************************************************************************

    //******************************************************************************************************************
    //   ###    ###   #   #   ####  #####  ####   #   #   ###   #####   ###   ####    ####
    //  #   #  #   #  ##  #  #        #    #   #  #   #  #   #    #    #   #  #   #  #
    //  #      #   #  # # #   ###     #    ####   #   #  #        #    #   #  ####    ###
    //  #   #  #   #  #  ##      #    #    #   #  #   #  #   #    #    #   #  #   #      #
    //   ###    ###   #   #  ####     #    #   #   ###    ###     #     ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Constructeur du contrôleur de la pop-up de modification d'un média à une séquence et de ses composantes
     *
     * @param owner     fenêtre principale
     * @param sequence  séquence dans laquelle se trouve le média
     * @param media     média à modifier
     * @param listener1 event listener provenant du controller modifySeqPopUp
     * @param listener2 event listener provenant du controller modifySeqPopUp
     */
    public ModifyMediaPopUp(Window owner, Sequence sequence, Media media,
                            ModifySeqPopUp.SequenceModificationListener listener1,
                            ModifySeqPopUp.MediaModificationListener listener2) {

        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_media_popup.fxml"));
        fxmlLoader.setController(this);

        try {
            this.media = media;
            this.sequence = sequence;
            this.listener1 = listener1;
            this.listener2 = listener2;

            Scene dialogScene = new Scene(fxmlLoader.load());
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(140); //110 (+30 hauteur de l'entête de la fenêtre sur windows)
            dialog.setMinWidth(330); //320 (+10 largeur de la fenêtre sur windows)
            dialog.setTitle("Modifier le média : " + this.media.getName());

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
     * Initialise le controleur et ses attributs, ajoute des controleurs a chaque composant
     */
    @FXML
    private void initialize() {
        this.newMediaNameField.setText(this.media.getName());
        this.newMediaDurationField.setText((Long.toString(this.media.getDuration().getSeconds())));

        this.modifyMediaSave.setDisable(true);

        this.newMediaNameField.setOnKeyReleased(keyEvent -> checkMediaNameAndDuration());
        this.newMediaDurationField.setOnKeyReleased(keyEvent -> checkMediaNameAndDuration());
        this.modifyMediaDelete.setOnMouseClicked(mouseEvent -> deleteMedia());
        this.modifyMediaCancel.setOnMouseClicked(mouseEvent -> cancelModificationsMedia());
        this.modifyMediaSave.setOnMouseClicked(mouseEvent -> saveModificationsMedia());
    }

    /**
     * Méthode qui vérifie si un fichier a été sélectionné et si une durée a été remplie
     * si oui active le bouton de sauvegarde
     * si non le désactive
     */
    private void checkMediaNameAndDuration() {
        this.modifyMediaSave.setDisable(Objects.equals(this.newMediaNameField.getText(), this.media.getName())
                && Objects.equals(this.newMediaDurationField.getText(), this.media.getDuration().toString()));
    }

    /**
     * Méthode permettant la sauvegarde du média modifié
     */
    private void saveModificationsMedia() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.media.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO
        );

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                this.media.setName(this.newMediaNameField.getText());
                this.media.setDuration(Duration.ofSeconds(Integer.parseInt(this.newMediaDurationField.getText())));
                this.listener2.onModified(this.media);
                this.popUpStage.close();
            } catch (Exception e) {
                this.newMediaDurationField.setText("Incorrect duration value");
            }
        }
    }

    /**
     * Méthode de suppression du média
     */
    private void deleteMedia() {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                "Etes-vous sûr de vouloir supprimer " + this.media.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO
        );

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            this.sequence.removeMedia(this.media);
            this.listener1.onModified(this.sequence);
            this.popUpStage.close();
        }
    }

    /**
     * Méthode de fermeture de la pop-up de modification du média
     */
    private void cancelModificationsMedia() {
        this.popUpStage.close();
    }
}
