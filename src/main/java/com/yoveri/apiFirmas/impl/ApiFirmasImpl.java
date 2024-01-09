package com.yoveri.apiFirmas.impl;

//import com.google.gson.Gson;
//import com.google.gson.Gson;
import com.gadm.tulcan.firmarpdf.Funcion_Firmarpdf;
import com.gadm.tulcan.rest.modelo.EntradasFirmarpdf;
import com.gadm.tulcan.rest.modelo.SalidasFirmarpdf;
import com.google.gson.Gson;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.yoveri.apiFirmas.dto.Certificado;
import com.yoveri.apiFirmas.dto.DatosArchivoEntrada;
import com.yoveri.apiFirmas.dto.DatosDocumento;
import com.yoveri.apiFirmas.dto.DatosFirma;
import com.yoveri.apiFirmas.dto.EntradaArchivoFirmado;
import com.yoveri.apiFirmas.dto.EntradaToken;
import com.yoveri.apiFirmas.dto.ParametrosEntradaFirmas;
import com.yoveri.apiFirmas.util.ParametrosGlobales;
import io.rubrica.certificate.CertEcUtils;
import io.rubrica.certificate.to.DatosUsuario;
import io.rubrica.core.Util;
import io.rubrica.keystore.FileKeyStoreProvider;
import io.rubrica.keystore.KeyStoreProvider;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Store;

/**
 *
 * @author: Francis López
 * @version: 13-04-2022 v: 1.0
 */
public class ApiFirmasImpl {

    /**
     * Nombre del pool
     */
    private static final String POOL = ParametrosGlobales.getPOOL();

    public static JSONObject verificarArchivo(String body) throws GeneralSecurityException, FileNotFoundException, IOException, ParseException, Exception {
        Boolean lbEliminarArchivo = false;
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String lsFechaFirma = "";
        String lsSelloTiempo = null;
        //new SimpleDateFormat("yyyy-MM-dd-HH-mm");

        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // System.out.println(ks.size());
        /* try (FileInputStream stream = new FileInputStream(ROOT)) {
            ks.setCertificateEntry("root", cf.generateCertificate(stream));
        }*/

        JSONObject ljRetorno = new JSONObject();

        DatosArchivoEntrada loDatosEntrada;
        JSONObject jsonEntrada = new JSONObject();
        JSONParser parser = new JSONParser();
        Gson gson = new Gson();
        // System.out.println(body);
        jsonEntrada = (JSONObject) parser.parse(body);
        //System.out.println("********************");
        // System.out.println(jsonEntrada.toJSONString());
        loDatosEntrada = (DatosArchivoEntrada) gson.fromJson(jsonEntrada.toJSONString(), DatosArchivoEntrada.class);
        File lfArchivo;
        if (loDatosEntrada.getPsRutaArchivo() != null) {
            lfArchivo = new File(loDatosEntrada.getPsRutaArchivo());
            if (!lfArchivo.exists()) {
                ljRetorno.put("lsMensaje", "Archivo no existe");
                ljRetorno.put("lsError", "Archivo no existe :" + loDatosEntrada.getPsRutaArchivo());
                ljRetorno.put("lsCodError", "010");
                return ljRetorno;
            }
            lbEliminarArchivo = false;
        } else {

            String lsArchivoB64 = loDatosEntrada.getPsArchivo();
            if (lsArchivoB64 == null) {
                ljRetorno.put("lsMensaje", "Debe enviar el Archivo a Verificar");
                ljRetorno.put("lsError", "Debe enviar el Archivo a Verificar");
                ljRetorno.put("lsCodError", "020");
                return ljRetorno;
            }

            byte[] buffered = null;
            try {
                buffered = Base64.getDecoder().decode(lsArchivoB64);
            } catch (Exception e) {
                ljRetorno.put("lsMensaje", "El Certificado debe estar codificado en BASE64");
                ljRetorno.put("lsError", "El Certificado debe estar codificado en BASE64");
                ljRetorno.put("lsCodError", "030");
                return ljRetorno;
            }
            Random rnd = new Random();

            lfArchivo = File.createTempFile("archivo" + Double.toString(rnd.nextLong()), null);
            OutputStream outStream = new FileOutputStream(lfArchivo);
            outStream.write(buffered);
            IOUtils.closeQuietly(outStream);
            lbEliminarArchivo = true;
        }

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(lfArchivo));

        //pdfDoc.getDocumentInfo().getAuthor();
        // pdfDoc.getDocumentInfo().getCreator();
        /*System.out.println(pdfDoc.getDocumentInfo().getAuthor());
        System.out.println(pdfDoc.getDocumentInfo().getCreator());
        System.out.println(pdfDoc.getDocumentInfo().getProducer());
        System.out.println(pdfDoc.getDocumentInfo().getSubject());
        System.out.println(pdfDoc.getDocumentInfo().getTitle());*/
        //System.out.println(pdfDoc.getFirstPage().getPageSize().getHeight());
        //System.out.println(pdfDoc.getFirstPage().getPageSize().getWidth());
        ljRetorno.put("liPaginas", pdfDoc.getNumberOfPages());
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();

        List<DatosDocumento> firmas = new ArrayList<>();
        List<Certificado> certificados = new ArrayList<>();

        JSONObject ljDatosCertitificado = new JSONObject();
        JSONArray ljArray = new JSONArray();
        JSONArray laFirmas = new JSONArray();
        if (names.size() > 0) {
            ljRetorno.put("lsFirmado", "S");

        } else {
            ljRetorno.put("lsFirmado", "N");
        }

