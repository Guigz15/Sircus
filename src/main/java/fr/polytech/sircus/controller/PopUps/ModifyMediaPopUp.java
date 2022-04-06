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

    /**
     * Text field of the new name of the media
     */
    @FXML
    private TextField newMediaNameField;

    /**
     * Text field of the new duration of the media
     */
    @FXML
    private TextField newMediaDurationField;

    /**
     *  Button that deletes the media
     */
    @FXML
    private Button modifyMediaDelete;

    /**
     *  Button that cancels changes of the meta sequence
     */
    @FXML
    private Button modifyMediaCancel;

    /**
     *  Button that saves changes of the meta sequence
     */
    @FXML
    private Button modifyMediaSave;

    /**
     *  Sequence that contains the media
     */
    private Sequence sequence = null;

    /**
     *  Media that is to be modified
     */
    private Media media = null;

    /**
     *  Modification pop-up
     */
    private Stage popUpStage = null;

    /**
     *  Event listener that comes from the controller modifySeqPopUp
     */
    private ModifySeqPopUp.SequenceModificationListener listener1 = null;
    private ModifySeqPopUp.MediaModificationListener listener2 = null;


    /**
     * Builds controller of the media modification pop-up, with the components
     *
     * @param owner main window
     * @param sequence sequence that contains the media
     * @param media media that is to be modified
     * @param listener1 event listener that comes from the controller modifySeqPopUp
     * @param listener2 event listener that comes from the controller modifySeqPopUp
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

            dialog.initModality ( Modality.APPLICATION_MODAL );
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(140); //110 (+30 window header height on windows)
            dialog.setMinWidth(330); //320 (+10 window width on windows)
            dialog.setTitle("Modifier le média : " + this.media.getName());

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Initializes controller, adds controllers to each component
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
     *  Function that checks if a file is selected and if its duration is set
     *
     * if yes, turns on the save button
     * if no, turns off the save button
     */
    private void checkMediaNameAndDuration() {
        this.modifyMediaSave.setDisable(Objects.equals(this.newMediaNameField.getText(), this.media.getName())
                && Objects.equals(this.newMediaDurationField.getText(), this.media.getDuration().toString()));
    }

    /**
     *  Function that saves the modified media
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
     *  Function that deletes the media
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
     *  Function taht closes the media modification pop-up
     */
    private void cancelModificationsMedia() {
        this.popUpStage.close();
    }
}
