package org.sumanta.to;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.sumanta.rest.config.ByteArrayStreamingOutput;

public class ToJKS {

    public static void toJKSTrustStore(final Certificate cert, final String password, final String alias, final String keystoreloc) {
        try {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, password.toCharArray());
            ks.setCertificateEntry(alias, cert);
            final OutputStream out = new FileOutputStream(new File(keystoreloc));
            ks.store(out, password.toCharArray());
            out.close();
        } catch (final KeyStoreException kse) {
            kse.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param cert
     * @param password
     * @param keystoreloc
     *            does not need alias as common name will be used as alias name.
     */
    public static void toJKSTrustStore(final Certificate[] cert, final String password, final String keystoreloc) {
        try {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, password.toCharArray());
            for (int i = 0; i < cert.length; i++) {
                X509Certificate certificate = (X509Certificate) cert[i];
                ks.setCertificateEntry(certificate.getSubjectDN().toString(), certificate);

            }
            final OutputStream out = new FileOutputStream(new File(keystoreloc));
            ks.store(out, password.toCharArray());
            out.close();
        } catch (final KeyStoreException kse) {
            kse.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
    }

    public static File toJKSKeyStore(final Certificate[] cert, final PrivateKey pkey, final String password, final String alias, final String keystoreloc) throws IOException {
    	
    	 // Create temp file.
        File temp = File.createTempFile("keystoreloc", ".jks");

        // Delete temp file when program exits.
        temp.deleteOnExit();
        
        try {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, password.toCharArray());

            ks.setKeyEntry(alias, pkey, password.toCharArray(), cert);

           
            
            final OutputStream out = new FileOutputStream(temp);
            ks.store(out, password.toCharArray());
            out.close();
        } catch (final KeyStoreException kse) {
            kse.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
        return temp;
    }

}
