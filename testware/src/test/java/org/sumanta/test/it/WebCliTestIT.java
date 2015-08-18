package org.sumanta.test.it;

import javax.ejb.Stateless;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sumanta.test.it.setup.DeploymentBaseIT;

import java.util.StringTokenizer;

@RunWith(Arquillian.class)
@Stateless
public class WebCliTestIT extends DeploymentBaseIT {

    static String urlBase = "http://localhost:8080/Certificate/rest/certapi/execute/";
    private static final String PU="java:jboss/datasources/postgres";

    @Test
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testCreateRootCA() {

        ITutil iTutil = new ITutil();
        iTutil.executeCommand(urlBase, "create rootca -validity 3 -cn MYRootCA");

        Assert.assertTrue(true);

    }

    @Test
    @OperateOnDeployment("certificate-test")
    @DataSource(PU)
    public void testListRootCA() {

        ITutil iTutil = new ITutil();
        String result = iTutil.executeCommand(urlBase, "list rootca");
        System.out.println(result);
        Assert.assertTrue(result.contains("MYRootCA"));

    }

    
    @Test
    @OperateOnDeployment("certificate-test")
    public void testExportRootCA() {

        ITutil iTutil = new ITutil();
        String fullResult = iTutil.executeCommand(urlBase, "list rootca");
        StringTokenizer stringTokenizer=new StringTokenizer(fullResult,"\t");
        stringTokenizer.nextElement();
        stringTokenizer.nextElement();
        String serial = (String)stringTokenizer.nextElement();
      ITutil iTutilw = new ITutil();
      String result = iTutilw.executeCommand(urlBase, "export rootca -serialno "+ serial+ " -filename d.crt -format crt");
        System.out.println(result);
        /*Assert.assertTrue(result.contains("MYRootCA"));*/

    }

}