        for (String name : names) {
            certificados = new ArrayList<>();
            PdfPKCS7 pkcs7 = getSignatureData(signUtil, name);
         
            System.out.println("getDigestAlgorithm"+pkcs7.getDigestAlgorithm());
            System.out.println("getHashAlgorithm"+pkcs7.getHashAlgorithm());
            DatosDocumento documento = new DatosDocumento();
            documento.setLsRazon(pkcs7.getReason());
            documento.setLsLocalidad(pkcs7.getLocation());
            if (pkcs7.getSignDate() != null) {
                documento.setLsFechaFirma(date_format.format(pkcs7.getSignDate().getTime()));
                lsFechaFirma = date_format.format(pkcs7.getSignDate().getTime());
            }

            if (pkcs7.getTimeStampDate() != null) {
                documento.setLsSelloTiempo(date_format.format(pkcs7.getTimeStampDate().getTime()));
                lsSelloTiempo=date_format.format(pkcs7.getTimeStampDate().getTime());
            }
            //System.out.println(signUtil.getRevision(name));
            // System.out.println(signUtil.getTotalRevisions());
            // System.out.println("signatureCoversWholeDocument"+name+ signUtil.signatureCoversWholeDocument(name));

            X509Certificate lCertFimanteX509 = pkcs7.getSigningCertificate();
            Principal firmante = lCertFimanteX509.getSubjectDN();
            String firmanteArray[] = firmante.toString().split(",");
            for (String s : firmanteArray) {
                String[] str = s.trim().split("=");
                String key = str[0];
                String value = str[1];
                if (key.equals("CN")) {
                    documento.setLsFirmante(value);
                }
            }
            if (documento.getLsFirmante() == null) {
                documento.setLsFirmante(Util.getCN(lCertFimanteX509));
            }
            Certificate[] certs = pkcs7.getSignCertificateChain();
            for (int i = 0; i < certs.length; i++) {
                X509Certificate cert = (X509Certificate) certs[i];
                Principal subject = cert.getSubjectDN();
                String subjectArray[] = subject.toString().split(",");
                Certificado lCertificado = new Certificado();

                lCertificado.setLsValidoDesde(date_format.format(cert.getNotBefore().getTime()));
                lCertificado.setLsValidoHasta(date_format.format(cert.getNotAfter().getTime()));
                for (String s : subjectArray) {
                    String[] str = s.trim().split("=");
                    String key = str[0];
                    String value = str[1];
                    //System.out.println(key + " - " + value);
                    if (key.equals("CN")) {
                        lCertificado.setLsNombreComun(value);
                    } else if (key.equals("OU")) {
                        lCertificado.setLsUnidadOrganizacion(value);
                    } else if (key.equals("O")) {
                        lCertificado.setLsOrganizacion(value);
                    } else if (key.equals("C")) {
                        lCertificado.setLsPais(value);
                    } else if (key.equals("SERIALNUMBER")) {
                        lCertificado.setLsIdentificador(value);
                    }
                }

                if (lCertificado.getLsNombreComun() == null) {
                    lCertificado.setLsNombreComun(Util.getCN(cert));
                }
                if (lCertificado.getLsNombreComun().equals(documento.getLsFirmante())) {
                    DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(cert);
                    if (datosUsuario != null) {
                        lCertificado.setLsCedula(datosUsuario.getCedula());
                        lCertificado.setLsCargo(datosUsuario.getCargo());
                    }

                }

                Principal editora = cert.getIssuerDN();
                String editoraArray[] = editora.toString().split(",");
                for (String s : editoraArray) {
                    String[] str = s.trim().split("=");
                    String key = str[0];
                    String value = str[1];
                    //System.out.println(key + " - " + value);
                    if (key.equals("CN")) {
                        documento.setLsEditora(value);
                        lCertificado.setLsEmisor(value);
                    }
                }

                certificados.add(lCertificado);

                //  System.out.println("Subject: " + cert.getSubjectDN());
                //  System.out.println("Subject: " + cert.getSubjectAlternativeNames());
                // OUT_STREAM.println("=== Certificate " + i + " ===");
                //showCertificateInfo(cert, cal.getTime());
                // resultado.setLsFirmado("S");
                //String strDate = dateFormat.format(cal.getTime());
                //System.out.println(cal.getTime().toString());
                //   resultado.setLsFechaFirma(strDate);
            }

            TimeStampToken tsToken = pkcs7.getTimeStampToken();

            if (tsToken != null) { // Timestamping Change Openpdf to itext
                Store<X509CertificateHolder> certSello = tsToken.getCertificates();
                Collection<X509CertificateHolder> certificateHolders = certSello.getMatches(null);
                Set<X509Certificate> additionalCerts = new HashSet<>();
                JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
                for (X509CertificateHolder certHolder : certificateHolders) {
                    X509Certificate certificate = certificateConverter.getCertificate(certHolder);
                    // System.out.println(certificate.getNotAfter());
                }
                TimeStampTokenInfo tsInfo = tsToken.getTimeStampInfo();
                if (tsInfo.getTsa() != null) {
                    if (tsInfo.getTsa().getName() != null) {
                        Certificado lCertificadoSello = new Certificado();
                        String lsInfoSello = tsInfo.getTsa().getName().toString();
                        String subjectArray[] = lsInfoSello.split(",");
                        for (String s : subjectArray) {
                            String[] str = s.trim().split("=");
                            String key = str[0];
                            String value = str[1];
                            //System.out.println(key + " - " + value);

                            if (key.equals("CN")) {
                                lCertificadoSello.setLsNombreComun(value);
                                documento.setLsEntidadSelloTiempo(value);
                            } else if (key.equals("OU")) {
                                lCertificadoSello.setLsUnidadOrganizacion(value);
                            } else if (key.equals("O")) {
                                lCertificadoSello.setLsOrganizacion(value);
                            } else if (key.equals("C")) {
                                lCertificadoSello.setLsPais(value);
                            } else if (key.equals("SERIALNUMBER")) {
                                lCertificadoSello.setLsIdentificador(value);
                            }

                        }

                        certificados.add(lCertificadoSello);
                    }
                }

                SignerId signerId = tsToken.getSID();
                X500Name signerCertIssuer = signerId.getIssuer();
                // System.out.println("signerCertIssuer:" + signerCertIssuer);
                if (signerCertIssuer != null) {
                    String subjectArray[] = signerCertIssuer.toString().split(",");
                    for (String s : subjectArray) {
                        String[] str = s.trim().split("=");
                        String key = str[0];
                        String value = str[1];
                        //System.out.println(key + " - " + value);
                        if (key.equals("CN")) {
                            documento.setLsEditoraSelloTiempo(value);
                        }
                    }
                }

            }

            /*List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, cal);
            if (errors.size() == 0) {
                System.out.println("Certificates verified against the KeyStore");
            } else {
                System.out.println(errors);
            }*/
            //ljArray.add(ljDatosCertitificado);
            documento.setLaCertificados(certificados);
            firmas.add(documento);
        }
        if (firmas.size() > 0) {
            ljRetorno.put("laFirmas", firmas);
            ljRetorno.put("lsFechaFirma", lsFechaFirma);
            if (lsSelloTiempo != null) {
                ljRetorno.put("lsSelloTiempo", lsSelloTiempo);
            }
        }
        pdfDoc.close();

