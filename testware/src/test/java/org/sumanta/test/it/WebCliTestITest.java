package org.sumanta.test.it;

import javax.ejb.Stateless;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sumanta.test.it.setup.DeploymentBaseIT;
import org.sumanta.test.it.util.PropertyHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.*;


@RunWith(Arquillian.class)
@Stateless
public class WebCliTestITest extends DeploymentBaseIT {

    static String urlBase = "http://localhost:8080/Certificate/rest/certapi/execute/";
    static String downloadUrl = "http://localhost:8080/Certificate/rest/certapi/download/";
    private static final String PU = "java:jboss/datasources/postgres";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    @Test
    @InSequence(1)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testCreateRootCA() {

        ITutil iTutil = new ITutil();
        iTutil.executeCommand(urlBase, "create rootca -validity 3 -cn MYRootCA");

        Assert.assertTrue(true);

    }

    @Test
    @InSequence(2)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testListRootCA() {

        ITutil iTutil = new ITutil();
        String result = iTutil.executeCommand(urlBase, "list rootca");

        String serial=iTutil.fetchSerialNumber(result);
        PropertyHolder.getInstance().getPropertyHolder().put("rootcaserial", serial);

        Assert.assertTrue(result.contains("MYRootCA"));

    }

    @Test
    @InSequence(3)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testExportRootCA() throws IOException, CertificateException {
        ITutil iTutil = new ITutil();
        String serial=PropertyHolder.getInstance().getPropertyHolder().get("rootcaserial");

        String result = iTutil.executeCommand(urlBase, "export -cat certificate rootca -serialno " + serial + " -filename d.crt -format crt");
        System.out.println(result);
        
        InputStream stream=iTutil.downloadCommand(downloadUrl + result);
        
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (stream.available() > 0) {
                final Certificate cert = cf.generateCertificate(stream);
                final String certificate = cert.toString();
                System.out.println(certificate);
        }
        /* Assert.assertTrue(result.contains("MYRootCA")); */

    }


    @Test
    @InSequence(4)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testCreateSubCA() {

        ITutil iTutil = new ITutil();
        String serial=PropertyHolder.getInstance().getPropertyHolder().get("rootcaserial");

        iTutil.executeCommand(urlBase, "create ca -issuer "+serial+" -validity 3 -cn MySubCA");

        Assert.assertTrue(true);

    }

    @Test
    @InSequence(5)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testListWSubCA() {

        ITutil iTutil = new ITutil();
        String result = iTutil.executeCommand(urlBase, "list ca");
        System.out.println(result);
        Assert.assertTrue(result.contains("MySubCA"));

        String serial=iTutil.fetchSerialNumber(result);
        PropertyHolder.getInstance().getPropertyHolder().put("subcaserial", serial);

    }
    
    
    @Test
    @InSequence(6)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testExportSubCA() throws IOException, CertificateException {
        ITutil iTutil = new ITutil();
        String serial=PropertyHolder.getInstance().getPropertyHolder().get("subcaserial");

        String result = iTutil.executeCommand(urlBase, "export -cat certificate ca -serialno " + serial + " -filename d.crt -format crt");
        System.out.println(result);
        
        InputStream stream=iTutil.downloadCommand(downloadUrl + result);
        
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (stream.available() > 0) {
                final Certificate cert = cf.generateCertificate(stream);
                final String certificate = cert.toString();
                System.out.println(certificate);
        }
        /* Assert.assertTrue(result.contains("MYRootCA")); */

    }


    @Test
    @InSequence(7)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testCreateEntityCertificate() {

        ITutil iTutil = new ITutil();

        String serial=PropertyHolder.getInstance().getPropertyHolder().get("subcaserial");

        iTutil.executeCommand(urlBase, "create certificate -issuer "+serial+" -validity 3 -cn enitity1");

        Assert.assertTrue(true);

    }

    @Test
    @InSequence(8)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testListXEndEntity() {

        ITutil iTutil = new ITutil();
        String result = iTutil.executeCommand(urlBase, "list certificate");
        System.out.println(result);
        Assert.assertTrue(result.contains("enitity1"));

        String serial=iTutil.fetchSerialNumber(result);
        PropertyHolder.getInstance().getPropertyHolder().put("entityserial", serial);

    }
    
    @Test
    @InSequence(9)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testExportEntityCertificate() throws IOException, CertificateException {
        ITutil iTutil = new ITutil();
        String serial=PropertyHolder.getInstance().getPropertyHolder().get("entityserial");

        String result = iTutil.executeCommand(urlBase, "export -cat certificate certificate -serialno " + serial + " -filename d.crt -format crt");
        System.out.println(result);
        
        InputStream stream=iTutil.downloadCommand(downloadUrl + result);
        
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (stream.available() > 0) {
                final Certificate cert = cf.generateCertificate(stream);
                final String certificate = cert.toString();
                System.out.println(certificate);
        }
        /* Assert.assertTrue(result.contains("MYRootCA")); */

    }

    
    @Test
    @InSequence(10)
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testExportEntityCertificateTOJKS() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        ITutil iTutil = new ITutil();
        String serial=PropertyHolder.getInstance().getPropertyHolder().get("entityserial");

        String result = iTutil.executeCommand(urlBase, "export -cat keystore certificate -serialno " + serial + " -filename d.jks -format jks");
        System.out.println(result);
        
        InputStream stream=iTutil.downloadCommand(downloadUrl + result);
   
        FileOutputStream fos=new FileOutputStream(new File("target/gh.jks"));
        
        
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(stream,  "secret".toCharArray());
        
        ks.store(fos, "secret".toCharArray());
        fos.close();
        
        Assert.assertEquals(ks.getCertificate("key").getPublicKey().getAlgorithm(), "RSA");

    }


}
