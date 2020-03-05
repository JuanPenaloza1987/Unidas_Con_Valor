package com.creatio.imm.Objects;

/**
 * Created by Layge on 22/12/2017.
 */

public class OPEdidos {
    String ID, create_date, status, location, total, type, is_dom, location_branch, name_branch;
    String horarev, persons, date_rev, area, has_rev, ID_rev, name_area;



    public OPEdidos(String ID, String create_date, String status, String location, String total, String type, String is_dom, String location_branch, String name_branch, String horarev, String persons, String date_rev, String area, String has_rev, String ID_rev, String name_area) {
        this.ID = ID;
        this.create_date = create_date;
        this.status = status;
        this.location = location;
        this.total = total;
        this.type = type;
        this.is_dom = is_dom;
        this.location_branch = location_branch;
        this.name_branch = name_branch;
        this.horarev = horarev;
        this.persons = persons;
        this.date_rev = date_rev;
        this.area = area;
        this.has_rev = has_rev;
        this.ID_rev = ID_rev;
        this.name_area = name_area;
    }
    public String getName_area() {
        return name_area;
    }

    public void setName_area(String name_area) {
        this.name_area = name_area;
    }
    public String getID_rev() {
        return ID_rev;
    }

    public void setID_rev(String ID_rev) {
        this.ID_rev = ID_rev;
    }

    public String getHas_rev() {
        return has_rev;
    }

    public void setHas_rev(String has_rev) {
        this.has_rev = has_rev;
    }

    public String getHorarev() {
        return horarev;
    }

    public void setHorarev(String horarev) {
        this.horarev = horarev;
    }

    public String getPersons() {
        return persons;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public String getDate_rev() {
        return date_rev;
    }

    public void setDate_rev(String date_rev) {
        this.date_rev = date_rev;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getName_branch() {
        return name_branch;
    }

    public void setName_branch(String name_branch) {
        this.name_branch = name_branch;
    }

    public String getLocation_branch() {
        return location_branch;
    }

    public void setLocation_branch(String location_branch) {
        this.location_branch = location_branch;
    }

    public String getIs_dom() {
        return is_dom;
    }

    public void setIs_dom(String is_dom) {
        this.is_dom = is_dom;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
