package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.MetaSequenceController;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Controleur permettant la gestion d'ajout d'une sequence a une meta sequence
 */
public class addSeqPopUp {
    //******************************************************************************************************************
    // Composants UI
    //******************************************************************************************************************

    /**
     * Champ texte du nom de la sequence a ajouter
     */
    @FXML
    private TextField nameNewSeq;

    /**
     * Bouton annulant la creation de la sequence
     */
    @FXML
    private Button addSeqCancel;

    /**
     * Bouton validant la creation de la sequence
     */
    @FXML
    private Button addSeqSave;

    /**
     * Bouton radio selectionnant la creation d'une nouvelle sequence
     */
    @FXML
    private RadioButton addNewSeq;

    /**
     * Bouton radio selectionnant la copie d'une nouvelle sequence
     */
    @FXML
    private RadioButton addCopySeq;

    /**
     * ComboBox representant l'ensemble des sequences
     */
    @FXML
    private ComboBox nameListSeq;
    //******************************************************************************************************************

    //******************************************************************************************************************
    // Gestionnaires méta-sequences
    //******************************************************************************************************************

    /**
     * List des sequences
     */
    private ObservableList listSequences = null;

    /**
     * Meta sequence a laquelle sera ajoutee la sequence
     */
    private MetaSequence metaSequence = null;

    /**
     * Pop up ajout de sequence
     */
    private Stage popUpStage = null;

    /**
     * Listener de l'ajout de la sequence
     */
    private MetaSequenceController.ModificationMetaSeqListener addListener = null;
    //******************************************************************************************************************

    //******************************************************************************************************************
    //   ###    ###   #   #   ####  #####  ####   #   #   ###   #####   ###   ####    ####
    //  #   #  #   #  ##  #  #        #    #   #  #   #  #   #    #    #   #  #   #  #
    //  #      #   #  # # #   ###     #    ####   #   #  #        #    #   #  ####    ###
    //  #   #  #   #  #  ##      #    #    #   #  #   #  #   #    #    #   #  #   #      #
    //   ###    ###   #   #  ####     #    #   #   ###    ###     #     ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Constructeur du controleur
     *
     * @param owner         la fenetre principale
     * @param listSequences la liste des sequences
     * @param metaSequence  la meta sequence a laquelle on ajoute la sequence
     * @param addListener   le listener de l'evenement d'ajout
     */
    public addSeqPopUp(Window owner,
                       ObservableList<Sequence> listSequences,
                       MetaSequence metaSequence,
                       MetaSequenceController.ModificationMetaSeqListener addListener) {

        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_seq_popup.fxml"));
        fxmlLoader.setController(this);

        try {
            this.metaSequence = metaSequence;
            this.listSequences = listSequences;
            this.addListener = addListener;

            Scene dialogScene = new Scene(fxmlLoader.load(), 500, 160);
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(140); //110 (+30 hauteur de l'entête de la fenêtre sur windows)
            dialog.setMinWidth(320); //310 (+10 largeur de la fenêtre sur windows)
            dialog.setTitle("Ajout Séquence à " + this.metaSequence.getName());

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
        this.nameListSeq.setItems(listSequences);
        this.nameListSeq.getSelectionModel().select(0);
        this.nameListSeq.setDisable(true);
        this.addSeqSave.setDisable(true);

        this.nameNewSeq.setOnKeyReleased(keyEvent -> checkNameNewSeqFilled());
        this.addCopySeq.setOnMouseClicked(mouseEvent -> selectAddCopySeq());
        this.addNewSeq.setOnMouseClicked(mouseEvent -> selectAddNewSeq());
        this.addSeqCancel.setOnMouseClicked(mouseEvent -> cancelAddSeq());
        this.addSeqSave.setOnMouseClicked(mouseEvent -> addSeqToMetaSeq());
    }

    /**
     * Desactive le bouton qui cree une sequence si l'utilisateur veut creer une nouvelle sequence
     * et que le nom de la nouvelle sequence est vide
     */
    @FXML
    private void checkNameNewSeqFilled() {
        if (this.addNewSeq.isSelected()) {
            this.addSeqSave.setDisable(this.nameNewSeq.getText().length() <= 0);
        }
    }

    /**
     * Desactive l'autre bouton radio si un des deux est selectionne
     */
    @FXML
    private void selectAddNewSeq() {
        if (addCopySeq.isSelected()) {
            this.addCopySeq.fire();
        }
        this.nameListSeq.setDisable(true);

        this.addSeqSave.setDisable(this.nameNewSeq.getText().length() <= 0);
    }

    /**
     * Desactive l'autre bouton radio si un des deux est selectionne
     */
    @FXML
    private void selectAddCopySeq() {
        if (this.addNewSeq.isSelected()) {
            this.addNewSeq.fire();
        }
        this.nameListSeq.setDisable(false);
        this.addSeqSave.setDisable(false);
    }

    /**
     * Ferme la pop up si le bouton annuler est cliqué
     */
    @FXML
    private void cancelAddSeq() {
        this.popUpStage.close();
    }

    /**
     * Ajoute la sequence a la meta sequence si le bouton creer est cliqué
     */
    @FXML
    private void addSeqToMetaSeq() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.metaSequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            if (this.addNewSeq.isSelected()) {
                this.metaSequence.addSequence(new Sequence(this.nameNewSeq.getText()));
            } else {
                Sequence copiedSeq = new Sequence((Sequence) this.nameListSeq.getSelectionModel().getSelectedItem());
                this.metaSequence.addSequence(copiedSeq);
            }
            this.addListener.onModified(this.metaSequence);
            this.popUpStage.close();
        }
    }

}
