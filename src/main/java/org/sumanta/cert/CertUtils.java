package org.sumanta.cert;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

public class CertUtils {

    /**
     * Loads a KeyPair from file
     * 
     * @param filename
     *            File to load from
     * @return Valid KeyPair or null
     * @throws IOException
     */
    public KeyPair loadKeyPairFromFile(String filename) throws IOException {

        KeyPair keyPair = null;

        try {

            final Reader reader = new FileReader(filename);
            final PEMReader pemReader = new PEMReader(reader);

            Object object;
            while ((object = pemReader.readObject()) != null) {
                if (object instanceof KeyPair) {
                    keyPair = (KeyPair) object;
                }
            }

            reader.close();
            return keyPair;
        } catch (FileNotFoundException e) {
            return null;
        }

    }

    public void saveKeypairToFile(KeyPair keyPair, String filename) throws IOException {
        final Writer writer = new FileWriter(filename);
        final PEMWriter pemWriter = new PEMWriter(writer);
        pemWriter.writeObject(keyPair.getPrivate());
        pemWriter.close();
    }

    /**
     * Loads an X.509 certificate from file
     * 
     * @param filename
     *            File to load from
     * @return Valid X509Certificate or null
     * @throws IOException
     */
    public static X509Certificate loadCertificateFromFile(String filename) throws IOException {

        X509Certificate cert = null;

        try {
            final Reader reader = new FileReader(filename);
            final PEMReader pemReader = new PEMReader(reader);

            Object object;
            while ((object = pemReader.readObject()) != null) {
                if (object instanceof X509Certificate) {
                    cert = (X509Certificate) object;
                }
            }

            reader.close();
            return cert;
        } catch (FileNotFoundException e) {
            return null;
        }

    }

    /**
     * Generates a valid CA certificate and saves it in PEM format to the specified filename
     * 
     * @param filename
     *            The filename to write out a CA certificate in PEM format
     * @param X509Certificate
     *            certificate
     * 
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    public void saveCertificateToFile(String filename, X509Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException,
            SignatureException, IOException {

        final Writer writer = new FileWriter(filename);
        final PEMWriter pemWriter = new PEMWriter(writer);
        pemWriter.writeObject(certificate);
        pemWriter.close();
    }

}
