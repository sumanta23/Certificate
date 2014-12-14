package org.sumanta.cert;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.jce.PKCS10CertificationRequest;

public class GenarateCertificateFromCSR {

	public static void main(final String[] args) throws Exception {
		// TODO Auto-generated method stub
		final KeyPair pair = SamCA.generateRSAKeyPair();
		final PKCS10CertificationRequest cr = SamCA.generateCSR(pair, "csr");
		final SamCA ca = new SamCA("RootCA.crt", "rootkeypair");
		final X509Certificate cert = ca.signCSR(cr, 365,
				KeyPurposeId.id_kp_emailProtection);
		SamCA.saveCertificateToFile(cert, "csr.crt");
	}

}
