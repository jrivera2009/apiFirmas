/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.yoveri.apiFirmas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Yoveri_PC
 */
public class Certificado {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsNombreComun;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsIdentificador;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsUnidadOrganizacion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsOrganizacion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsPais;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsValidoDesde;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsValidoHasta;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsEmisor;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsCedula;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lsCargo;

    public String getLsCedula() {
        return lsCedula;
    }

    public void setLsCedula(String lsCedula) {
        this.lsCedula = lsCedula;
    }

    public String getLsCargo() {
        return lsCargo;
    }

    public void setLsCargo(String lsCargo) {
        this.lsCargo = lsCargo;
    }

    public String getLsEmisor() {
        return lsEmisor;
    }

    public void setLsEmisor(String lsEmisor) {
        this.lsEmisor = lsEmisor;
    }

    public String getLsNombreComun() {
        return lsNombreComun;
    }

    public void setLsNombreComun(String lsNombreComun) {
        this.lsNombreComun = lsNombreComun;
    }

    public String getLsIdentificador() {
        return lsIdentificador;
    }

    public void setLsIdentificador(String lsIdentificador) {
        this.lsIdentificador = lsIdentificador;
    }

    public String getLsUnidadOrganizacion() {
        return lsUnidadOrganizacion;
    }

    public void setLsUnidadOrganizacion(String lsUnidadOrganizacion) {
        this.lsUnidadOrganizacion = lsUnidadOrganizacion;
    }

    public String getLsOrganizacion() {
        return lsOrganizacion;
    }

    public void setLsOrganizacion(String lsOrganizacion) {
        this.lsOrganizacion = lsOrganizacion;
    }

    public String getLsPais() {
        return lsPais;
    }

    public void setLsPais(String lsPais) {
        this.lsPais = lsPais;
    }

    public String getLsValidoDesde() {
        return lsValidoDesde;
    }

    public void setLsValidoDesde(String lsValidoDesde) {
        this.lsValidoDesde = lsValidoDesde;
    }

    public String getLsValidoHasta() {
        return lsValidoHasta;
    }

    public void setLsValidoHasta(String lsValidoHasta) {
        this.lsValidoHasta = lsValidoHasta;
    }

}
