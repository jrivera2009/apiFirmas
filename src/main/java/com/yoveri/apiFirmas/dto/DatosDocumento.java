/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.yoveri.apiFirmas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 *
 * @author Yoveri_PC
 */
public class DatosDocumento {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsFechaFirma;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsSelloTiempo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsRazon;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsLocalidad;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsFirmante;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsEditora;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsEntidadSelloTiempo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsEditoraSelloTiempo;   
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Certificado> laCertificados;

    public List<Certificado> getLaCertificados() {
        return laCertificados;
    }

    public void setLaCertificados(List<Certificado> laCertificados) {
        this.laCertificados = laCertificados;
    }


    

    public String getLsEntidadSelloTiempo() {
        return lsEntidadSelloTiempo;
    }

    public void setLsEntidadSelloTiempo(String lsEntidadSelloTiempo) {
        this.lsEntidadSelloTiempo = lsEntidadSelloTiempo;
    }

    public String getLsEditoraSelloTiempo() {
        return lsEditoraSelloTiempo;
    }

    public void setLsEditoraSelloTiempo(String lsEditoraSelloTiempo) {
        this.lsEditoraSelloTiempo = lsEditoraSelloTiempo;
    }
    
    

    public String getLsEditora() {
        return lsEditora;
    }

    public void setLsEditora(String lsEditora) {
        this.lsEditora = lsEditora;
    }
    
    

    public String getLsFirmante() {
        return lsFirmante;
    }

    public void setLsFirmante(String lsFirmante) {
        this.lsFirmante = lsFirmante;
    }
    
    

    public String getLsLocalidad() {
        return lsLocalidad;
    }

    public void setLsLocalidad(String lsLocalidad) {
        this.lsLocalidad = lsLocalidad;
    }
    
    
    
    

    public String getLsRazon() {
        return lsRazon;
    }

    public void setLsRazon(String lsRazon) {
        this.lsRazon = lsRazon;
    }
    
    

    public String getLsFechaFirma() {
        return lsFechaFirma;
    }

    public void setLsFechaFirma(String lsFechaFirma) {
        this.lsFechaFirma = lsFechaFirma;
    }

    public String getLsSelloTiempo() {
        return lsSelloTiempo;
    }

    public void setLsSelloTiempo(String lsSelloTiempo) {
        this.lsSelloTiempo = lsSelloTiempo;
    }

  
    
    

}
