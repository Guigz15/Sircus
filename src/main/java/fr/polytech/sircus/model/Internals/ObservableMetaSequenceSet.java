package fr.polytech.sircus.model.Internals;

import fr.polytech.sircus.model.MetaSequence;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Locale;

public class ObservableMetaSequenceSet extends SimpleListProperty<MetaSequence> {

    public ObservableMetaSequenceSet() {
        super(FXCollections.observableArrayList());
    }

    public boolean findName(String name) {
        ObservableList<MetaSequence> metaSequences = super.get();

        for (MetaSequence metaSequence : metaSequences) {
            if (metaSequence.getName().toUpperCase().equals(name.toUpperCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    public boolean add(MetaSequence metaSequence) {
        if (!super.get().contains(metaSequence)) {
            return super.get().add(metaSequence);
        }
        return false;
    }

    public List<MetaSequence> getList() {
        return super.get();
    }
}
