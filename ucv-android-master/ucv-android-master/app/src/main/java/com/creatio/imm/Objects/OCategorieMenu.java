package com.creatio.imm.Objects;

import java.util.ArrayList;

/**
 * Created by gerardo on 29/08/18.
 */

public class OCategorieMenu {
    String name,description,ID_business,image;
    ArrayList<OMenu> dataMenu;

    public OCategorieMenu(String name, String description, String ID_business, String image, ArrayList<OMenu> dataMenu) {
        this.name = name;
        this.description = description;
        this.ID_business = ID_business;
        this.image = image;
        this.dataMenu = dataMenu;
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

    public String getID_business() {
        return ID_business;
    }

    public void setID_business(String ID_business) {
        this.ID_business = ID_business;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<OMenu> getDataMenu() {
        return dataMenu;
    }

    public void setDataMenu(ArrayList<OMenu> dataMenu) {
        this.dataMenu = dataMenu;
    }
}
