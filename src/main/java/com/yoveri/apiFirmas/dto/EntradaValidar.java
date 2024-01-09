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
public class EntradaValidar {
    private String psIdEmpresa     = "";
    private String psIdDoc         = "";
    private String psNombreArchivo = "";
    private String psRutaArchivo   = "";

    public String getPsIdEmpresa() {
        return psIdEmpresa;
    }

    public void setPsIdEmpresa(String psIdEmpresa) {
        this.psIdEmpresa = psIdEmpresa;
    }

    public String getIdDoc() {
        return psIdDoc;
    }

    public void setIdDoc(String idDoc) {
        this.psIdDoc = idDoc;
    }

    public String getPsNombreArchivo() {
        return psNombreArchivo;
    }

    public void setPsNombreArchivo(String psNombreArchivo) {
        this.psNombreArchivo = psNombreArchivo;
    }

    public String getPsIdDoc() {
        return psIdDoc;
    }

    public void setPsIdDoc(String psIdDoc) {
        this.psIdDoc = psIdDoc;
    }

    public String getPsRutaArchivo() {
        return psRutaArchivo;
    }

    public void setPsRutaArchivo(String psRutaArchivo) {
        this.psRutaArchivo = psRutaArchivo;
    }
    
}