        if (lbEliminarArchivo) {
            lfArchivo.delete();
        }

        ljRetorno.put("lsCodError", "0");
        return ljRetorno;

    }

    public static PdfPKCS7 getSignatureData(SignatureUtil signUtil, String name) throws GeneralSecurityException {
        PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);

        //OUT_STREAM.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
        //OUT_STREAM.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
        // OUT_STREAM.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
        return pkcs7;
    }

    /**
     * Convierte CLOB a String
     *
     * @param clob
     * @return String con información del clob
     * @throws SQLException
     * @throws IOException
     */
    public static String getClobString(Clob clob) throws SQLException, IOException {
        BufferedReader stringReader = new BufferedReader(clob.getCharacterStream());
        StringBuilder strBuff = new StringBuilder();
        String singleLine;
        while ((singleLine = stringReader.readLine()) != null) {
            strBuff.append(singleLine);
        }

        stringReader.close();
        clob.free();
        return strBuff.toString();
    }

    public static JSONObject firmarPDF(String body) throws FileNotFoundException, IOException, ParseException, Exception {
        ParametrosEntradaFirmas loDatosEntrada;
        JSONObject jsonEntrada = new JSONObject();
        JSONObject ljRetorno = new JSONObject();
        JSONParser parser = new JSONParser();
        Gson gson = new Gson();
        // System.out.println(body);
        jsonEntrada = (JSONObject) parser.parse(body);
        //System.out.println("********************");
        // System.out.println(jsonEntrada.toJSONString());
        loDatosEntrada = (ParametrosEntradaFirmas) gson.fromJson(jsonEntrada.toJSONString(), ParametrosEntradaFirmas.class);

        String lsArchivo = loDatosEntrada.getPsRutaArchivo() + loDatosEntrada.getPsNombreArchivo();
        File lfArchivo = new File(lsArchivo);
        if (!lfArchivo.exists()) {
            ljRetorno.put("lsMensaje", "Archivo no existe");
            ljRetorno.put("lsMensaje", "Archivo no existe");
            ljRetorno.put("lsArchivo", lsArchivo);
            ljRetorno.put("lsCodError", "010");
            return ljRetorno;
        }
        if (loDatosEntrada.getPsClaseFirma() == null) {
            ljRetorno.put("lsMensaje", "Debe enviar La Clase de Firma");
            ljRetorno.put("lsError", "Debe enviar La Clase de Firma");
            ljRetorno.put("lsCodError", "020");
            return ljRetorno;
        }
        if (loDatosEntrada.getPsClaseFirma().equals("SCD")) {
            //Firma con Security Data
            ljRetorno = firmarSecurityData(loDatosEntrada);
        } else if (loDatosEntrada.getPsClaseFirma().equals("ECL")) {
            //Firma con Eclisoft
            ljRetorno = firmarEclipsoft(loDatosEntrada);
            ljRetorno.put("lsArchivoDestino", "");
            ljRetorno.put("lsDirectorioDestino", "");
        } else if (loDatosEntrada.getPsClaseFirma().equals("INT")) {
            //Firma Interna
            ljRetorno = firmarPdfLocal(loDatosEntrada);

        }

        return ljRetorno;
    }

    public static JSONObject firmarSecurityData(ParametrosEntradaFirmas pDatosFirma) throws FileNotFoundException {
        JSONObject loTrama = new JSONObject();
        loTrama.put("cedulaFirmante", pDatosFirma.getPsFirmaUsuario());
        loTrama.put("identificador", pDatosFirma.getPsIdDocumento());
        loTrama.put("claveEncriptada", pDatosFirma.getPsFirmaClave());
        loTrama.put("usuario", pDatosFirma.getPsAplUsuario());
        loTrama.put("password", pDatosFirma.getPsAplClave());
        loTrama.put("posx", Integer.toString(pDatosFirma.getPsPositionX1()));
        loTrama.put("posy", Integer.toString(pDatosFirma.getPsPositionY1()));
        loTrama.put("ancho", Integer.toString(pDatosFirma.getPsPositionX2()));
        loTrama.put("alto", Integer.toString(pDatosFirma.getPsPositionY2()));
        loTrama.put("pag", pDatosFirma.getPsPagina());
        loTrama.put("nomDoc", pDatosFirma.getPsNombreArchivo());

        String qrCodeString = null;
        ByteArrayOutputStream out = null;
        JSONObject ljRetorno = new JSONObject();
        try {
            //Inicio Generación de Código QR
            out = generarQR.generarImagenQR(pDatosFirma.getPsQRInfo());
            qrCodeString = Base64.getEncoder().encodeToString(out.toByteArray());//QR firma 
            loTrama.put("codigoQR", "data:image/png;base64," + qrCodeString);
            //Fin Generación de Código QR
            String lsArchivo = pDatosFirma.getPsRutaArchivo() + pDatosFirma.getPsNombreArchivo();
            String lsArchivoDestino = pDatosFirma.getPsNombreArchivo().substring(0, pDatosFirma.getPsNombreArchivo().length() - 4) + "_signedSCD.pdf";
            File archivo = new File(lsArchivo);
            byte[] lbArchivo = null;
            lbArchivo = FileUtils.readFileToByteArray(archivo);
            String base64String = Base64.getEncoder().encodeToString(lbArchivo);
            loTrama.put("archivo", base64String);

            JSONObject ljRespuestaApi = CTRConsumos.realizarPeticion(pDatosFirma.getPsAplUrl(), loTrama);
            if (ljRespuestaApi != null) {
                String lsMensaje = (String) ljRespuestaApi.get("mensaje");

                if (lsMensaje.equals("FIRMADO")) {
                    String lsBase64 = (String) ljRespuestaApi.get("resp");
                    byte[] asBytes = Base64.getDecoder().decode(lsBase64);
                    File lfArchivo;
                    if (pDatosFirma.getPsSincronico() == null) {
                        lfArchivo = new File(pDatosFirma.getPsRutaArchivo() + lsArchivoDestino);
                    } else if (pDatosFirma.getPsSincronico().equals("S")) {
                        lfArchivo = new File(lsArchivo);
                    } else {
                        lfArchivo = new File(pDatosFirma.getPsRutaArchivo() + lsArchivoDestino);
                    }
                    OutputStream outStream = new FileOutputStream(lfArchivo);
                    outStream.write(asBytes);
                    IOUtils.closeQuietly(outStream);
                    ljRetorno.put("lsArchivoDestino", lsArchivoDestino);
                    ljRetorno.put("lsDirectorioDestino", pDatosFirma.getPsRutaArchivo());
                    ljRetorno.put("lsCodError", "0");
                } else {
                    ljRetorno.put("lsCodError", "100");
                    ljRetorno.put("lsError", lsMensaje);
                    ljRetorno.put("lsMensaje", lsMensaje);
                }
            } else {
                ljRetorno.put("lsCodError", "999");
                ljRetorno.put("lsError", "SIN RESPUESTA");
                ljRetorno.put("lsMensaje", "SIN RESPUESTA");
            }
        } catch (Exception ex) {
            ljRetorno.put("lsCodError", "999");
            ljRetorno.put("lsError", ex.getMessage());
            ljRetorno.put("lsMensaje", "ERROR AL FIRMAR ARCHIVO.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
        return ljRetorno;
    }

    public static JSONObject firmarPdfLocal(ParametrosEntradaFirmas pDatosFirma) throws FileNotFoundException, IOException, ParseException {
        JSONObject ljRetorno = new JSONObject();
        String lsArchivoB64 = pDatosFirma.getPsCertificado();
        if (lsArchivoB64 == null) {
            ljRetorno.put("lsMensaje", "Debe enviar el Certificado");
            ljRetorno.put("lsError", "Debe enviar el Certificado");
            ljRetorno.put("lsCodError", "020");
            return ljRetorno;
        }
        String lsClave = pDatosFirma.getPsFirmaClave();

        if (lsClave == null) {
            ljRetorno.put("lsMensaje", "Debe enviar la clave del certificado");
            ljRetorno.put("lsError", "Debe enviar la clave del certificado");
            ljRetorno.put("lsCodError", "030");
            return ljRetorno;
        }

        byte[] buffered = null;
        try {
            buffered = Base64.getDecoder().decode(lsArchivoB64);
        } catch (Exception e) {
            ljRetorno.put("lsMensaje", "El Certificado debe estar codificado en BASE64");
            ljRetorno.put("lsError", "El Certificado debe estar codificado en BASE64");
            ljRetorno.put("lsCodError", "040");
            return ljRetorno;
        }

        File lfArchivo;
        lfArchivo = File.createTempFile("cert", null);
        OutputStream outStream = new FileOutputStream(lfArchivo);
        outStream.write(buffered);
        IOUtils.closeQuietly(outStream);

        try {
            KeyStoreProvider ksp = new FileKeyStoreProvider(lfArchivo);
            KeyStore keyStore = ksp.getKeystore(lsClave.toCharArray());
        } catch (Exception e) {
            ljRetorno.put("lsMensaje", "Contraseña incorrecta.");
            ljRetorno.put("lsError", e.getMessage());
            ljRetorno.put("lsCodError", "050");
            System.out.println(e.getMessage());
            return ljRetorno;
        }
        String lsArchivo = pDatosFirma.getPsRutaArchivo() + pDatosFirma.getPsNombreArchivo();

        try {
            EntradasFirmarpdf datos = new EntradasFirmarpdf();
            //datos.setArchivop12("e://pruebas/jlzm.p12");
            datos.setDocumentopdf(lsArchivo);
            //datos.setContrasena("YOV23nari");
            //datos.setContrasena("1977TASZG");
            EntradasFirmarpdf entradas = new EntradasFirmarpdf();
            entradas = datos;
            SalidasFirmarpdf firmar = new SalidasFirmarpdf();
            Funcion_Firmarpdf comprobar = new Funcion_Firmarpdf();
            SalidasFirmarpdf salida = new SalidasFirmarpdf();

            if (comprobar.Invocador(pDatosFirma,
                    entradas.getDocumentopdf(),
                    lfArchivo,
                    lsClave,
                    entradas.getPagina(),
                    entradas.getH(),
                    entradas.getV(), salida) == false) {

                salida = null;

            } else {

                ljRetorno.put("lsFirmado", "S");
                ljRetorno.put("lsCodError", "0");
                ljRetorno.put("lsArchivoDestino", salida.getDocFirmado());
                ljRetorno.put("lsDirectorioDestino", pDatosFirma.getPsRutaArchivo());
            }
        } catch (Exception e) {

        }

        return ljRetorno;
    }

    public static JSONObject firmarLote(String body) throws FileNotFoundException, IOException, ParseException {
        LocalDateTime lFechaInicio = LocalDateTime.now();
        JSONObject ljRetorno = new JSONObject();
        JSONObject jsonEntrada = new JSONObject();
        JSONArray jsonArchivos = new JSONArray();
        JSONArray jsonLog = new JSONArray();
        int lnCantidad = 0;
        int lnCantidadFirmados = 0;
        JSONParser parser = new JSONParser();
        jsonEntrada = (JSONObject) parser.parse(body);
        jsonArchivos = (JSONArray) jsonEntrada.get("laDocumentos");
        if (jsonArchivos == null) {
            ljRetorno.put("lsMensaje", "Debe enviar Documentos a firmar");
            ljRetorno.put("lsError", "Debe enviar Documentos a firmar");
            ljRetorno.put("lsCodError", "010");
            return ljRetorno;
        }

        lnCantidad = jsonArchivos.size();
        if (lnCantidad == 0) {
            ljRetorno.put("lsMensaje", "Debe enviar Documentos a firmar");
            ljRetorno.put("lsError", "Debe enviar Documentos a firmar");
            ljRetorno.put("lsCodError", "010");
            return ljRetorno;
        }

        String lsArchivoB64 = (String) jsonEntrada.get("lsCertificado");

        if (lsArchivoB64 == null) {
            ljRetorno.put("lsMensaje", "Debe enviar el Certificado");
            ljRetorno.put("lsError", "Debe enviar el Certificado");
            ljRetorno.put("lsCodError", "020");
            return ljRetorno;
        }
        String lsClave = (String) jsonEntrada.get("lsClave");

        if (lsClave == null) {
            ljRetorno.put("lsMensaje", "Debe enviar la clave del certificado");
            ljRetorno.put("lsError", "Debe enviar la clave del certificado");
            ljRetorno.put("lsCodError", "030");
            return ljRetorno;
        }

        byte[] buffered = null;
        buffered = Base64.getDecoder().decode(lsArchivoB64);
        File lfArchivo;
        lfArchivo = File.createTempFile("cert", null);
        OutputStream outStream = new FileOutputStream(lfArchivo);
        outStream.write(buffered);
        IOUtils.closeQuietly(outStream);

        try {
            KeyStoreProvider ksp = new FileKeyStoreProvider(lfArchivo);
            KeyStore keyStore = ksp.getKeystore(lsClave.toCharArray());
        } catch (Exception e) {
            ljRetorno.put("lsMensaje", "Contraseña incorrecta.");
            ljRetorno.put("lsError", e.getMessage());
            ljRetorno.put("lsCodError", "040");
            System.out.println(e.getMessage());
            return ljRetorno;
        }

        for (int i = 0; i < jsonArchivos.size(); i++) {
            JSONObject lDetalle = new JSONObject();
            lDetalle = (JSONObject) jsonArchivos.get(i);
            String lsArchivoAFirmar = (String) lDetalle.get("lsArchivo");

            File lfVerificar = new File(lsArchivoAFirmar);
            if (!lfVerificar.exists()) {
                lDetalle.put("lsMensaje", "Archivo no existe");
            }

            jsonLog.add(lDetalle);

            try {
                EntradasFirmarpdf datos = new EntradasFirmarpdf();
                //datos.setArchivop12("e://pruebas/jlzm.p12");
                datos.setDocumentopdf(lsArchivoAFirmar);
                //datos.setContrasena("YOV23nari");
                //datos.setContrasena("1977TASZG");
                EntradasFirmarpdf entradas = new EntradasFirmarpdf();
                entradas = datos;

                SalidasFirmarpdf firmar = new SalidasFirmarpdf();
                Funcion_Firmarpdf comprobar = new Funcion_Firmarpdf();
                SalidasFirmarpdf salida = null;

                if (comprobar.Invocador(entradas.getDocumentopdf(), lfArchivo, lsClave, entradas.getPagina(), entradas.getH(), entradas.getV()) == false) {

                    salida = null;

                } else {
                    salida = firmar;
                    System.out.println("firmado");
                    lDetalle.put("lsFirmado", "S");
                    lnCantidadFirmados = lnCantidadFirmados + 1;
                }
            } catch (Exception e) {

            }

        }
        LocalDateTime lFechaFin = LocalDateTime.now();
        lfArchivo.delete();
        ljRetorno.put("laLog", jsonLog);
        ljRetorno.put("liCantidadRegistros", lnCantidad);
        ljRetorno.put("liCantidadFirmados", lnCantidadFirmados);
        ljRetorno.put("lsFechaInicio", lFechaInicio);
        ljRetorno.put("lsFechaFin", lFechaFin);
        return ljRetorno;
    }

///METODO PARA FIRMAR CON ECLIPSOFT
    public static JSONObject firmarEclipsoft(ParametrosEntradaFirmas psEntrada) throws IOException, Exception {
        String lsMetodo = "Firmar";
        JSONObject ljRetorno = new JSONObject();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        File lfArchivo = null;
        //CargaDatosFirma         lCargaDatosFirma = new CargaDatosFirma();
        DatosFirma lDatos = new DatosFirma();
        ByteArrayOutputStream out;
        byte[] pbArchivo = null;
        String lsMensaje = "";
        String lsDocId = "";
        String lsArchivo = psEntrada.getPsRutaArchivo() + psEntrada.getPsNombreArchivo();
        String lsError = "0";
        //Información de la Firma
        lDatos.setPsUserName(psEntrada.getPsFirmaUsuario());      // getPsUserName());//firma
        lDatos.setPsPassWd(psEntrada.getPsFirmaClave());          //  getPsPassWd());
        lDatos.setPsPin(psEntrada.getPsFirmaPin());               // getPsPin());
        //fin Información de firma
        //Credendicales de Empresa para firma
        lDatos.setPsBillingUserName(psEntrada.getPsAplUsuario()); // getPsBillingUserName());//token
        lDatos.setPsBillingPassWd(psEntrada.getPsAplClave());     // getPsBillingPassWd());
        //Fin Credendicales de Empresa para firma
        lDatos.setPsPositionX1(Integer.toString(psEntrada.getPsPositionX1()));
        lDatos.setPsPositionY1(Integer.toString(psEntrada.getPsPositionY1()));
        lDatos.setPsPositionX2(Integer.toString(psEntrada.getPsPositionX2()));
        lDatos.setPsPositionY2(Integer.toString(psEntrada.getPsPositionY2()));
        lDatos.setPsNpage(psEntrada.getPsPagina());              // getPsNpage());
        lDatos.setPsLocation(psEntrada.getPsLocalidad());        // getPsLocation());
        lDatos.setPsInfoQR(psEntrada.getPsQRInfo());             // getPsInfoQR());
//System.out.println("getgetPsFormatoFirma>>> "+psEntrada.getPsFormatoFirma());
        lDatos.setPsParagraphFormat(psEntrada.getPsFormatoFirma());// getPsParagraphFormat());//

        lDatos.setPsImagenSizeX(psEntrada.getPsQRImagenX());    // *getPsImagenSizeX());//
        lDatos.setPsImagenSizeY(psEntrada.getPsQRImagenY());    // *getPsImagenSizeY());//

        lDatos.setPsReason(psEntrada.getPsRazon());

        if (psEntrada.getPsRutaArchivo().equals("")) {
            lsMensaje = "Ruta de destino no se encuentra configurado";
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "020");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            System.out.println("Error parametros >> " + ljRetorno.toJSONString());
            return ljRetorno;
        }

        lfArchivo = new File(psEntrada.getPsRutaArchivo());
        if (!lfArchivo.isDirectory()) {
            lsMensaje = "Ruta1 " + psEntrada.getPsRutaArchivo() + " no existe";
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "100");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            return ljRetorno;
        }
        lfArchivo = new File(lsArchivo);

        if (!lfArchivo.isFile()) {
            lsMensaje = "Archivo " + psEntrada.getPsNombreArchivo() + " no existe";
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "200");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            return ljRetorno;
        }

        //Genera Token
        EntradaToken lEntradaToken = new EntradaToken();
        lEntradaToken.setPsUserName(lDatos.getPsBillingUserName());
        lEntradaToken.setPsPassword(lDatos.getPsBillingPassWd());
        lEntradaToken.setPsAssociate("");
        JSONObject ljToken = Token(lEntradaToken);

        if (!ljToken.get("lsCodError").toString().equals("0")) {
            lsMensaje = ljToken.get("lsMensaje").toString();
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "250");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            return ljRetorno;
        }

        out = generarQR.generarImagenQR(lDatos.getPsInfoQR());

        String qrCodeString = Base64.getEncoder().encodeToString(out.toByteArray());//QR firma        

        if (psEntrada.getPsUbicacion().equals("L")) {//toma el archivo desde el servidor
            pbArchivo = FileUtils.readFileToByteArray(lfArchivo);
        } //else {//toma el archivo por parametro (b64)
        //  pbArchivo = Base64.getDecoder().decode(psEntrada.getPsArchivoB64());
        //}

        try {

            /* System.out.println("webhookId" + psEntrada.getPsNombreArchivo());
            System.out.println("image" + qrCodeString);
            System.out.println("pin" + lDatos.getPsPin());
            System.out.println("reason" + lDatos.getPsReason());
            System.out.println("location" + lDatos.getPsLocation());
            System.out.println("position" + lDatos.getPsPositionX1() + "," + lDatos.getPsPositionY1() + "," + lDatos.getPsPositionX2() + "," + lDatos.getPsPositionY2());
            System.out.println("npage" + lDatos.getPsNpage());
            System.out.println("username" + lDatos.getPsUserName());
            System.out.println("password" + lDatos.getPsPassWd());
            System.out.println("paragraphFormat" + lDatos.getPsParagraphFormat());
            System.out.println("img_size" + lDatos.getPsImagenSizeX() + "," + lDatos.getPsImagenSizeY());
             */
            //System.out.println("getPsParagraphFormat>>> "+lDatos.getPsParagraphFormat());
            RequestBody fileBody = RequestBody.create(pbArchivo, MediaType.parse("application/pdf"));
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("fileIn", psEntrada.getPsNombreArchivo(), fileBody)
                    .addFormDataPart("webhookId", psEntrada.getPsNombreArchivo())
                    .addFormDataPart("image", qrCodeString)
                    .addFormDataPart("pin", lDatos.getPsPin())
                    .addFormDataPart("reason", lDatos.getPsReason())
                    .addFormDataPart("location", lDatos.getPsLocation())
                    .addFormDataPart("position", lDatos.getPsPositionX1() + "," + lDatos.getPsPositionY1() + "," + lDatos.getPsPositionX2() + "," + lDatos.getPsPositionY2())
                    .addFormDataPart("npage", lDatos.getPsNpage())
                    .addFormDataPart("username", lDatos.getPsUserName())
                    .addFormDataPart("password", lDatos.getPsPassWd())
                    .addFormDataPart("paragraphFormat", lDatos.getPsParagraphFormat())
                    .addFormDataPart("img_size", lDatos.getPsImagenSizeX() + "," + lDatos.getPsImagenSizeY())
                    .build();

            Request request = new Request.Builder().url(ParametrosGlobales.getURL_API_SING())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("Authorization", "Bearer " + ljToken.get("id_token"))
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                lsMensaje = "Error al Firmar el Documento. " + response.toString();
                ljRetorno.put("lsMetodo", lsMetodo);
                ljRetorno.put("lsCodError", "300");
                ljRetorno.put("lsMensaje", lsMensaje);
                ljRetorno.put("lsError", lsMensaje);
                ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
                ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
                ljRetorno.put("lsDoctoId", "");
                System.out.println("Error firmar >> " + ljRetorno.toJSONString());
            }
            String lsResponseFirma = response.body().string();

            JSONParser parser = new JSONParser();
            JSONObject ljResponseFirma = (JSONObject) parser.parse(lsResponseFirma);
            String lsResult = "";
            String lsDetail = "";
            String lsStatus = "";
            String lsWebHookTxt = "";
            String lsWebHookPdf = "";
            try {
                lsResult = ljResponseFirma.get("result").toString();
            } catch (Exception e) {
                lsResult = "false";
            }
            try {
                lsDetail = ljResponseFirma.get("detail").toString();
                if (ljResponseFirma.get("detail").toString().split("&").length < 2) {
                    lsDetail = ljResponseFirma.get("detail").toString().split("=")[1];
                    lsError = "0";
                }

            } catch (Exception e) {
                lsDetail = "";
            }
            try {
                lsStatus = ljResponseFirma.get("status").toString();
            } catch (Exception e) {
                lsStatus = "";
            }
            try {
                lsWebHookTxt = ljResponseFirma.get("webhookTxt").toString();
            } catch (Exception e) {
                lsWebHookTxt = "";
            }
            try {
                lsWebHookPdf = ljResponseFirma.get("webhookPdf").toString();
            } catch (Exception e) {
                lsWebHookPdf = "";
            }

            if (lsResult.equals("false")) {
                lsMensaje = "Error al Firmar el Documento. " + lsStatus + " - " + lsDetail;
                System.out.println("false >> " + lsMensaje);
                ljRetorno.put("lsMetodo", lsMetodo);
                ljRetorno.put("lsCodError", "350");
                ljRetorno.put("lsMensaje", lsMensaje);
                ljRetorno.put("lsError", lsMensaje);
                ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
                ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
                ljRetorno.put("lsDoctoId", lsDetail);
                out.close();
                response.close();
                return ljRetorno;
            }

            if (!lsStatus.equals("200 Ok")) {
                lsMensaje = "Error al Firmar el Documento. " + lsStatus + " - " + lsDetail;
                ljRetorno.put("lsMetodo", lsMetodo);
                ljRetorno.put("lsCodError", "360");
                ljRetorno.put("lsMensaje", lsMensaje);
                ljRetorno.put("lsError", lsMensaje);
                ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
                ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
                ljRetorno.put("lsDoctoId", lsDetail);
                out.close();
                response.close();
                return ljRetorno;

            }

            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "0");
            ljRetorno.put("lsMensaje", "Documento Firmado");
            ljRetorno.put("lsError", "Documento Firmado");
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            ljRetorno.put("lsWebhookTxt", lsWebHookTxt);
            ljRetorno.put("lsWebhookPdf", lsWebHookPdf);
            ljRetorno.put("lsDoctoId", lsDetail);
            out.close();
            response.close();
            return ljRetorno;

        } catch (IOException | ParseException x) {
            lsMensaje = "Error al Firmar el Documento. " + x.getMessage();
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "999");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            System.out.println("Error firmar >> " + ljRetorno.toJSONString());
        }
        out.close();
        return ljRetorno;

    }

