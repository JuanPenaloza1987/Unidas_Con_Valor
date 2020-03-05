package com.creatio.imm.Objects;

/**
 * Created by gerardo on 19/06/18.
 */

public class OCupons {
    String ID, name, description, create_date, status, quantity, type, category, image, price, discount, isFavorite, isReserved, s_vencido;

    public OCupons(String ID, String name, String description, String create_date, String status, String quantity, String type, String category, String image, String price, String discount, String isFavorite, String isReserved, String s_vencido) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.create_date = create_date;
        this.status = status;
        this.quantity = quantity;
        this.type = type;
        this.category = category;
        this.image = image;
        this.price = price;
        this.discount = discount;
        this.isFavorite = isFavorite;
        this.isReserved = isReserved;
        this.s_vencido = s_vencido;
    }

    public String getS_vencido() {
        return s_vencido;
    }

    public void setS_vencido(String s_vencido) {
        this.s_vencido = s_vencido;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getIsReserved() {
        return isReserved;
    }

    public void setIsReserved(String isReserved) {
        this.isReserved = isReserved;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
