package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represent a user
 */
public class User {

    public enum UserType { Praticien, Expérimentateur }

    @Getter @Setter
    private UserType userType;
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;

    public User() {

    }

    public User(User user) {
        this.userType = user.userType;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
    }
}
