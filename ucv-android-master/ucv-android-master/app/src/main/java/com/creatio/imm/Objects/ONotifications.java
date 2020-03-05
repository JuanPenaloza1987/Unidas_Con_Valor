package com.creatio.imm.Objects;

/**
 * Created by gerardo on 6/07/18.
 */

public class ONotifications {
    String ID,name,description,date,image,ID_cupon;

    public ONotifications(String ID, String name, String description, String date, String image, String ID_cupon) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.date = date;
        this.image = image;
        this.ID_cupon = ID_cupon;
    }

    public String getID_cupon() {
        return ID_cupon;
    }

    public void setID_cupon(String ID_cupon) {
        this.ID_cupon = ID_cupon;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
