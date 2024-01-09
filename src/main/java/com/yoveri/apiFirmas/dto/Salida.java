/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoveri.apiFirmas.dto;

/**
 *
 * @author YOVERI PC1
 */
public class Salida {
    private String psCodError = "";
    private String psMensaje  = "";
    private String psRuta     = "";

    public String getPsCodError() {
        return psCodError;
    }

    public void setPsCodError(String psCodError) {
        this.psCodError = psCodError;
    }

    public String getPsMensaje() {
        return psMensaje;
    }

    public void setPsMensaje(String psMensaje) {
        this.psMensaje = psMensaje;
    }

    public String getPsRuta() {
        return psRuta;
    }

    public void setPsRuta(String psRuta) {
        this.psRuta = psRuta;
    }
    
    
}
