package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represent an user
 */
public class User {

    enum TypeUser { Praticien, Experimentateur }

    @Getter @Setter
    private TypeUser typeUser;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String firstName;

    public User() {

    }

    public User(User user) {
        this.typeUser = user.typeUser;
        this.lastName = user.lastName;
        this.firstName = user.firstName;
    }
}
