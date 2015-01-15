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
	public static void toP12withPrivateKey(final String alias,final String keystorepassword,final Certificate[] certchain,final PrivateKey privateKey, final String filename) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try{
			KeyStore outStore = KeyStore.getInstance("PKCS12");
			outStore.load(null, keystorepassword.toCharArray());
			outStore.setKeyEntry(alias, privateKey, keystorepassword.toCharArray(), certchain);
			final OutputStream outputStream = new FileOutputStream(filename);
			outStore.store(outputStream, keystorepassword.toCharArray());
			outputStream.flush();
			outputStream.close();
		}catch(final Exception e){
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
	public static void main(final String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try {
			final Certificate[] cert={SamCA.loadCertificateFromFile("jh.crt")};
			toP12withPrivateKey("mykey", "password", cert, null, "public.p12");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
