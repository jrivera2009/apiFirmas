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
public class EntradaArchivoFirmado {
    private String psWebHookTxt = "";
    private String psWebHookPdf = "";
    private String psRutaArchivo = "";
    private String psNombreArchivo = "";
    private String psIdEmpresa = "";
    private String psClaseFirma;

    public String getPsWebHookTxt() {
        return psWebHookTxt;
    }

    public void setPsWebHookTxt(String psWebHookTxt) {
        this.psWebHookTxt = psWebHookTxt;
    }

    public String getPsWebHookPdf() {
        return psWebHookPdf;
    }

    public void setPsWebHookPdf(String psWebHookPdf) {
        this.psWebHookPdf = psWebHookPdf;
    }

    public String getPsRutaArchivo() {
        return psRutaArchivo;
    }

    public void setPsRutaArchivo(String psRutaArchivo) {
        this.psRutaArchivo = psRutaArchivo;
    }

    public String getPsNombreArchivo() {
        return psNombreArchivo;
    }

    public void setPsNombreArchivo(String psNombreArchivo) {
        this.psNombreArchivo = psNombreArchivo;
    }

    public String getPsIdEmpresa() {
        return psIdEmpresa;
    }

    public void setPsIdEmpresa(String psIdEmpresa) {
        this.psIdEmpresa = psIdEmpresa;
    }
    
    
}