//Método que genera el Token para firmar el documento en eclipsoft
    public static JSONObject Token(EntradaToken pEntradaToken) {
        JSONObject ljRetorno = new JSONObject();
        String lsResponseToken = "";
        String lsCodError = "";
        String lsMensaje = "";
        /**
         * **************************************************************
         */
        //Gson gson = new Gson();
        //String lEntradaToken = gson.toJson(pEntradaToken);
        String lsParametrosToken = "{"
                + "\"username\": \"" + pEntradaToken.getPsUserName() + "\","
                + " \"password\": \"" + pEntradaToken.getPsPassword() + "\","
                + "\"associate\":\"" + pEntradaToken.getPsAssociate() + "\""
                + "}";

        JSONParser lJsonParser = new JSONParser();
        Response lResponse = null;
        try {
            RequestBody lBody = RequestBody.create(MediaType.parse("application/json"), lsParametrosToken);
            OkHttpClient lClient = new OkHttpClient().newBuilder().build();
            Request lRequest = new Request.Builder().url(ParametrosGlobales.getURL_API_TOKEN())
                    .post(lBody)
                    .build();
            lResponse = lClient.newCall(lRequest).execute();
            if (lResponse.isSuccessful()) {
                lsResponseToken = lResponse.body().string();
                ljRetorno = (JSONObject) lJsonParser.parse(lsResponseToken);
                ljRetorno.put("lsMensaje", "OK");
                ljRetorno.put("lsCodError", "0");
                ljRetorno.put("lsEstado", "Token");
            } else {
                lsResponseToken = lResponse.body().string();
                ljRetorno = (JSONObject) lJsonParser.parse(lsResponseToken);
                lsMensaje = ljRetorno.get("title").toString() + " - " + ljRetorno.get("detail").toString();
                lsCodError = ljRetorno.get("status").toString();
                ljRetorno = new JSONObject();
                ljRetorno.put("lsMensaje", lsMensaje);
                ljRetorno.put("lsCodError", lsCodError);
                ljRetorno.put("lsEstado", "Token");
            }
        } catch (Exception e) {
            lsMensaje = e.getMessage();
            ljRetorno = new JSONObject();
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            ljRetorno.put("lsCodError", "200");
            ljRetorno.put("lsEstado", "Token");

        }
        lResponse.close();
        /**
         * **************************************************************
         */
        return ljRetorno;
    }

