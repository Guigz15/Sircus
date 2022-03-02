package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.MetaSequenceController;
import fr.polytech.sircus.model.Internals.ObservableMetaSequenceSet;
import fr.polytech.sircus.model.MetaSequence;
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

/**
 * This class allows us to modify a meta sequence
 */
public class ModifyMetaSeqPopUp {

    @FXML
    private TextField modifyMetaSeqName;

    @FXML
    private Button modifyMetaSeqDelete;

    @FXML
    private Button modifyMetaSeqCancel;

    @FXML
    private Button modifyMetaSeqSave;

    private ObservableMetaSequenceSet metaSequences = null;

    /**
     * Meta sequence to modify
     */
    private MetaSequence metaSequence = null;

    /**
     * Pop up to add a sequence
     */
    private Stage popUpStage = null;

    private MetaSequenceController.ModificationMetaSeqListener modificationMetaSeqListener = null;


    /**
     * Constructeur du controleur
     *
     * @param owner The main window
     * @param metaSequences The meta sequences list
     * @param metaSequence The meta sequence to modify
     * @param modificationMetaSeqListener The listener about the modification
     */
    public ModifyMetaSeqPopUp(Window owner,
                              final ObservableMetaSequenceSet metaSequences,
                              MetaSequence metaSequence,
                              MetaSequenceController.ModificationMetaSeqListener modificationMetaSeqListener) {
        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_meta_seq_popup.fxml"));
        fxmlLoader.setController(this);

        try {
            this.metaSequences = metaSequences;
            this.metaSequence = metaSequence;
            this.modificationMetaSeqListener = modificationMetaSeqListener;

            Scene dialogScene = new Scene(fxmlLoader.load(), 500, 100);
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(110); //80 (+30 hauteur de l'entête de la fenêtre sur windows)
            dialog.setMinWidth(440); //430 (+10 largeur de la fenêtre sur windows)
            dialog.setTitle("Modifications " + this.metaSequence.getName());

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initialize the controller, its attributes and bind a controller to each component
     */
    @FXML
    private void initialize() {
        this.modifyMetaSeqName.setText(this.metaSequence.getName());
        this.modifyMetaSeqSave.setDisable(true);

        this.modifyMetaSeqName.setOnKeyReleased(keyEvent -> checkMetaSeqName());
        this.modifyMetaSeqDelete.setOnMouseClicked(mouseEvent -> deleteMetaSeq());
        this.modifyMetaSeqCancel.setOnMouseClicked(mouseEvent -> cancelModificationsMetaSeq());
        this.modifyMetaSeqSave.setOnMouseClicked(mouseEvent -> saveModificationsMetaSeq());
    }

    /**
     * If the new name of meta sequence already exists, it disables the save button
     */
    @FXML
    private void checkMetaSeqName() {
        modifyMetaSeqSave.setDisable(metaSequences.findName(modifyMetaSeqName.getText()));
    }

    /**
     * Delete the meta sequence
     */
    @FXML
    private void deleteMetaSeq() {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Êtes-vous sûr de vouloir supprimer " + this.metaSequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            this.metaSequences.remove(this.metaSequence);
            this.popUpStage.close();
        }
    }

    /**
     * If you choose to cancel the modification, it closes the window
     */
    @FXML
    private void cancelModificationsMetaSeq() {
        this.popUpStage.close();
    }

    /**
     * Save the modification
     */
    @FXML
    private void saveModificationsMetaSeq() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.metaSequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            this.metaSequence.setName(this.modifyMetaSeqName.getText());
            this.modificationMetaSeqListener.onModified(this.metaSequence);
            this.popUpStage.close();
        }
    }
}
