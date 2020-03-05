package com.creatio.imm.Objects;

/**
 * Created by Layge on 22/12/2017.
 */

public class OCar {
    String ID,ID_menu,name_menu,price_menu,quantity,total,ID_car,image,ID_cupon;

    public OCar(String ID, String ID_menu, String name_menu, String price_menu, String quantity, String total, String ID_car, String image, String ID_cupon) {
        this.ID = ID;
        this.ID_menu = ID_menu;
        this.name_menu = name_menu;
        this.price_menu = price_menu;
        this.quantity = quantity;
        this.total = total;
        this.ID_car = ID_car;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getID_menu() {
        return ID_menu;
    }

    public void setID_menu(String ID_menu) {
        this.ID_menu = ID_menu;
    }

    public String getName_menu() {
        return name_menu;
    }

    public void setName_menu(String name_menu) {
        this.name_menu = name_menu;
    }

    public String getPrice_menu() {
        return price_menu;
    }

    public void setPrice_menu(String price_menu) {
        this.price_menu = price_menu;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getID_car() {
        return ID_car;
    }

    public void setID_car(String ID_car) {
        this.ID_car = ID_car;
    }
}
