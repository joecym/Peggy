package com.example.joseph.peggy;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by j.cymerman on 4/13/2017.
 */

public class Person extends RealmObject {


    private String FirstName;
    private String LastName;

    @PrimaryKey
    @Required
    private String Username;

    @Required
    private String Password;
    private String PictureURL;

    public Person(){
        // Nothing
    }

    public Person(String firstName, String lastName, String username, String password, String pictureURL) {
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Username = username;
        this.Password = password;
        this.PictureURL = pictureURL;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPicture() {
        return PictureURL;
    }

    public void setPicture(String pictureURL) {
        PictureURL = pictureURL;
    }
}
