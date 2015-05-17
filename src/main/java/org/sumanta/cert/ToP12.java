package org.sumanta.cert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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
  public static void toP12withPrivateKey(final String alias, final String keystorepassword,
          final Certificate[] certchain, final PrivateKey privateKey, final String filename) throws KeyStoreException,
          NoSuchAlgorithmException, CertificateException, IOException {
    try {
      KeyStore outStore = KeyStore.getInstance("PKCS12");
      outStore.load(null, keystorepassword.toCharArray());
      outStore.setKeyEntry(alias, privateKey, keystorepassword.toCharArray(), certchain);
      final OutputStream outputStream = new FileOutputStream(filename);
      outStore.store(outputStream, keystorepassword.toCharArray());
      outputStream.flush();
      outputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

}