//Descarga el archivo firmado
    public static JSONObject descargarArchivoFirmado(EntradaArchivoFirmado psEntrada) throws IOException, Exception {

        JSONObject ljRetorno = new JSONObject();
        String lsMetodo = "descargarArchivoFirmado";
        String lsDocId = "";
        String lsDetail = "";
        String lsError = "";
        String lsMensaje = "";
        try {
            String ficheroDestino = "";
            //URL ficheroUrl = new URL(psEntrada.getPsWebHookTxt());
            //System.out.println(psEntrada.getPsWebHookTxt());
            //System.out.println(psEntrada.getPsWebHookPdf());
            //Se lee el archivo de información de la ejecución
            BufferedInputStream inputStream = new BufferedInputStream(new URL(psEntrada.getPsWebHookTxt()).openStream());
            byte data[] = new byte[1024];
            int lnCantidadBytes = 0;
            String lsRespuestaFirma = null;
            //System.out.println("DATA>>> "+data.toString());
            while ((lnCantidadBytes = inputStream.read(data)) != -1) {
                lsRespuestaFirma += new String(data, 0, lnCantidadBytes);
            }
            inputStream.close();
            String lsException = "";
            String lsMessage = "";
            String[] lsMensajeError;
            if (lsRespuestaFirma == null) {
                ljRetorno.put("lsMetodo", lsMetodo);
                ljRetorno.put("lsCodError", "999");
                ljRetorno.put("lsMensaje", "SIN RESPUESTA");
                ljRetorno.put("lsError", "SIN RESPUESTA");
                ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
                ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
                return ljRetorno;

            }

            //System.out.println("1: " + lsRespuestaFirma);
            if (lsRespuestaFirma.contains("exception")) {
                //Si genera error se descompone para obtener los mensajes
                lsMensajeError = lsRespuestaFirma.split("&");
                for (int x = 0; x < lsMensajeError.length; x++) {
                    if (lsMensajeError[x].split("=")[0].equals("exception")) {
                        lsException = lsMensajeError[x].split("=")[1];
                    } else {
                        if (lsMensajeError[x].split("=")[0].equals("message")) {
                            lsMessage = lsMensajeError[x].split("=")[1];
                        } else {
                            lsDocId = lsMensajeError[x].split("=")[1];
                            lsDetail = lsDocId;
                        }
                    }
                }
                lsError = "1";
            } else {
                //sin error
                if (lsRespuestaFirma.split("&").length < 2) {
                    lsDocId = lsRespuestaFirma.split("=")[1];
                    lsDetail = lsDocId;
                    lsError = "0";
                }

            }

            if (!lsError.equals("0")) {
                lsMensaje = "Error al Firmar el Documento. " + lsException + " - " + lsMessage;
                ljRetorno.put("lsMetodo", lsMetodo);
                ljRetorno.put("lsCodError", "360");
                ljRetorno.put("lsMensaje", lsMensaje);
                ljRetorno.put("lsError", lsMensaje);
                ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
                ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
                ljRetorno.put("lsDoctoId", lsDocId);
                return ljRetorno;
            }

            OkHttpClient lClientPDFFirm = new OkHttpClient().newBuilder().build();
            Request lRequestPDFFirm = new Request.Builder().url(psEntrada.getPsWebHookPdf()).build();
            Response lResponsePDFFirm = lClientPDFFirm.newCall(lRequestPDFFirm).execute();

            if (lResponsePDFFirm.isSuccessful()) {
                byte[] buffered = lResponsePDFFirm.body().bytes();
                File docSigned = new File(psEntrada.getPsRutaArchivo() + psEntrada.getPsNombreArchivo());
                OutputStream outStream = new FileOutputStream(docSigned);
                outStream.write(buffered);
                IOUtils.closeQuietly(outStream);
            } else {
                System.out.println("error >> " + lResponsePDFFirm.toString());
            }
            lResponsePDFFirm.close();

            //lsMensaje  = "Documento. "+response.toString();
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsMensaje", "Documento Descargado");
            ljRetorno.put("lsCodError", "0");
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            ljRetorno.put("lsWebhookTxt", psEntrada.getPsWebHookTxt());
            ljRetorno.put("lsWebhookPdf", psEntrada.getPsWebHookPdf());
            ljRetorno.put("lsDoctoId", lsDetail);

            //out.close();
            //response.close();
            //return ljRetorno;
        } catch (Exception x) {
            lsMensaje = "Error al Firmar el Documento. " + x.toString();
            ljRetorno.put("lsMetodo", lsMetodo);
            ljRetorno.put("lsCodError", "999");
            ljRetorno.put("lsMensaje", lsMensaje);
            ljRetorno.put("lsError", lsMensaje);
            ljRetorno.put("lsNombre", psEntrada.getPsNombreArchivo());
            ljRetorno.put("lsRuta", psEntrada.getPsRutaArchivo());
            System.out.println("Error firmar >> " + ljRetorno.toJSONString());
        }

        return ljRetorno;
    }

}
