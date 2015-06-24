package org.sumanta.cert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.bouncycastle.openssl.PEMWriter;

public class KeypairGenerator {

    //private Logger logger = LoggerFactory.getLogger(KeypairGenerator.class);

    /**
     * Generates an RSA public/private KeyPair in specified size
     * 
     * @return Generated KeyPair
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public KeyPair generateRSAKeyPairWithSize(int keysize) throws NoSuchProviderException, NoSuchAlgorithmException {
        // logger.info("keypair generate with Signature algo RSA(default) keysize:{}", keysize);
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(keysize, new SecureRandom());
        return kpGen.generateKeyPair();
    }

    /**
     * Generates an RSA public/private KeyPair
     * 
     * @return Generated KeyPair
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public KeyPair generateRSAKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
        // logger.info("keypair generate with Signature algo RSA(default) keysize:2048");
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        return kpGen.generateKeyPair();
    }

    public KeyPair generateRSAKeyPair(String signatureAlgo, int keysize) throws NoSuchProviderException, NoSuchAlgorithmException {
        // logger.info("keypair generate with Signature algo RSA{} keysize:{}", signatureAlgo, keysize);
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance(signatureAlgo, "BC");
        kpGen.initialize(keysize, new SecureRandom());
        return kpGen.generateKeyPair();
    }

    /**
     * Generates a new RSA KeyPair and saves the private key in PEM format to the specified filename
     * 
     * @param filename
     *            The filename to write out a RSA private key in PEM format
     * @return The generated RSA {@link KeyPair}
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Deprecated
    public KeyPair generateKeyPairAndSaveToFile(String filename) throws NoSuchProviderException, NoSuchAlgorithmException, IOException {

        KeyPair keyPair = generateRSAKeyPair();

        final Writer writer = new FileWriter(filename);
        final PEMWriter pemWriter = new PEMWriter(writer);
        pemWriter.writeObject(keyPair.getPrivate());
        pemWriter.close();

        return keyPair;

    }

}
