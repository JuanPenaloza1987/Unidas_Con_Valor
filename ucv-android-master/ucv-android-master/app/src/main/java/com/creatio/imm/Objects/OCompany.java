package com.creatio.imm.Objects;

/**
 * Created by gerardo on 20/06/18.
 */

public class OCompany {
    String ID,name,description,create_date,image;

    public OCompany(String ID, String name, String description, String create_date, String image) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.create_date = create_date;
        this.image = image;
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

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
