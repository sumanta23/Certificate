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
    public static File toP12withPrivateKey(final String alias, final String keystorepassword, final Certificate[] certchain, final PrivateKey privateKey, final String filename)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    	
    	 // Create temp file.
        File temp = File.createTempFile("keystoreloc", ".p12");

        // Delete temp file when program exits.
        temp.deleteOnExit();
        
        try {
            final KeyStore outStore = KeyStore.getInstance("PKCS12");
            outStore.load(null, keystorepassword.toCharArray());
            outStore.setKeyEntry(alias, privateKey, keystorepassword.toCharArray(), certchain);
            final OutputStream outputStream = new FileOutputStream(temp);
            outStore.store(outputStream, keystorepassword.toCharArray());
            outputStream.flush();
            outputStream.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

}
