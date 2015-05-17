package org.sumanta.cert;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CSRGeneratorTest {

/*  @Spy
  Logger logger = LoggerFactory.getLogger(CSRGenerator.class);

  @Spy
  Logger kpglogger = LoggerFactory.getLogger(KeypairGenerator.class);*/


  KeyPair keypair;
  @Mock
  KeypairGenerator keyPairGen=new KeypairGenerator();
  CSRGenerator csrGenerator=new CSRGenerator(); 
  
  @Before
  public void setUp() throws Exception {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
    kpGen.initialize(2048, new SecureRandom());
    keypair = kpGen.generateKeyPair();
  }

  @Test
  public void testGenerateCSRKeyPairString() throws Exception {
    Mockito.when(keyPairGen.generateRSAKeyPair()).thenReturn(keypair);
    KeyPair keyPair = keyPairGen.generateRSAKeyPair();
    PKCS10CertificationRequest certificationRequest = csrGenerator.generateCSR(keyPair, "sam");
    assertEquals(certificationRequest.getCertificationRequestInfo().getSubject().toString(), "CN=sam");
  }

  @Test
  public void testGenerateCSRStringKeyPairString() throws Exception {
    /*MockitoAnnotations.initMocks(csrGenerator);*/
    Mockito.when(keyPairGen.generateRSAKeyPair()).thenReturn(keypair);
    KeyPair keyPair = keyPairGen.generateRSAKeyPair();
    PKCS10CertificationRequest certificationRequest = csrGenerator.generateCSR(SignatureAlgo.SHA256withRSA, keyPair,
            "sam");
    assertEquals(certificationRequest.getCertificationRequestInfo().getSubject().toString(), "CN=sam");
  }

}
