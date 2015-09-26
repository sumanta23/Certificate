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

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.*;


@RunWith(Arquillian.class)
@Stateless
public class WebCliTestITest extends DeploymentBaseIT {

    static String urlBase = "http://localhost:8080/Certificate/rest/certapi/execute/";
    static String downloadUrl = "http://localhost:8080/Certificate/rest/certapi/download/";
    private static final String PU = "java:jboss/datasources/postgres";

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
    public void testCreateEntityCertificate() {

        ITutil iTutil = new ITutil();

        String serial=PropertyHolder.getInstance().getPropertyHolder().get("subcaserial");

        iTutil.executeCommand(urlBase, "create certificate -issuer "+serial+" -validity 3 -cn enitity1");

        Assert.assertTrue(true);

    }

    @Test
    @InSequence(7)
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

}
