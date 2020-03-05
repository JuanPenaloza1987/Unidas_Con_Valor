package com.creatio.imm.Objects;

/**
 * Created by gerardo on 20/06/18.
 */

public class OCitys {
    String ID;
    String city;
    String state;

    public OCitys(String ID, String city, String state) {
        this.ID = ID;
        this.city = city;
        this.state = state;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
