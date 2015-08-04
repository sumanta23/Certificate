package org.sumanta.unit.cert;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.sumanta.cert.KeyGenAlgo;
import org.sumanta.cert.KeypairGenerator;

@RunWith(MockitoJUnitRunner.class)
public class KeypairGeneratorTest {

  /*
   * @Spy Logger logger = LoggerFactory.getLogger(KeypairGenerator.class);
   */

  KeypairGenerator keypairGenerator = new KeypairGenerator();

  @Before
  public void setUp() throws Exception {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
  }

  @Test
  public void testGenerateRSAKeyPairWithSize() throws NoSuchProviderException, NoSuchAlgorithmException {

    KeyPair keyPair = keypairGenerator.generateRSAKeyPairWithSize(1024);
    assertEquals(keyPair.getPublic().getAlgorithm(), "RSA");
  }

  @Test
  public void testGenerateRSAKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
    
    KeyPair keyPair = keypairGenerator.generateRSAKeyPair();
    assertEquals(keyPair.getPublic().getAlgorithm(), "RSA");
  }

  @Test
  public void testGenerateRSAKeyPairStringIntDSA() throws NoSuchProviderException, NoSuchAlgorithmException {
    
    KeyPair keyPair = keypairGenerator.generateRSAKeyPair(KeyGenAlgo.DSA, 512);
    assertEquals(keyPair.getPublic().getFormat(), "X.509");
  }

  @Test
  public void testGenerateRSAKeyPairStringIntRSA() throws NoSuchProviderException, NoSuchAlgorithmException {
    
    KeyPair keyPair = keypairGenerator.generateRSAKeyPair(KeyGenAlgo.RSA, 512);
    assertEquals(keyPair.getPublic().getFormat(), "X.509");
  }

  @Test
  public void testGenerateKeyPairAndSaveToFile() throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
    
    KeyPair keyPair = keypairGenerator.generateKeyPairAndSaveToFile(System.getProperty("java.io.tmpdir") + "/mykey");
    assertEquals(keyPair.getPublic().getFormat(), "X.509");
  }

}
