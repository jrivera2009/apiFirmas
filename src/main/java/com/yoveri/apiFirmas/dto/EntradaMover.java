/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.yoveri.apiFirmas.dto;

/**
 *
 * @author Yoveri_PC
 */
public class EntradaMover {

    private String psNombreArchivo = "";
    private String psNombreArchivoDestino="";
    private String psRutaOrigen = "";
    private String psRutaDestino = "";

    public String getPsNombreArchivoDestino() {
        return psNombreArchivoDestino;
    }

    public void setPsNombreArchivoDestino(String psNombreArchivoDestino) {
        this.psNombreArchivoDestino = psNombreArchivoDestino;
    }
    
    

    public String getPsNombreArchivo() {
        return psNombreArchivo;
    }

    public void setPsNombreArchivo(String psNombreArchivo) {
        this.psNombreArchivo = psNombreArchivo;
    }

    public String getPsRutaOrigen() {
        return psRutaOrigen;
    }

    public void setPsRutaOrigen(String psRutaOrigen) {
        this.psRutaOrigen = psRutaOrigen;
    }

    public String getPsRutaDestino() {
        return psRutaDestino;
    }

    public void setPsRutaDestino(String psRutaDestino) {
        this.psRutaDestino = psRutaDestino;
    }
    
    

}
