package com.creatio.imm;

import android.app.Application;

public class GlobalsClassTarjeta extends Application {

    private String urlConexion;
    private String numTarjetaGral;
    public String urlCon = R.string.apiurl + "GetInfoByCardId";
    public String getUrlConexion() {
        return urlConexion;
    }

    public void setUrlConexion(String urlConexion) {
        this.urlConexion = urlConexion;
    }

    public String getNumTarjetaGral() {
        return numTarjetaGral;
    }

    public void setNumTarjetaGral(String numTarjetaGral) {
        this.numTarjetaGral = numTarjetaGral;
    }


}
