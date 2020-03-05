package com.creatio.imm.Objects;

/**
 * Created by gerardo on 29/08/18.
 */

public class OMenu {

    String ID,name,price,ID_categorie,ID_branch,image,description,is_offer;

    public OMenu(String ID, String name, String price, String ID_categorie, String ID_branch, String image, String description, String is_offer) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.ID_categorie = ID_categorie;
        this.ID_branch = ID_branch;
        this.image = image;
        this.description = description;
        this.is_offer = is_offer;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getID_categorie() {
        return ID_categorie;
    }

    public void setID_categorie(String ID_categorie) {
        this.ID_categorie = ID_categorie;
    }

    public String getID_branch() {
        return ID_branch;
    }

    public void setID_branch(String ID_branch) {
        this.ID_branch = ID_branch;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIs_offer() {
        return is_offer;
    }

    public void setIs_offer(String is_offer) {
        this.is_offer = is_offer;
    }
}
