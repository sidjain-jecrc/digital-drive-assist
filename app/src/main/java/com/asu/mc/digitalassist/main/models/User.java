package com.asu.mc.digitalassist.main.models;

/**
 * Created by anurag on 4/26/17.
 */

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private Long zip;
    private String provider;

    public User(String firstName, String lastName, String email, Long zip, String provider) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.zip = zip;
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getZip() {
        return zip;
    }

    public void setZip(Long zip) {
        this.zip = zip;
    }
}