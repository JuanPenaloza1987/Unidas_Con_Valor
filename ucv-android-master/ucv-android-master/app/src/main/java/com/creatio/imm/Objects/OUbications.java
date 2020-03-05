package com.creatio.imm.Objects;

/**
 * Created by gerardo on 26/06/18.
 */

public class OUbications {
    String ID,name;
    boolean selected;

    public OUbications(String ID, String name, boolean selected) {
        this.ID = ID;
        this.name = name;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
}
