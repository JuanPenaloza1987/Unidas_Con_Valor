package com.creatio.imm.Objects;

/**
 * Created by gerardo on 19/06/18.
 */

public class OCategoryCupon {
    String ID,name,description,create_date,status,image;
    Boolean isSelect;

    public OCategoryCupon(String ID, String name, String description, String create_date, String status, String image, Boolean isSelect) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.create_date = create_date;
        this.status = status;
        this.image = image;
        this.isSelect = isSelect;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
