package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Method implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String name;

    /**
     * The default constructor
     */
    public Method() {

    }

    /**
     * The constructor
     *
     * @param name Method's name
     */
    public Method(String name) {
        this.name = name;
    }
}
