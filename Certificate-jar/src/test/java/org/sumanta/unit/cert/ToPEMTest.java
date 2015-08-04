package org.sumanta.unit.cert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import javax.security.cert.CertificateException;

import org.junit.Before;
import org.junit.Test;
import org.sumanta.cert.IssueCertificate;
import org.sumanta.to.ToP12;
import org.sumanta.to.ToPEM;

public class ToPEMTest {

    X509Certificate cert;
    KeyPair keypair;

    @Before
    public void setUp() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        keypair = kpGen.generateKeyPair();
        IssueCertificate issueCertificate = new IssueCertificate();
        cert = issueCertificate.issueV1Certificate(keypair, "sam", 100);
    }

    @Test
    public void testToPEM() throws CertificateParsingException, IOException, CertificateEncodingException {
        ToPEM pem = new ToPEM();

        OutputStream outputStream = pem.toPEM(cert);
        assertTrue(outputStream.toString().contains("BEGIN CERTIFICATE"));
    }

    @Test
    public void testconvertToX509Certificate() throws CertificateException, IOException, CertificateParsingException, CertificateEncodingException {
        ToPEM pem = new ToPEM();
        OutputStream outputStream = pem.toPEM(cert);
        assertTrue(outputStream.toString().contains("BEGIN CERTIFICATE"));
        X509Certificate certificate = pem.convertToX509Certificate(outputStream.toString());
        assertEquals(certificate.getIssuerDN().toString(), "CN=sam");
    }

}
