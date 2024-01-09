/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.yoveri.firmar.pdf;

import java.io.IOException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.CertificateInfo;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CertificateVerification;
import com.itextpdf.text.pdf.security.KeyStoreUtil;
import com.itextpdf.text.pdf.security.PdfPKCS7;


/**
 * Verifica una firma digital sobre un documento PDF utilizando iText.
 * 
 * @author Ricardo Arguello (ricardo.arguello@soportelibre.com)
 */
public class VerificadorFirmaPdf {

	/** Campos de un PDF */
	private AcroFields af;

	/** Certificados de CAs del JVM */
	private KeyStore cacerts;

	private static final Logger log = Logger
			.getLogger(VerificadorFirmaPdf.class.getName());

	static {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			public Void run() {
				Security.addProvider(new BouncyCastleProvider());
				return null;
			}
		});
	}

	public VerificadorFirmaPdf(byte[] pdf) throws IOException {
		PdfReader pdfReader = new PdfReader(pdf);
		this.af = pdfReader.getAcroFields();
		this.cacerts = KeyStoreUtil.loadCacertsKeyStore();
	}
private void verificarSiTieneOCSP(Certificate[] chain) {
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = (X509Certificate) chain[i];
			System.out
					.println(String.format("[%s] %s", i, cert.getSubjectDN()));
			System.out.println(CertificateUtil.getOCSPURL(cert));
		}
	}



}
