package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represent a user
 */
public class User {
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;

    public User() {

    }

    public User(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
    }

    public String toXML() {
        return "<user firstName=\"" + this.firstName + "\" lastName=\"" + this.lastName + "\" />\n";
    }
}
