package org.sumanta.cert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * @author Sumanta
 *
 */
public class ToDER {

	public static void main(String[] args) throws IOException {
		//InputStream pemStream = new ByteArrayInputStream(pemKey.getBytes());
		//byte[] derKey = EncryptionUtil.convertRsaPemToDer(pemStream);
		toDER(SamCA.loadCertificateFromFile("rootca.crt"),"rootca.der");
	}
	
	

	/**
	 * @param cert
	 * @param filepath
	 */
	private static void toDER(X509Certificate cert,String filepath) {
		try {
			FileOutputStream fos = new FileOutputStream(filepath);
		      byte[] certBytes = cert.getEncoded();
		      fos.write(certBytes);
		      fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
	}
}
