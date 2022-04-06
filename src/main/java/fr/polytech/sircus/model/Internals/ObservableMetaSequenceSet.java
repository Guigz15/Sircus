package fr.polytech.sircus.model.Internals;

import fr.polytech.sircus.model.MetaSequence;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Locale;

/**
 * List of the meta sequences
 */
public class ObservableMetaSequenceSet extends SimpleListProperty<MetaSequence> {

	/**
	 * Constructor
	 */
	public ObservableMetaSequenceSet() {
		super(FXCollections.observableArrayList());
	}

	/**
	 * Determine if a meta sequence of a certain name exist
	 * @param name Name of the meta sequence
	 * @return boolean
	 */
	public boolean findName (String name) {
		ObservableList<MetaSequence> metaSequences = super.get ();

		for(MetaSequence metaSequence: metaSequences) {
			if(metaSequence.getName().toUpperCase().equals(name.toUpperCase(Locale.ROOT))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a meta sequence in the list of meta sequences
	 * @param metaSequence A meta sequence
	 * @return boolean
	 */
	public boolean add(MetaSequence metaSequence) {
		if(!super.get().contains(metaSequence)) {
			return super.get().add(metaSequence);
		}
		return false;
	}

	/**
	 * Get the list of meta sequences
	 * @return the list of meta sequences
	 */
	public List<MetaSequence> getList() {
		return super.get();
	}
}
