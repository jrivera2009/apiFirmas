/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.yoveri.apiFirmas.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utilws.beans.DatosWSDL;
import utilws.util.ConsumosServicios;

/**
 *
 * @author Yoveri_PC
 */
public class CTRConsumos {

    public static JSONObject realizarPeticion(String psUrl, JSONObject poTrama) {
        String lsUrl = psUrl;
        String lsTrama = JSONObject.toJSONString(poTrama);
        String lsRetorno = null;
        JSONObject jsonObject = null;
        JSONParser parser = new JSONParser();
        DatosWSDL datos = ConsumosServicios.consumirPost("1", lsUrl, lsTrama, null);
        if (datos.getResultado() != null) {
            if (datos.getResultado().getExito().equals("S")) {
                lsRetorno = datos.getContenido();
                try {
                    jsonObject = (JSONObject) parser.parse(lsRetorno);
                } catch (ParseException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        //System.out.println("*************************************************");
        //System.out.println(lsUrl);
        //System.out.println(lsTrama);
        //System.out.println(lsRetorno);
        //System.out.println("*************************************************");
        return jsonObject;
    }
}
