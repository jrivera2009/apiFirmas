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
public class EntradaToken {
    
    private String psUserName  = "";
    private String psPassword  = "";
    private String psAssociate = "";

    public String getPsUserName() {
        return psUserName;
    }

    public void setPsUserName(String psUserName) {
        this.psUserName = psUserName;
    }

    public String getPsPassword() {
        return psPassword;
    }

    public void setPsPassword(String psPassword) {
        this.psPassword = psPassword;
    }

    public String getPsAssociate() {
        return psAssociate;
    }

    public void setPsAssociate(String psAssociate) {
        this.psAssociate = psAssociate;
    }

    
}
