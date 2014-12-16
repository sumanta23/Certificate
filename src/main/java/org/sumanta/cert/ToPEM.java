package org.sumanta.cert;

import java.io.*;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.PEMWriter;

public class ToPEM {

	/**
	 * @param args
	 * @throws IOException
	 * @throws CertificateParsingException
	 */
	public static void main(final String[] args) throws IOException,
			CertificateParsingException {
		// TODO Auto-generated method stub
		toPEM(SamCA.loadCertificateFromFile("rootca.crt"),"rootca.pem");
	}

	public static void toPEM(final X509Certificate x509Certificate,String name)
			throws IOException, CertificateParsingException {
		final Writer writer = new FileWriter(name);
		final PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(x509Certificate);
		pemWriter.close();
	}

}
