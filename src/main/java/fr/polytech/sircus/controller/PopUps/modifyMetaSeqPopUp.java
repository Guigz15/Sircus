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
 * Controleur permettant la gestion de modification d'une meta sequence
 */
public class modifyMetaSeqPopUp
	{
	//******************************************************************************************************************
	// Composants UI
	//******************************************************************************************************************

	/**
	 * Champ texte du nouveau nom de la meta sequence
	 */
	@FXML private TextField modifyMetaSeqName;

	/**
	 * Bouton supprimant la meta sequence
	 */
	@FXML private Button    modifyMetaSeqDelete;

	/**
	 * Bouton annulant la modification de la meta sequence
	 */
	@FXML private Button    modifyMetaSeqCancel;

	/**
	 * Bouton sauvegardant la modification de la meta sequence
	 */
	@FXML private Button    modifyMetaSeqSave;
	//******************************************************************************************************************

	//******************************************************************************************************************
	// Gestionnaires méta-sequences
	//******************************************************************************************************************
	/**
	 * List des meta sequences
	 */
	private ObservableMetaSequenceSet metaSequences = null;

	/**
	 * Meta sequence a modifier
	 */
	private MetaSequence metaSequence = null;

	/**
	 * Pop up ajout de sequence
	 */
	private Stage popUpStage = null;

	/**
	 * Listener de la modification de la meta sequence
	 */
	private MetaSequenceController.ModificationMetaSeqListener modificationMetaSeqListener = null;

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
	 * @param owner la fenetre principale
	 * @param metaSequences la liste des meta sequences
	 * @param metaSequence la meta sequence a modifier
	 * @param modificationMetaSeqListener le listener de l'evenement de modification
	 */
	public modifyMetaSeqPopUp ( Window owner,
	                            final ObservableMetaSequenceSet metaSequences,
	                            MetaSequence metaSequence,
	                            MetaSequenceController.ModificationMetaSeqListener modificationMetaSeqListener )
		{
		FXMLLoader fxmlLoader = new FXMLLoader ( SircusApplication.class.getClassLoader().getResource ( "views/popups/modify_meta_seq_popup.fxml" ) );
		fxmlLoader.setController ( this );

		try
			{
			this.metaSequences           = metaSequences;
			this.metaSequence                = metaSequence;
			this.modificationMetaSeqListener = modificationMetaSeqListener;

			Scene dialogScene  = new Scene ( fxmlLoader.load (), 500, 100 );
			Stage dialog       = new Stage ();

			this.popUpStage = dialog;

			dialog.initModality ( Modality.APPLICATION_MODAL                 );
			dialog.initOwner    ( owner                                      );
			dialog.setScene     ( dialogScene                                );
			dialog.setResizable ( true                                      );
			dialog.setMinHeight(110); //80 (+30 hauteur de l'entête de la fenêtre sur windows)
			dialog.setMinWidth(440); //430 (+10 largeur de la fenêtre sur windows)
			dialog.setTitle     ( "Modifications " + this.metaSequence.getName () );

			dialog.show();
			}
		catch ( IOException e )
			{
			e.printStackTrace ();
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
	@FXML private void initialize () {
		this.modifyMetaSeqName.setText    ( this.metaSequence.getName () );
		this.modifyMetaSeqSave.setDisable ( true                         );

		this.modifyMetaSeqName  .setOnKeyReleased  ( keyEvent   -> checkMetaSeqName           () );
		this.modifyMetaSeqDelete.setOnMouseClicked ( mouseEvent -> deleteMetaSeq              () );
		this.modifyMetaSeqCancel.setOnMouseClicked ( mouseEvent -> cancelModificationsMetaSeq () );
		this.modifyMetaSeqSave  .setOnMouseClicked ( mouseEvent -> saveModificationsMetaSeq   () );
	}

	/**
	 * Desactive le bouton modifiant la meta sequence si le nouveau nom existe deja pour une meta sequence
	 */
	@FXML private void checkMetaSeqName ()
		{
		modifyMetaSeqSave.setDisable ( metaSequences.findName ( modifyMetaSeqName.getText () ) );
		}

	/**
	 * Supprime la meta sequence
	 */
	@FXML private void deleteMetaSeq() {
		Alert alert = new Alert( Alert.AlertType.WARNING,
		                         "Etes-vous sûr de vouloir supprimer " + this.metaSequence.getName () + " ?",
		                         ButtonType.YES,
		                         ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES)
			{
			this.metaSequences.remove ( this.metaSequence );
			this.popUpStage.close ();
			}
	}

	/**
	 * Ferme la pop up de modification de la meta sequence
	 */
	@FXML private void cancelModificationsMetaSeq()
		{
		this.popUpStage.close ();
		}

	/**
	 * Modifie le nom de la meta sequence par son nouveau nom et ferme la pop up
	 */
	@FXML private void saveModificationsMetaSeq() {

		Alert alert = new Alert( Alert.AlertType.CONFIRMATION,
		                         "Etes-vous sûr de vouloir enregistrer les modifications de " + this.metaSequence.getName () + " ?",
		                         ButtonType.YES,
		                         ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES)
			{
			this.metaSequence.setName ( this.modifyMetaSeqName.getText () );
			this.modificationMetaSeqListener.onModified ( this.metaSequence );
			this.popUpStage.close ();
			}
	}
}
