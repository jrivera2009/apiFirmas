
package io.rubrica.sign.pdf;

import com.lowagie.text.Element;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTemplate;
import com.yoveri.apiFirmas.dto.ParametrosEntradaFirmas;
import io.rubrica.certificate.CertEcUtils;

import io.rubrica.exceptions.RubricaException;
import io.rubrica.exceptions.InvalidFormatException;
import io.rubrica.sign.SignInfo;
import io.rubrica.sign.Signer;
import io.rubrica.certificate.to.DatosUsuario;
import io.rubrica.utils.BouncyCastleUtils;
import io.rubrica.utils.FileUtils;
import io.rubrica.utils.Utils;

public class PDFSigner implements Signer {

    private static final Logger logger = Logger.getLogger(PDFSigner.class.getName());

    private static final String PDF_FILE_HEADER = "%PDF-";
    private static final PdfName PDFNAME_ETSI_RFC3161 = new PdfName("ETSI.RFC3161");
    private static final PdfName PDFNAME_DOCTIMESTAMP = new PdfName("DocTimeStamp");
    public static final String SIGNING_REASON = "signingReason";
    public static final String SIGNING_LOCATION = "signingLocation";
    public static final String SIGN_TIME = "signTime";
    public static final String SIGNATURE_PAGE = "signingPage";
    public static final String LAST_PAGE = "0";
    public static final String FONT_SIZE = "3";
    public static final String TYPE_SIG = "information1";
    public static final String INFO_QR = "";

