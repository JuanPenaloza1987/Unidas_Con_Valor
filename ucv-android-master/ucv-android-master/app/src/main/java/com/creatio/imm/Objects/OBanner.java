package com.creatio.imm.Objects;

/**
 * Created by gerardo on 20/06/18.
 */

public class OBanner {
    String ID;
    String name;
    String image;
    String icon;
    String action;
    String params;
    String type;
    String description;
    String button_text;
    String show_button;


    public OBanner(String ID, String name, String image, String icon, String action, String params, String type, String description, String button_text, String show_button) {
        this.ID = ID;
        this.name = name;
        this.image = image;
        this.icon = icon;
        this.action = action;
        this.params = params;
        this.type = type;
        this.description = description;
        this.button_text = button_text;
        this.show_button = show_button;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getButton_text() {
        return button_text;
    }

    public void setButton_text(String button_text) {
        this.button_text = button_text;
    }

    public String getShow_button() {
        return show_button;
    }

    public void setShow_button(String show_button) {
        this.show_button = show_button;
    }
}
