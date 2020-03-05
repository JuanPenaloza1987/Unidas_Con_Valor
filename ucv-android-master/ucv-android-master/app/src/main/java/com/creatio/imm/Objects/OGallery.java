package com.creatio.imm.Objects;

/**
 * Created by gerardo on 20/06/18.
 */

public class OGallery {
    String ID;
    String name;
    String name_server;
    String orden;

    public OGallery(String ID, String name, String name_server, String orden) {
        this.ID = ID;
        this.name = name;
        this.name_server = name_server;
        this.orden = orden;
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

    public String getName_server() {
        return name_server;
    }

    public void setName_server(String name_server) {
        this.name_server = name_server;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
}
