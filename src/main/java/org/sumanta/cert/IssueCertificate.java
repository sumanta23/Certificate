package org.sumanta.cert;

import java.security.KeyPair;
import java.security.cert.Certificate;

import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.jce.PKCS10CertificationRequest;

/**
 * @author Sumanta
 *
 */
public class IssueCertificate {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final Certificate rootca = SamCA.loadCertificateFromFile("RootCA.crt");
		final KeyPair pair = SamCA.generateRSAKeyPair();
		PKCS10CertificationRequest cr = SamCA.generateCSR(pair, "CN=my cert");

		final SamCA ca = new SamCA("RootCA.crt", "rootkeypair");
		ca.issueCertificate(null, "CN=my cert", 365,
				KeyPurposeId.id_kp_ipsecUser);
		SamCA.saveKeypairToFile(ca.getIssuedKeyPair(), "subkeypair");
		final Certificate issedcert = ca.getIssuedCertificate();
		SamCA.saveCertificateToFile(issedcert, "mycert.crt");

	}

}
