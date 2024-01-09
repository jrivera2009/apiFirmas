/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gadm.tulcan.rest.firmarpdf;

import com.gadm.tulcan.firmarpdf.Funcion_Firmarpdf;
import com.gadm.tulcan.rest.modelo.EntradasFirmarpdf;
import com.gadm.tulcan.rest.modelo.SalidasFirmarpdf;

/**
 *
 * @author Yoveri_PC
 */
public class Test {

    public static void main(String[] args) throws Exception {
        EntradasFirmarpdf datos = new EntradasFirmarpdf();
        datos.setArchivop12("e://pruebas/jlzm.p12");
        datos.setDocumentopdf("e://pruebas/docto.pdf");
        //datos.setContrasena("YOV23nari");
        datos.setContrasena("1977TASZG");
        EntradasFirmarpdf entradas = new EntradasFirmarpdf();
        entradas = datos;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(datos.getArchivop12());
        System.out.println(datos.getDocumentopdf());

        System.out.println(datos.getPagina());
        System.out.println(datos.getH());
        System.out.println(datos.getV());
        SalidasFirmarpdf firmar = new SalidasFirmarpdf();
        Funcion_Firmarpdf comprobar = new Funcion_Firmarpdf();
        SalidasFirmarpdf salida = null;

        if (comprobar.Invocador(entradas.getDocumentopdf(), entradas.getArchivop12(), entradas.getContrasena(), entradas.getPagina(), entradas.getH(), entradas.getV()) == false) {

            salida = null;

        } else {
            salida = firmar;

        }

    }
}
