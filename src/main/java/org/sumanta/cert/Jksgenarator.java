package org.sumanta.cert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Sumanta
 *
 */
public class Jksgenarator {

  /**
   * @param args
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws CertificateException
   * @throws IOException
   */
  public static void main(final String[] args) throws KeyStoreException, NoSuchAlgorithmException,
      CertificateException, IOException {
    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(null, "password".toCharArray());
    final X509Certificate cert = SamCA.loadCertificateFromFile("rootca.crt");
    ks.setCertificateEntry("RootCA", cert);
    final Certificate[] c = { cert };
    ks.setKeyEntry("df", cert.getPublicKey(), "password".toCharArray(), c);
    final OutputStream out = new FileOutputStream(new File("Keystore.jks"));
    ks.store(out, "password".toCharArray());
    out.close();

  }

}