    static {
        BouncyCastleUtils.initializeBouncyCastle();
    }
    
    
       @Override
    public byte[] sign(byte[] data, String algorithm, PrivateKey key, Certificate[] certChain, Properties xParams)
            throws RubricaException, IOException, BadPasswordException {

        Properties extraParams = xParams != null ? xParams : new Properties();

        X509Certificate x509Certificate = (X509Certificate) certChain[0];

        // Motivo de la firma
        String reason = extraParams.getProperty(SIGNING_REASON);

        // Lugar de realizacion de la firma
        String location = extraParams.getProperty(SIGNING_LOCATION);

        // Fecha y hora de la firma, en formato ISO-8601
        String signTime = extraParams.getProperty(SIGN_TIME);

        // Tamaño letra
        float fontSize = 3;
        try {
            if (extraParams.getProperty(FONT_SIZE) == null) {
                fontSize = 3;
            } else {
                fontSize = Float.parseFloat(extraParams.getProperty(FONT_SIZE).trim());
            }
        } catch (final Exception e) {
            logger.warning("Se ha indicado un tamaño de letra invalida ('" + extraParams.getProperty(FONT_SIZE)
                    + "'), se usara el tamaño por defecto: " + fontSize + " " + e);
        }

        // Tipo de firma (Información, QR)
        String typeSig = extraParams.getProperty(TYPE_SIG);
        if (typeSig == null) {
            typeSig = "information1";
        }

        if (typeSig.equals("QR") && extraParams.getProperty(FONT_SIZE) == null) {
            fontSize = 4.5f;
        }

        // Información QR
        String infoQR = "";
        if (extraParams.getProperty(INFO_QR) == null) {
            infoQR = "";
        } else {
            infoQR = extraParams.getProperty(INFO_QR).trim();
        }

        // Tamaño espaciado
        float fontLeading = fontSize;

        // Pagina donde situar la firma visible
        int page = 0;
        try {
            if (extraParams.getProperty(LAST_PAGE) == null) {
                page = 0;
            } else {
                page = Integer.parseInt(extraParams.getProperty(LAST_PAGE).trim());
            }
        } catch (final Exception e) {
            logger.warning("Se ha indicado un numero de pagina invalido ('" + extraParams.getProperty(LAST_PAGE)
                    + "'), se usara la ultima pagina: " + e);
        }

        // Leer el PDF
        PdfReader pdfReader = new PdfReader(data);
        if (pdfReader.isEncrypted()) {
            logger.severe("Documento encriptado");
            throw new RubricaException("Documento encriptado");
        }
        
        
              
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfStamper stp;
        try {
            stp = PdfStamper.createSignature(pdfReader, baos, '\0', null, true);
        } catch (com.lowagie.text.DocumentException e) {
            logger.severe("Error al crear la firma para estampar: " + e);
            throw new RubricaException("Error al crear la firma para estampar", e);
        }

        PdfSignatureAppearance sap = stp.getSignatureAppearance();
        sap.setAcro6Layers(true);
        System.out.println("validar posicion");

        // Razon de firma
        if (reason != null) {
            sap.setReason(reason);
        }

        // Localización en donde se produce la firma
        if (location != null) {
            sap.setLocation(location);
        }

        // Fecha y hora de la firma
        if (signTime != null) {
            Date date = Utils.getSignTime(signTime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            sap.setSignDate(calendar);
        }

        if (page == 0 || page < 0 || page > pdfReader.getNumberOfPages()) {
            page = pdfReader.getNumberOfPages();
        }
        page=0;
        
       Rectangle org= pdfReader.getPageSize(1);
         //  System.out.println("getHeight"+org.getHeight());
        //   System.out.println("getWidth"+org.getWidth());
        //   System.out.println("getTop"+org.getTop());
        //   System.out.println("getBottom"+org.getBottom());
Rectangle signaturePositionOnPage = getSignaturePositionOnPage(extraParams);

  /*
            System.out.println("**********************************************");
            System.out.println("getHeight"+signaturePositionOnPage.getHeight());
            System.out.println("getWidth"+signaturePositionOnPage.getWidth());
            System.out.println("getTop"+signaturePositionOnPage.getTop());
            System.out.println("getBottom"+signaturePositionOnPage.getBottom());  
            */
            
            System.out.println("getLeft"+signaturePositionOnPage.getLeft());
            System.out.println("getRight"+signaturePositionOnPage.getRight());  
             System.out.println("getRight"+signaturePositionOnPage);  
            Rectangle rectanguloPararFirmar = RectanguloParaFirmar.obtenerRectangulo(pdfReader.getPageSize(1),
                    20, 110);
        if (signaturePositionOnPage != null) {
            /*
              // Top left
  Rectangle rectangle = new Rectangle(cropBox.getLeft(), cropBox.getTop(height),
                              cropBox.getLeft(width), cropBox.getTop());

    // Top right
    rectangle = new Rectangle(cropBox.getRight(width), cropBox.getTop(height),
                              cropBox.getRight(), cropBox.getTop());

    // Bottom left
    rectangle = new Rectangle(cropBox.getLeft(), cropBox.getBottom(),
                              cropBox.getLeft(width), cropBox.getBottom(height));
*/
    // Bottom right
   Rectangle rectangle = new Rectangle(org.getRight(110), org.getBottom(),
                              org.getRight(), org.getBottom(36));
            
            System.out.println("validar posicion1ok");
            System.out.println(signaturePositionOnPage);
            System.out.println(page);
           // sap.setVisibleSignature(signaturePositionOnPage, page, null);
            //sap.setVisibleSignature(rectanguloPararFirmar, page, null);
           sap.setVisibleSignature(rectangle, 1, "sig"); 
            //sap.setVisibleSignature(new Rectangle(org.getHeight(), org.getWidth(), org.getTop(), org.getBottom()), 1, "sig"); 
            String informacionCertificado = x509Certificate.getSubjectDN().getName();
            DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
            String nombreFirmante = (datosUsuario.getNombre() + " " + datosUsuario.getApellido()).toUpperCase();
            try {
                // Creating the appearance for layer 0
                PdfTemplate pdfTemplate = sap.getLayer(0);
                float width = pdfTemplate.getBoundingBox().getWidth();
                float height = pdfTemplate.getBoundingBox().getHeight();
                pdfTemplate.rectangle(0, 0, width, height);
                // Color de fondo
                // pdfTemplate.setColorFill(Color.LIGHT_GRAY);
                // pdfTemplate.fill();
                // Color de fondo 
                System.out.println("typeSig"+typeSig);
                switch (typeSig) {
                    
                    case "QR": {
                        // Creating the appearance for layer 2
                        // Nombre Firmante
                        // nombreFirmante = nombreFirmante+" "+nombreFirmante;
                        // nombreFirmante="PRUEBA QUIPUX MISAEL FERNANDEZ";
                        //nombreFirmante = "RAUL JAVIER JARA INIGUEZ";
                        // nombreFirmante = "PRUEBA MV PRUEBA F PRUEBA C";
                         nombreFirmante="Fernado Rivera";
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);
                        Font font = new Font(Font.COURIER, fontSize + (fontSize / 2), Font.BOLD, Color.BLACK);
                        float maxFontSize = getMaxFontSize(com.lowagie.text.pdf.BaseFont.createFont(),
                                nombreFirmante.trim(), width - ((width / 3) + 3));
                        font.setSize(maxFontSize);
                        fontLeading = maxFontSize;

                        Paragraph paragraph = new Paragraph("Firmado electrónicamente por:\n",
                                new Font(Font.COURIER, fontSize / 1.25f, Font.NORMAL, Color.BLACK));
                        paragraph.add(new Paragraph(nombreFirmante.trim(), font));
                        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
                        paragraph.setLeading(fontLeading);
                        ColumnText columnText = new ColumnText(pdfTemplate1);
                        columnText.setSimpleColumn((width / 3) + 3, 0, width, height);
                        columnText.addElement(paragraph);
                        columnText.go();
                        // Imagen
                        java.awt.image.BufferedImage bufferedImage = null;
                        // QR
                        String text = "FIRMADO POR: " + nombreFirmante.trim() + "\n";
                        text = text + "RAZON: " + reason + "\n";
                        text = text + "LOCALIZACION: " + location + "\n";
                        text = text + "FECHA: " + signTime + "\n";
                        text = text + infoQR;
                        try {
                            bufferedImage = io.rubrica.utils.QRCode.generateQR(text, (int) height, (int) height);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // QR
                        PdfTemplate pdfTemplateImage = sap.getLayer(2);
                        ColumnText columnTextImage = new ColumnText(pdfTemplateImage);
                        columnTextImage.setSimpleColumn(0, 0, width / 3, height);
                        columnTextImage.setAlignment(Paragraph.ALIGN_CENTER);
                        columnTextImage.addElement(com.lowagie.text.Image.getInstance(bufferedImage, null));
                        columnTextImage.go();
                        break;
                    }
                    case "information1": {
                        // Creating the appearance for layer 2
                        // Nombre Firmante
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);
                        Font font1 = new Font(Font.ITALIC, fontSize + (fontSize / 2), Font.BOLD, Color.BLACK);
                        // Font font1 = new Font(Font.ITALIC, 5.0f, Font.BOLD, Color.BLACK);
                        Paragraph paragraph1 = new Paragraph(nombreFirmante.trim(), font1);
                        paragraph1.setAlignment(Paragraph.ALIGN_RIGHT);
                        ColumnText columnText1 = new ColumnText(pdfTemplate1);
                        columnText1.setSimpleColumn(0, 0, (width / 2) - 1, height);
                        columnText1.addElement(paragraph1);
                        columnText1.go();
                        // Segunda Columna
                        PdfTemplate pdfTemplate2 = sap.getLayer(2);
                        Font font2 = new Font(Font.ITALIC, fontSize, Font.NORMAL, Color.DARK_GRAY);
                        Paragraph paragraph2 = new Paragraph(fontLeading, "Nombre de reconocimiento "
                                + informacionCertificado.trim() + "\nRazón: " + reason + "\nLocalización: " + location + "\nFecha: " + signTime, font2);
                        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
                        ColumnText columnText2 = new ColumnText(pdfTemplate2);
                        columnText2.setSimpleColumn((width / 2) + 1, 0, width, height);
                        columnText2.addElement(paragraph2);
                        columnText2.go();
                        break;
                    }
                    case "information2": {
                        // Creating the appearance for layer 2
                        // ETSI TS 102 778-6 V1.1.1 (2010-07)
                        Font font = new Font(Font.HELVETICA, fontSize, Font.BOLD, Color.BLACK);
                        com.lowagie.text.pdf.BaseFont baseFont = com.lowagie.text.pdf.BaseFont.createFont();

                        float x = Float.parseFloat(extraParams.getProperty("PositionOnPageLowerLeftX").trim());
                        float y = Float.parseFloat(extraParams.getProperty("PositionOnPageLowerLeftY").trim());
                        nombreFirmante = nombreFirmante.replace(" ", "*");
                        width = baseFont.getWidthPoint(nombreFirmante, font.getSize());
                        nombreFirmante = nombreFirmante.replace("*", " ");
                        height = font.getSize() * 5;
                        sap.setVisibleSignature(new Rectangle(x, y, x + width, y - height), page, null);
                        pdfTemplate = sap.getLayer(0);
                        pdfTemplate.rectangle(0, 0, width, height);
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);

                        Paragraph paragraph = new Paragraph(fontLeading, "Firmado digitalmente por:\n",
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK));
                        paragraph.add(new Paragraph(fontLeading, nombreFirmante, font));
                        paragraph.add(new Paragraph(fontLeading, "Razón: " + reason,
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK)));
                        paragraph.add(new Paragraph(fontLeading, "Localización: " + location,
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK)));
                        paragraph.add(new Paragraph(fontLeading, "Fecha: " + signTime,
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK)));
                        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
                        ColumnText columnText = new ColumnText(pdfTemplate1);
                        columnText.setSimpleColumn(0, 0, width, height);
                        columnText.addElement(paragraph);
                        columnText.go();
                        break;
                    }
                    default: {
                    }
                }
            } catch (com.lowagie.text.DocumentException de) {
                logger.severe("Error al estampar la firma: " + de);
                throw new RubricaException("Error al estampar la firma", de);
            }
        }

        sap.setCrypto(key, x509Certificate, null, PdfSignatureAppearance.WINCER_SIGNED);

        try {
            stp.close();
        } catch (com.lowagie.text.ExceptionConverter ec) {
            logger.severe("Problemas con el driver\n" + ec);
            throw new RubricaException(io.rubrica.utils.PropertiesUtils.getMessages().getProperty("mensaje.error.driver_problemas") + "\n", ec);
        } catch (com.lowagie.text.DocumentException | com.lowagie.text.exceptions.InvalidPdfException de) {
            logger.severe("Error al estampar la firma\n" + de);
            throw new RubricaException("Error al estampar la firma\n", de);
        }

        return baos.toByteArray();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    

    // ETSI TS 102 778-1 V1.1.1 (2009-07)
    // PAdES Basic - Profile based on ISO 32000-1

    @Override
    public byte[] sign(byte[] data, String algorithm, PrivateKey key, Certificate[] certChain,
            Properties xParams,
            ParametrosEntradaFirmas p_parametros)
            throws RubricaException, IOException, BadPasswordException {

        Properties extraParams = xParams != null ? xParams : new Properties();

        X509Certificate x509Certificate = (X509Certificate) certChain[0];

        // Motivo de la firma
        String reason = p_parametros.getPsRazon();

        // Lugar de realizacion de la firma
        String location = p_parametros.getPsLocalidad();

        // Fecha y hora de la firma, en formato ISO-8601
        String signTime = extraParams.getProperty(SIGN_TIME);

        // Tamaño letra
        float fontSize = 3;
        try {
            if (extraParams.getProperty(FONT_SIZE) == null) {
                fontSize = 3;
            } else {
                fontSize = Float.parseFloat(extraParams.getProperty(FONT_SIZE).trim());
            }
        } catch (final Exception e) {
            logger.warning("Se ha indicado un tamaño de letra invalida ('" + extraParams.getProperty(FONT_SIZE)
                    + "'), se usara el tamaño por defecto: " + fontSize + " " + e);
        }

        // Tipo de firma (Información, QR)
        String typeSig = extraParams.getProperty(TYPE_SIG);
        if (typeSig == null) {
            typeSig = "information1";
        }

        if (typeSig.equals("QR") && extraParams.getProperty(FONT_SIZE) == null) {
            fontSize = 4.5f;
        }

        // Información QR
        String infoQR = "";
        if (extraParams.getProperty(INFO_QR) == null) {
            infoQR = "";
        } else {
            infoQR = extraParams.getProperty(INFO_QR).trim();
        }

        // Tamaño espaciado
        float fontLeading = fontSize;

        // Pagina donde situar la firma visible
        int page = 0; 
        try {
           page= Integer.parseInt(p_parametros.getPsPagina());
        } catch (final Exception e) {
page = 0; 
        }

        // Leer el PDF
        PdfReader pdfReader = new PdfReader(data);
        if (pdfReader.isEncrypted()) {
            logger.severe("Documento encriptado");
            throw new RubricaException("Documento encriptado");
        }
        
        
              
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfStamper stp;
        try {
            stp = PdfStamper.createSignature(pdfReader, baos, '\0', null, true);
        } catch (com.lowagie.text.DocumentException e) {
            logger.severe("Error al crear la firma para estampar: " + e);
            throw new RubricaException("Error al crear la firma para estampar", e);
        }

        PdfSignatureAppearance sap = stp.getSignatureAppearance();
        sap.setAcro6Layers(true);

        // Razon de firma
        if (reason != null) {
            sap.setReason(reason);
        }

        // Localización en donde se produce la firma
        if (location != null) {
            sap.setLocation(location);
        }

        // Fecha y hora de la firma
        if (signTime != null) {
            Date date = Utils.getSignTime(signTime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            sap.setSignDate(calendar);
        }

        if (page == 0 || page < 0 || page > pdfReader.getNumberOfPages()) {
            page = pdfReader.getNumberOfPages();
        }
        
       Rectangle org= pdfReader.getPageSize(1);
      /* System.out.println("getHeight"+org.getHeight());
       System.out.println("getWidth"+org.getWidth());
       System.out.println("getTop"+org.getTop());
       System.out.println("getBottom"+org.getBottom());
*/
     
       
       Rectangle signaturePositionOnPage = getSignaturePositionOnPage(extraParams);

  /*
            System.out.println("**********************************************");
            System.out.println("getHeight"+signaturePositionOnPage.getHeight());
            System.out.println("getWidth"+signaturePositionOnPage.getWidth());
            System.out.println("getTop"+signaturePositionOnPage.getTop());
            System.out.println("getBottom"+signaturePositionOnPage.getBottom());  
            */
            
            /*System.out.println("getLeft"+signaturePositionOnPage.getLeft());
            System.out.println("getRight"+signaturePositionOnPage.getRight());  
            System.out.println("getRight"+signaturePositionOnPage);  
  */
          /*  Rectangle rectanguloPararFirmar = RectanguloParaFirmar.obtenerRectangulo(pdfReader.getPageSize(1),
                    20, 110);*/
        if (signaturePositionOnPage != null) {
            /*
              // Top left
  Rectangle rectangle = new Rectangle(cropBox.getLeft(), cropBox.getTop(height),
                              cropBox.getLeft(width), cropBox.getTop());

    // Top right
    rectangle = new Rectangle(cropBox.getRight(width), cropBox.getTop(height),
                              cropBox.getRight(), cropBox.getTop());

    // Bottom left
   
*/       
         /*
         System.out.println("aaaa");
         System.out.println(org.getLeft());
         System.out.println(org.getRight());
         System.out.println(org.getTop());
         System.out.println(org.getBottom());
         Rectangle   rectangle = new Rectangle(org.getLeft(), org.getBottom(),org.getLeft(110), org.getBottom(36));
         System.out.println("bbbbbbb");
         System.out.println(rectangle.getLeft());
         System.out.println(rectangle.getRight());
         System.out.println(rectangle.getTop());
         System.out.println(rectangle.getBottom());
         rectangle = new Rectangle(org.getRight(110), org.getBottom(15),org.getRight(), org.getBottom(36));
  
         System.out.println("ccccc");
         System.out.println(rectangle.getLeft());
         System.out.println(rectangle.getRight());
         System.out.println(rectangle.getTop());
         System.out.println(rectangle.getBottom());  
         System.out.println("validar posicion1ok");
         System.out.println(signaturePositionOnPage);
         System.out.println(page);
         */
         
          //Rectangle rectangle = new Rectangle(org.getRight(110), org.getBottom(15),org.getRight(), org.getBottom(36));
          //E
           //Rectangle rectangle = new Rectangle(org.getRight(230-10), org.getBottom(120),org.getRight(380-10), org.getBottom(185));
           Rectangle rectangle = new Rectangle(org.getRight(p_parametros.getPsPositionX1()-10), org.getBottom(p_parametros.getPsPositionY1()),org.getRight(p_parametros.getPsPositionX2()-10), org.getBottom(p_parametros.getPsPositionY2()));
           sap.setVisibleSignature(rectangle, page, "sig"); 
            //sap.setVisibleSignature(new Rectangle(org.getHeight(), org.getWidth(), org.getTop(), org.getBottom()), 1, "sig"); 
            String informacionCertificado = x509Certificate.getSubjectDN().getName();
            DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
            String nombreFirmante = (datosUsuario.getNombre() + " " + datosUsuario.getApellido()).toUpperCase();
            try {
                // Creating the appearance for layer 0
                PdfTemplate pdfTemplate = sap.getLayer(0);
                float width = pdfTemplate.getBoundingBox().getWidth();
                float height = pdfTemplate.getBoundingBox().getHeight();
                //System.out.println("width"+width);
               // System.out.println("height"+height);
                pdfTemplate.rectangle(0, 0, width, height);
                // Color de fondo
                // pdfTemplate.setColorFill(Color.LIGHT_GRAY);
                // pdfTemplate.fill();
                // Color de fondo 
              //  System.out.println("typeSig"+typeSig);
                switch (typeSig) {
                    
                    case "QR": {
                        // Creating the appearance for layer 2
                        // nombreFirmante = "PRUEBA";
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);
                        Font font = new Font(Font.COURIER, fontSize + (fontSize / 2), Font.BOLD, Color.BLACK);
                        float maxFontSize = getMaxFontSize(com.lowagie.text.pdf.BaseFont.createFont(),
                                nombreFirmante.trim(), width - ((width / 3) + 3));
                        font.setSize(3);
                       // System.out.println("maxFontSize:"+maxFontSize);
                        fontLeading = maxFontSize;
                        Paragraph paragraph = new Paragraph("Firmado electrónicamente por:\n",
                                new Font(Font.COURIER, fontSize / 1.25f, Font.NORMAL, Color.BLACK));
                        paragraph.add(new Paragraph(nombreFirmante.trim(), font));
                        // paragraph.add(new Paragraph("Consultor", font));
                        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
                        paragraph.setLeading(fontLeading);
                        
                        Paragraph parrafo = new Paragraph("\n\nFirmado electrónicamente por:\n",new Font(Font.COURIER, 4.1f, Font.NORMAL, Color.BLACK));
                        parrafo.setAlignment(Element.ALIGN_CENTER);
                        parrafo.setSpacingBefore(5);
                        parrafo.setIndentationLeft(0);
                        parrafo.setIndentationRight(0);
                        parrafo.setLeading(fontLeading);
                        
                        
                        ColumnText columnText = new ColumnText(pdfTemplate1);
                        columnText.setSimpleColumn(50, 10, width, height);
                        //columnText.addElement(paragraph);
                         columnText.addElement(parrafo);
                         
                        parrafo = new Paragraph(nombreFirmante.trim(),new Font(Font.COURIER, 4.85f, Font.BOLD, Color.BLACK));
                        parrafo.setAlignment(Element.ALIGN_CENTER);
                        parrafo.setSpacingBefore(2);
                        parrafo.setIndentationLeft(0);
                        parrafo.setIndentationRight(0);                        
                        columnText.addElement(parrafo);
                         
                        parrafo = new Paragraph(p_parametros.getPsCargo(),new Font(Font.COURIER, 4f, Font.BOLD, Color.BLACK));
                        parrafo.setAlignment(Element.ALIGN_CENTER);
                        parrafo.setSpacingBefore(1);
                        parrafo.setIndentationLeft(0);
                        parrafo.setIndentationRight(0);
                        
                        columnText.addElement(parrafo);
                        columnText.go();
                        // Imagen
                        java.awt.image.BufferedImage bufferedImage = null;
                        // QR
                        String textQR=p_parametros.getPsQRInfo();
  
                        if(textQR==null){
                        textQR = "FIRMADO POR: " + nombreFirmante.trim() + "\n";
                        textQR = textQR + "RAZON: " + reason + "\n";
                        textQR = textQR + "LOCALIZACION: " + location + "\n";
                        textQR = textQR + "FECHA: " + signTime + "\n";
                        textQR = textQR + infoQR;                            
                        }
      
                        try {
                            bufferedImage = io.rubrica.utils.QRCode.generateQR(textQR, (int) height, (int) height);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // QR
                        PdfTemplate pdfTemplateImage = sap.getLayer(2);
                        ColumnText columnTextImage = new ColumnText(pdfTemplateImage);
                        columnTextImage.setSimpleColumn(0, 0, 100, height);
                        columnTextImage.setAlignment(Paragraph.ALIGN_CENTER);
                        columnTextImage.addElement(com.lowagie.text.Image.getInstance(bufferedImage, null));
                        columnTextImage.go();
                        break;
                    }
                    case "information1": {
                        // Creating the appearance for layer 2
                        // Nombre Firmante
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);
                        Font font1 = new Font(Font.ITALIC, fontSize + (fontSize / 2), Font.BOLD, Color.BLACK);
                        // Font font1 = new Font(Font.ITALIC, 5.0f, Font.BOLD, Color.BLACK);
                        Paragraph paragraph1 = new Paragraph(nombreFirmante.trim(), font1);
                        paragraph1.setAlignment(Paragraph.ALIGN_RIGHT);
                        ColumnText columnText1 = new ColumnText(pdfTemplate1);
                        columnText1.setSimpleColumn(0, 0, (width / 2) - 1, height);
                        columnText1.addElement(paragraph1);
                        columnText1.go();
                        // Segunda Columna
                        PdfTemplate pdfTemplate2 = sap.getLayer(2);
                        Font font2 = new Font(Font.ITALIC, fontSize, Font.NORMAL, Color.DARK_GRAY);
                        Paragraph paragraph2 = new Paragraph(fontLeading, "Nombre de reconocimiento "
                                + informacionCertificado.trim() + "\nRazón: " + reason + "\nLocalización: " + location + "\nFecha: " + signTime, font2);
                        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
                        ColumnText columnText2 = new ColumnText(pdfTemplate2);
                        columnText2.setSimpleColumn((width / 2) + 1, 0, width, height);
                        columnText2.addElement(paragraph2);
                        columnText2.go();
                        break;
                    }
                    case "information2": {
                        // Creating the appearance for layer 2
                        // ETSI TS 102 778-6 V1.1.1 (2010-07)
                        Font font = new Font(Font.HELVETICA, fontSize, Font.BOLD, Color.BLACK);
                        com.lowagie.text.pdf.BaseFont baseFont = com.lowagie.text.pdf.BaseFont.createFont();

                        float x = Float.parseFloat(extraParams.getProperty("PositionOnPageLowerLeftX").trim());
                        float y = Float.parseFloat(extraParams.getProperty("PositionOnPageLowerLeftY").trim());
                        nombreFirmante = nombreFirmante.replace(" ", "*");
                        width = baseFont.getWidthPoint(nombreFirmante, font.getSize());
                        nombreFirmante = nombreFirmante.replace("*", " ");
                        height = font.getSize() * 5;
                        sap.setVisibleSignature(new Rectangle(x, y, x + width, y - height), page, null);
                        pdfTemplate = sap.getLayer(0);
                        pdfTemplate.rectangle(0, 0, width, height);
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);

                        Paragraph paragraph = new Paragraph(fontLeading, "Firmado digitalmente por:\n",
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK));
                        paragraph.add(new Paragraph(fontLeading, nombreFirmante, font));
                        paragraph.add(new Paragraph(fontLeading, "Razón: " + reason,
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK)));
                        paragraph.add(new Paragraph(fontLeading, "Localización: " + location,
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK)));
                        paragraph.add(new Paragraph(fontLeading, "Fecha: " + signTime,
                                new Font(Font.HELVETICA, fontSize / 1.5f, Font.NORMAL, Color.BLACK)));
                        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
                        ColumnText columnText = new ColumnText(pdfTemplate1);
                        columnText.setSimpleColumn(0, 0, width, height);
                        columnText.addElement(paragraph);
                        columnText.go();
                        break;
                    }
                    default: {
                    }
                }
            } catch (com.lowagie.text.DocumentException de) {
                logger.severe("Error al estampar la firma: " + de);
                throw new RubricaException("Error al estampar la firma", de);
            }
        }
        
        sap.setCrypto(key, x509Certificate, null, PdfSignatureAppearance.WINCER_SIGNED);

        try {
            stp.close();
        } catch (com.lowagie.text.ExceptionConverter ec) {
            logger.severe("Problemas con el driver\n" + ec);
            throw new RubricaException(io.rubrica.utils.PropertiesUtils.getMessages().getProperty("mensaje.error.driver_problemas") + "\n", ec);
        } catch (com.lowagie.text.DocumentException | com.lowagie.text.exceptions.InvalidPdfException de) {
            logger.severe("Error al estampar la firma\n" + de);
            throw new RubricaException("Error al estampar la firma\n", de);
        }

        return baos.toByteArray();
    }

    private float getMaxFontSize(com.lowagie.text.pdf.BaseFont baseFont, String text, float width) {
        float measureWidth = 1;
        float fontSize = 0.1f;
        float oldSize = 0.1f;
        int repeat = 0;
        float multiply = 1;
        text = text.replace(" ", "*");
        while (measureWidth < width) {
            repeat++;
            measureWidth = baseFont.getWidthPoint(text, fontSize);
            oldSize = fontSize;
            fontSize += 0.1f;
        }
        if (repeat > 60) {
            multiply = 1;
        }
        if (repeat <= 60 && repeat > 20) {
            multiply = 2;
        }
        if (repeat <= 20 && repeat > 10) {
            multiply = 3;
        }
        if (repeat <= 10) {
            multiply = 4;
        }

        if (fontSize > 20) {
            oldSize = 20;
            multiply = 1;
        }
        return oldSize * multiply;
    }

    @Override
    public List<SignInfo> getSigners(byte[] sign) throws InvalidFormatException, IOException {
        if (!isPdfFile(sign)) {
            throw new InvalidFormatException("El archivo no es un PDF");
        }

        com.itextpdf.kernel.pdf.PdfReader pdfReader;

        try {
            pdfReader = new com.itextpdf.kernel.pdf.PdfReader(FileUtils.byteArrayConvertToFile(sign));
        } catch (Exception e) {
            logger.severe("No se ha podido leer el PDF: " + e);
            throw new InvalidFormatException("No se ha podido leer el PDF", e);
        }

        com.itextpdf.signatures.SignatureUtil signatureUtil;

        try {
            com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(pdfReader);
            signatureUtil = new com.itextpdf.signatures.SignatureUtil(pdfDocument);
        } catch (Exception e) {
            logger.severe(
                    "No se ha podido obtener la informacion de los firmantes del PDF, se devolvera un arbol vacio: "
                    + e);
            throw new InvalidFormatException("No se ha podido obtener la informacion de los firmantes del PDF", e);
        }

        @SuppressWarnings("unchecked")
        List<String> names = signatureUtil.getSignatureNames();

        List<SignInfo> signInfos = new ArrayList<>();

        for (String signatureName : names) {
            com.itextpdf.signatures.PdfPKCS7 pdfPKCS7;

            try {
                pdfPKCS7 = signatureUtil.readSignatureData(signatureName);
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("El PDF contiene una firma corrupta o con un formato desconocido (" + signatureName
                        + "), se continua con las siguientes si las hubiese: " + e);
                continue;
            }

            Certificate[] signCertificateChain = pdfPKCS7.getSignCertificateChain();
            X509Certificate[] certChain = new X509Certificate[signCertificateChain.length];

            for (int i = 0; i < certChain.length; i++) {
                certChain[i] = (X509Certificate) signCertificateChain[i];
            }

            SignInfo signInfo = new SignInfo(certChain, pdfPKCS7.getSignDate().getTime());
            System.out.println("pdfPKCS7.getSignDate().getTime())"+pdfPKCS7.getSignDate().getTime());
            signInfos.add(signInfo);
        }

        return signInfos;
    }

    private boolean isPdfFile(final byte[] data) {

        byte[] buffer = new byte[PDF_FILE_HEADER.length()];

        try {
            new ByteArrayInputStream(data).read(buffer);
        } catch (Exception e) {
            buffer = null;
        }

        // Comprobamos que cuente con una cabecera PDF
        if (buffer != null && !PDF_FILE_HEADER.equals(new String(buffer))) {
            return false;
        }

        try {
            // Si lanza una excepcion al crear la instancia, no es un fichero
            // PDF
            new PdfReader(data);
        } catch (final Exception e) {
            return false;
        }

        return true;
    }

    private static Rectangle getSignaturePositionOnPage(Properties extraParams) {
        return PdfUtil.getPositionOnPage(extraParams);
    }
}
