package org.sumanta.cert;

import java.security.KeyPair;
import java.security.cert.Certificate;

import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.jce.PKCS10CertificationRequest;

public class GenarateSubCA {

  /**
   * @param args
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    // TODO Auto-generated method stub
    KeyPair pair = SamCA.generateRSAKeyPair();

    SamCA ca = new SamCA("mycert.crt", "subkeypair");
    ca.issueCertificate(null, "CN=subcacert", 365, KeyPurposeId.id_kp_ipsecUser);
    Certificate issedcert = ca.getIssuedCertificate();
    SamCA.saveCertificateToFile(issedcert, "my.crt");
  }

}
