package org.sumanta.cert;

import java.security.KeyPair;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.PKCS10CertificationRequest;

public class CSRGenerator {

  //private Logger logger = LoggerFactory.getLogger(CSRGenerator.class);

  /**
   * Generate an SSL CSR
   * 
   * @param pair
   *          KeyPair to use for the CSR
   * @param cn
   *          Common name for certificate (eg: blah.mydomain.com)
   * @return Generated CSR object
   * @throws Exception
   */
  public PKCS10CertificationRequest generateCSR(KeyPair pair, String cn) throws Exception {
   // logger.info("generating csr with signature algo SHA256withRSA and cn={}", cn);
    return new PKCS10CertificationRequest("SHA1withRSA", new X500Principal("CN=" + cn), pair.getPublic(), null,
            pair.getPrivate());
  }

  /**
   * signatureAlgorithm, X500Principal subject, PublicKey key, ASN1Set
   * attributes, PrivateKey signingKey
   * 
   * @param KeyPair
   * @return
   */
  public PKCS10CertificationRequest generateCSR(String signatureAlgorithm, KeyPair pair, String cn) throws Exception {
    //logger.info("generating csr with signature algo {} and cn={}", signatureAlgorithm, cn);
    return new PKCS10CertificationRequest(signatureAlgorithm, new X500Principal("CN=" + cn), pair.getPublic(), null,
            pair.getPrivate());
  }

}
