package com.creatio.imm.Objects;

/**
 * Created by gerardo on 19/06/18.
 */

public class OMyCupons {
    String ID;
    String name;
    String description;
    String create_date;
    String status;
    String quantity;
    String type;
    String category;
    String image;
    String price;
    String discount;
    String name_branch;
    String name_business;
    String date_reserved;
    String count_days;
    String ID_r;
    String is_cuponcode;

    public OMyCupons(String ID, String name, String description, String create_date, String status, String quantity, String type, String category, String image, String price, String discount, String name_branch, String name_business, String date_reserved, String count_days, String ID_r, String is_cuponcode) {
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
        this.name_branch = name_branch;
        this.name_business = name_business;
        this.date_reserved = date_reserved;
        this.count_days = count_days;
        this.ID_r = ID_r;
        this.is_cuponcode = is_cuponcode;
    }

    public String getIs_cuponcode() {
        return is_cuponcode;
    }

    public void setIs_cuponcode(String is_cuponcode) {
        this.is_cuponcode = is_cuponcode;
    }

    public String getID_r() {
        return ID_r;
    }

    public void setID_r(String ID_r) {
        this.ID_r = ID_r;
    }

    public String getCount_days() {
        return count_days;
    }

    public void setCount_days(String count_days) {
        this.count_days = count_days;
    }

    public String getName_branch() {
        return name_branch;
    }

    public void setName_branch(String name_branch) {
        this.name_branch = name_branch;
    }

    public String getName_business() {
        return name_business;
    }

    public void setName_business(String name_business) {
        this.name_business = name_business;
    }

    public String getDate_reserved() {
        return date_reserved;
    }

    public void setDate_reserved(String date_reserved) {
        this.date_reserved = date_reserved;
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
