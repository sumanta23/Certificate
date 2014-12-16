package org.sumanta.cert;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class ToP12 {

	/**
	 * @param alias
	 * @param keystorepassword
	 * @param certchain
	 * @param privateKey
	 * @param filename
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static void toP12withPrivateKey(String alias,String keystorepassword,Certificate[] certchain, PrivateKey privateKey, String filename) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try{
			KeyStore outStore = KeyStore.getInstance("PKCS12");
			outStore.load(null, keystorepassword.toCharArray());
			outStore.setKeyEntry(alias, privateKey, keystorepassword.toCharArray(), certchain);
			OutputStream outputStream = new FileOutputStream(filename);
			outStore.store(outputStream, keystorepassword.toCharArray());
			outputStream.flush();
			outputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @param args
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try {
			Certificate[] cert={SamCA.loadCertificateFromFile("jh.crt")};
			toP12withPrivateKey("mykey", "password", cert, null, "public.p12");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
