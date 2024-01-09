/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.yoveri.firmar.pdf;

import java.security.*;
import java.security.cert.Certificate;
import java.io.*;
import com.lowagie.text.pdf.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author Imaginanet
 */
public class Main {

    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException {


        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);

        List<TrustManager> trustManagers = Arrays.asList(trustManagerFactory.getTrustManagers());
        List<X509Certificate> certificates = trustManagers.stream()
                .filter(X509TrustManager.class::isInstance)
                .map(X509TrustManager.class::cast)
                .map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("certificates"+certificates.size());
        try {

            Random rnd = new Random();
            KeyStore kall = PdfPKCS7.loadCacertsKeyStore();

            PdfReader reader = new PdfReader("E:\\u01\\Expedientes\\AMT-TC-2023-11-00000386_ECL.pdf");
            AcroFields af = reader.getAcroFields();
            ArrayList names = af.getSignatureNames();
            for (int k = 0; k < names.size(); ++k) {
                String name = (String) names.get(k);
                int random = rnd.nextInt();
                FileOutputStream out = new FileOutputStream("revision_" + random + "_" + af.getRevision(name) + ".pdf");

                byte bb[] = new byte[8192];
                InputStream ip = af.extractRevision(name);
                int n = 0;
                while ((n = ip.read(bb)) > 0) {
                    out.write(bb, 0, n);
                }
                out.close();
                ip.close();

                PdfPKCS7 pk = af.verifySignature(name);
                Calendar cal = pk.getSignDate();
                Certificate pkc[] = pk.getCertificates();
                Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
                if (fails == null) {
                    System.out.print(pk.getSignName());
                } else {
               //     System.out.print(fails[0].toString());
                    System.out.print("Firma no válida");
                }
                File f = new File("revision_" + random + "_" + af.getRevision(name) + ".pdf");
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
