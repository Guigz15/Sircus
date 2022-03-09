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
 * Controller to add a sequence in a meta sequence
 */
public class addSeqPopUp {

	/**
	 * Text field of the name of the sequence to add
	 */
	@FXML
	private TextField nameNewSeq;
	/**
	 * Button to cancel the creation of the sequence
	 */
	@FXML
	private Button addSeqCancel;
	/**
	 * Button to validate the creation of the sequence
	 */
	@FXML
	private Button addSeqSave;
	/**
	 * Radio button selecting the creation of a new sequence
	 */
	@FXML
	private RadioButton addNewSeq;
	/**
	 * Radio button selecting the copy of a new sequence
	 */
	@FXML
	private RadioButton addCopySeq;
	/**
	 * ComboBox representing all the sequences
	 */
	@FXML
	private ComboBox nameListSeq;

	//manager of the meta sequence
	/**
	 * List of the sequences
	 */
	private ObservableList listSequences = null;
	/**
	 * Meta sequence where the sequence will be added
	 */
	private MetaSequence metaSequence = null;
	/**
	 * Pop up adding of sequence
	 */
	private Stage popUpStage = null;
	/**
	 * Listener of the adding of the sequence
	 */
	private MetaSequenceController.ModificationMetaSeqListener addListener = null;

	/**
	 * Constructor of the controller
	 * @param owner main frame
	 * @param listSequences list of the sequences
	 * @param metaSequence the meta sequence where we add the sequence
	 * @param addListener the listener of the adding event
	 */
	public addSeqPopUp (Window owner,
					   ObservableList<Sequence> listSequences,
                       MetaSequence metaSequence,
                       MetaSequenceController.ModificationMetaSeqListener addListener ) {

		FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_seq_popup.fxml" ));
		fxmlLoader.setController(this);

		try {
			this.metaSequence = metaSequence;
			this.listSequences = listSequences;
			this.addListener = addListener;

			Scene dialogScene = new Scene(fxmlLoader.load(),500,160);
			Stage dialog = new Stage ();

			this.popUpStage = dialog;

			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(owner);
			dialog.setScene(dialogScene);
			dialog.setResizable(true);
			dialog.setMinHeight(140); //110 (+30 for the height of the header and the frame on Windows)
			dialog.setMinWidth(320); //310 (+10 for the width of the frame on Windows)
			dialog.setTitle("Ajout Séquence à " + this.metaSequence.getName());
			dialog.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the controller and the attributes, add controllers to each component
	 */
	@FXML
	private void initialize() {
		this.nameListSeq.setItems(listSequences);
		this.nameListSeq.getSelectionModel().select(0);
		this.nameListSeq.setDisable(true);
		this.addSeqSave.setDisable (true);

		this.nameNewSeq.setOnKeyReleased(keyEvent->checkNameNewSeqFilled());
		this.addCopySeq.setOnMouseClicked(mouseEvent->selectAddCopySeq());
		this.addNewSeq.setOnMouseClicked(mouseEvent->selectAddNewSeq ());
		this.addSeqCancel.setOnMouseClicked(mouseEvent->cancelAddSeq());
		this.addSeqSave.setOnMouseClicked(mouseEvent->addSeqToMetaSeq());
	}

	/**
	 * Disable the button that create a sequence if the name of the new sequence is empty
	 */
	@FXML
	private void checkNameNewSeqFilled(){
		if(this.addNewSeq.isSelected()){
			if(this.nameNewSeq.getText().length() > 0){
				this.addSeqSave.setDisable(false);
			} else {
				this.addSeqSave.setDisable(true);
			}
		}
	}

	/**
	 * Disable the radio button for a copy sequence if the new sequence radio button is selected
	 */
	@FXML
	private void selectAddNewSeq() {
		if(addCopySeq.isSelected()){
			this.addCopySeq.fire();
		}
		this.nameListSeq.setDisable(true);

		if(this.nameNewSeq.getText().length() > 0){
			this.addSeqSave.setDisable(false);
		} else {
			this.addSeqSave.setDisable(true);
		}
	}

	/**
	 * Disable the radio button for a new sequence if the copy sequence radio button is selected
	 */
	@FXML
	private void selectAddCopySeq() {
		if(this.addNewSeq.isSelected()){
			this.addNewSeq.fire();
		}
		this.nameListSeq.setDisable(false);
		this.addSeqSave.setDisable(false);
	}

	/**
	 * Close the pop-up
	 */
	@FXML
	private void cancelAddSeq() {
		this.popUpStage.close ();
	}

	/**
	 * Create alert to confirm the adding of a sequence, add it if confirm
	 */
	@FXML
	private void addSeqToMetaSeq() {
		Alert alert = new Alert( Alert.AlertType.CONFIRMATION,
		                         "Etes-vous sûr de vouloir enregistrer les modifications de " + this.metaSequence.getName () + " ?",
		                         ButtonType.YES,
		                         ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			if(this.addNewSeq.isSelected()) {
				this.metaSequence.addSequence(new Sequence(this.nameNewSeq.getText()));
			} else {
				Sequence copiedSeq = new Sequence((Sequence)this.nameListSeq.getSelectionModel().getSelectedItem());
				this.metaSequence.addSequence(copiedSeq);
			}
			this.addListener.onModified(this.metaSequence);
			this.popUpStage.close();
		}
	}
}
