package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private long postCode;

    @Getter @Setter
    private String street;

    @Getter @Setter
    private int streetNumber;

    /**
     * The default constructor
     */
    public Location() {

    }

    /**
     * The constructor
     *
     * @param country Country's name
     * @param city City's name
     * @param postCode PostCode number
     * @param street Street's name
     * @param streetNumber Number in the street
     */
    public Location(String country, String city, long postCode, String street, int streetNumber) {
        this.country = country;
        this.city = city;
        this.postCode = postCode;
        this.street = street;
        this.streetNumber = streetNumber;
    }
}
