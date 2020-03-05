package com.creatio.imm.Objects;

/**
 * Created by gerardo on 26/06/18.
 */

public class OInfo {
    String name,description,phone,horario;
    int icon;

    public OInfo(String name, String description, int icon, String phone, String horario) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.phone = phone;
        this.horario = horario;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
