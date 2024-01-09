package com.yoveri.apiFirmas.controller;

import com.yoveri.apiFirmas.dto.EntradaArchivoFirmado;
import com.yoveri.apiFirmas.impl.ApiFirmasImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoveri_PC
 */
@RestController
@RequestMapping("/firmas")
public class ApiFirmasController {

    @Autowired
    private HttpServletRequest req;

    @PostMapping("/firmarLote")
    public ResponseEntity<JSONObject> firmarLote(@RequestBody String body) throws FileNotFoundException, IOException, ParseException {

        JSONObject salida;

        salida = ApiFirmasImpl.firmarLote(body);
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }
    @PostMapping("/firmarPDF")
    public ResponseEntity<JSONObject> firmarPDF(@RequestBody String body) throws FileNotFoundException, IOException, ParseException, Exception {
        //System.out.println("*************************************");
        //System.out.println(body);
        //System.out.println("*************************************");
        JSONObject salida;

        salida = ApiFirmasImpl.firmarPDF(body);
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }    
    
    //Método que descarga documento firmado de eclipsoft
    @PostMapping("/descargarArchivoFirmado")
    public ResponseEntity<JSONObject> descargarArchivoFirmado(@RequestBody EntradaArchivoFirmado p_request) {
        String lsMetodo = "DescargarArchivoFirmado";
        String lsMensaje = "";
        JSONObject ljRetorno = new JSONObject();
        try {
            ljRetorno = ApiFirmasImpl.descargarArchivoFirmado(p_request);
        } catch (Exception ex) {
            lsMensaje = ex.getMessage();
            System.out.println("retorno >>> " + lsMensaje);
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "999");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
        }
        return new ResponseEntity<>(ljRetorno, HttpStatus.OK);
    }
    
        @PostMapping("/verificarArchivo")
    public ResponseEntity<JSONObject> verificarArchivo(@RequestBody String body) throws FileNotFoundException, IOException, ParseException, Exception {
        //System.out.println("*************************************");
        //System.out.println(body);
        //System.out.println("*************************************");
        JSONObject salida;

        salida = ApiFirmasImpl.verificarArchivo(body);
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }    
    

}
