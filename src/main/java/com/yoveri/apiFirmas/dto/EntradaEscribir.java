/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoveri.apiFirmas.dto;

import java.util.List;

/**
 *
 * @author YOVERI PC1
 */
public class EntradaEscribir {
    private String psIdEmpresa = "";
    private String psCodReporte = "";
    private String psNombreArchivo ="";
    private String psRutaArchivo = "";
    private List<ParametrosReporte> paParametros  ; 

    public String getPsIdEmpresa() {
        return psIdEmpresa;
    }

    public void setPsIdEmpresa(String psIdEmpresa) {
        this.psIdEmpresa = psIdEmpresa;
    }

    public String getPsCodReporte() {
        return psCodReporte;
    }

    public void setPsCodReporte(String psCodReporte) {
        this.psCodReporte = psCodReporte;
    }

    public String getPsNombreArchivo() {
        return psNombreArchivo;
    }

    public void setPsNombreArchivo(String psNombreArchivo) {
        this.psNombreArchivo = psNombreArchivo;
    }

    public String getPsRutaArchivo() {
        return psRutaArchivo;
    }

    public void setPsRutaArchivo(String psRutaArchivo) {
        this.psRutaArchivo = psRutaArchivo;
    }

    public List<ParametrosReporte> getPaParametros() {
        return paParametros;
    }

    public void setPaParametros(List<ParametrosReporte> paParametros) {
        this.paParametros = paParametros;
    }
    
    
}
