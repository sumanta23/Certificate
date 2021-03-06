package org.sumanta.to;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * @author Sumanta
 *
 */
public class ToDER {

    /**
     * @param cert
     * @param filepath
     */
    private static void toDER(final X509Certificate cert, final String filepath) {
        try {
            final FileOutputStream fos = new FileOutputStream(filepath);
            final byte[] certBytes = cert.getEncoded();
            fos.write(certBytes);
            fos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final CertificateEncodingException e) {
            e.printStackTrace();
        }
    }
}
