package org.sumanta.cert;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;


public class ToJKS {

	public static void toJKSTrustStore(Certificate cert,String password,String alias,String keystoreloc) {
		try{
		final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, password.toCharArray());
		ks.setCertificateEntry(alias, cert);
		final OutputStream out = new FileOutputStream(new File(keystoreloc));
		ks.store(out, password.toCharArray());
		out.close();
		}
		catch(KeyStoreException kse){
			kse.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param cert
	 * @param password
	 * @param keystoreloc
	 * does not need alias as common name will be used as alias name.
	 */
	public static void toJKSTrustStore(Certificate[] cert,String password,String keystoreloc) {
		try{
		final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, password.toCharArray());
		for (int i = 0; i < cert.length; i++) {
			X509Certificate certificate = (X509Certificate) cert[i];
			ks.setCertificateEntry(certificate.getSubjectDN().toString(), certificate);
			
		}
		final OutputStream out = new FileOutputStream(new File(keystoreloc));
		ks.store(out, password.toCharArray());
		out.close();
		}
		catch(KeyStoreException kse){
			kse.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void toJKSKeyStore(Certificate[] cert,PrivateKey pkey,String password,String alias,String keystoreloc) {
		try{
		final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, password.toCharArray());
		
		ks.setKeyEntry(alias, pkey, password.toCharArray(), cert);
		
		final OutputStream out = new FileOutputStream(new File(keystoreloc));
		ks.store(out, password.toCharArray());
		out.close();
		}
		catch(KeyStoreException kse){
			kse.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try {
			Certificate[] cert={SamCA.loadCertificateFromFile("ca.crt")};
			toJKSTrustStore(cert[0], "password", "gh", "public.jks");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
