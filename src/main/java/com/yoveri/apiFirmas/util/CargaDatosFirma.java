/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoveri.apiFirmas.util;

import com.yoveri.apiFirmas.dto.DatosFirma;
import com.yoveri.conexion.Conexion;

/**
 *
 * @author YOVERI PC1
 */
public class CargaDatosFirma {
    private final Conexion db_con;
    public CargaDatosFirma(Conexion dbConexion) {
        this.db_con = dbConexion;
    }
    public CargaDatosFirma(){
        this.db_con = null;
    }
    
    public DatosFirma infoFirma(String psIdEmpresa,
                                 String psTipoDocto){
        DatosFirma lDatos = new DatosFirma();
        //Se tiene que obtener de configuración de la bd
        //pendiente por definir

        lDatos.setPsEnv("sandbox");
        lDatos.setPsFormat("pades");
        lDatos.setPsUserName("1091583");
        lDatos.setPsPassWd("RY3qn76H");
        lDatos.setPsPin("Javier123_");
        lDatos.setPsLevel("T");
        lDatos.setPsBillingUserName("eclipsoft@eclipsoft");
        lDatos.setPsBillingPassWd("s7J5kfnd");
        lDatos.setPsIdentifier("DS0");
        lDatos.setPsParagraphFormat("[{ \"font\" : [\"Universal-Bold\",6],\"align\":\"right\",\"data_format\" : { \"timezone\":\"America/Guayaquil\", \"strtime\":\"%d/%m/%Y %H:%M:%S\"},\"format\": [\"Firmado por:\",\"$(CN)s\",\"ID: $(serialNumber)s\",\"Fecha: $(date)s\"]}]");
        lDatos.setPsImagenSizeX("200");
        lDatos.setPsImagenSizeY("200");
        lDatos.setPsPositionX1("44");
        lDatos.setPsPositionY1("110");
        lDatos.setPsPositionX2("300");
        lDatos.setPsPositionY2("190");
        lDatos.setPsTsaBookmark("uanataca");
        lDatos.setPsNpage("0");
        lDatos.setPsReason("Prueba de firma");
        lDatos.setPsLocation("Guayaquil, Ecuador");
        lDatos.setPsCodError("0");
        lDatos.setPsMensaje("");
        lDatos.setPsInfoQR("Pruebas QR www.flopez.com.ec");
        //ParametrosGlobales.setRUTA_DESTINO("C:\\datos\\fri\\");
        ParametrosGlobales.setRUTA_DESTINO("//u01//Expedientes//");
        ParametrosGlobales.setRETORNA_DOCTO("N");
        ParametrosGlobales.setVALIDA_ENLINEA("S");
        
        return lDatos;
    }
    
}
