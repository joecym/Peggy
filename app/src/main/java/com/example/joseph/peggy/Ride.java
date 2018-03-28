package com.example.joseph.peggy;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by j.cymerman on 4/14/2017.
 */

public class Ride extends RealmObject {

    @Required
    private String location;

    @PrimaryKey
    @Required
    private String date;
    private String pic_URL;

    public String getPic_URL() {
        return pic_URL;
    }

    public void setPic_URL(String pic_URL) {
        this.pic_URL = pic_URL;
    }

    public Ride(){
        // Nothing
    }

    public Ride(String location, String date, String pic_URL) {
        this.location = location;
        this.date = date;
        this.pic_URL = pic_URL;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

/*    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }*/

}
