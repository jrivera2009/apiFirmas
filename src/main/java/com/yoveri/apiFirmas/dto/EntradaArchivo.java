/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoveri.apiFirmas.dto;


public class EntradaArchivo {
    private String psIdEmpresa = "";
    private String psNombreArchivo = "";
    private String psArchivoB64 = "";
    private String psFirmado = "";
    private byte[] pfArchivo = null;
    private String psRuta = "";
    private String psTipoArchivo = "";
    private String psEstado = "";

    public String getPsIdEmpresa() {
        return psIdEmpresa;
    }

    public void setPsIdEmpresa(String psIdEmpresa) {
        this.psIdEmpresa = psIdEmpresa;
    }

    public String getPsNombreArchivo() {
        return psNombreArchivo;
    }

    public void setPsNombreArchivo(String psNombreArchivo) {
        this.psNombreArchivo = psNombreArchivo;
    }

    public String getPsArchivoB64() {
        return psArchivoB64;
    }

    public void setPsArchivoB64(String psArchivoB64) {
        this.psArchivoB64 = psArchivoB64;
    }

    public String getPsFirmado() {
        return psFirmado;
    }

    public void setPsFirmado(String psFirmado) {
        this.psFirmado = psFirmado;
    }

    public byte[] getPfArchivo() {
        return pfArchivo;
    }

    public void setPfArchivo(byte[] pfArchivo) {
        this.pfArchivo = pfArchivo;
    }

    public String getPsRuta() {
        return psRuta;
    }

    public void setPsRuta(String psRuta) {
        this.psRuta = psRuta;
    }

    public String getPsTipoArchivo() {
        return psTipoArchivo;
    }

    public void setPsTipoArchivo(String psTipoArchivo) {
        this.psTipoArchivo = psTipoArchivo;
    }

    public String getPsEstado() {
        return psEstado;
    }

    public void setPsEstado(String psEstado) {
        this.psEstado = psEstado;
    }

    
    
}
