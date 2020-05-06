package com.upgrad.FoodOrderingApp.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

public class CustomerLoginRseponse {
    @JsonProperty("id")
    private int id = 0;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("first_name")
    private String firstName = null;

    @JsonProperty("last_name")
    private String lastName = null;

    @JsonProperty("email_address")
    private String emailAddress = null;

    @JsonProperty("contact_number")
    private String contactNumber = null;

    private String access_token=null;

    private String UUID=null;


    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }
}
