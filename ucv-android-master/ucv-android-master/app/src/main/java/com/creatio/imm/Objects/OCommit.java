package com.creatio.imm.Objects;

/**
 * Created by Layge on 22/12/2017.
 */

public class OCommit {
    String ID,commit,rate,name_user,image_user,create_date,ID_menu;

    public OCommit(String ID, String commit, String rate, String name_user, String image_user, String create_date, String ID_menu) {
        this.ID = ID;
        this.commit = commit;
        this.rate = rate;
        this.name_user = name_user;
        this.image_user = image_user;
        this.create_date = create_date;
        this.ID_menu = ID_menu;
    }

    public String getImage_user() {
        return image_user;
    }

    public void setImage_user(String image_user) {
        this.image_user = image_user;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getID_menu() {
        return ID_menu;
    }

    public void setID_menu(String ID_menu) {
        this.ID_menu = ID_menu;
    }
}
