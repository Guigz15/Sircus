package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.Sequence;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Class create to make a list sequence. Is use for create a custom Cell of ListView. Cf Etape2AdminController.
 */
public class ItemSequence {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty on = new SimpleBooleanProperty();


    @Getter @Setter
    private final Sequence sequence;

    public ItemSequence(String name, boolean on, Sequence sequence) {
        this.sequence = sequence;
        setName(name);
        setOn(on);
    }

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public final BooleanProperty onProperty() {
        return this.on;
    }

    public final boolean isOn() {
        return this.onProperty().get();
    }

    public final void setOn(final boolean on) {
        this.onProperty().set(on);
    }

    @Override
    public String toString() {
        return getName();
    }

}