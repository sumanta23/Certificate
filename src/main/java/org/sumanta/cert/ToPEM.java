package org.sumanta.cert;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import javax.security.cert.CertificateException;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;


public class ToPEM {

  /**
   * Converts a PEM formatted String to a {@link X509Certificate} instance.
   *
   * @param pem
   *          PEM formatted String
   * @return a X509Certificate instance
   * @throws CertificateException
   * @throws IOException
   */
  public X509Certificate convertToX509Certificate(String pem) throws CertificateException, IOException {
    X509Certificate cert = null;
    StringReader reader = new StringReader(pem);
    PEMReader pr = new PEMReader(reader);
    cert = (X509Certificate) pr.readObject();
    return cert;
  }

  public void toPEM(final X509Certificate x509Certificate, final String name) throws IOException,
          CertificateParsingException {
    final Writer writer = new FileWriter(name);
    final PEMWriter pemWriter = new PEMWriter(writer);
    pemWriter.writeObject(x509Certificate);
    pemWriter.close();
  }

  public OutputStream toPEM(final X509Certificate x509Certificate) throws IOException, CertificateParsingException,
          CertificateEncodingException {
    OutputStream outputStream = new ByteArrayOutputStream();
    PEMWriter pemWrt = new PEMWriter(new OutputStreamWriter(outputStream));
    pemWrt.writeObject(x509Certificate);
    pemWrt.close();
    return outputStream;
  }

}
