package org.sumanta.test.it;

import javax.ejb.Stateless;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sumanta.test.it.setup.DeploymentBaseIT;

@RunWith(Arquillian.class)
@Stateless
public class WebCliTestIT extends DeploymentBaseIT {

    static String urlBase = "http://localhost:8080/Certificate-2.0/rest/certapi/execute/";

    @Test
    @OperateOnDeployment("certificate-test")
    public void testListRootCA() {

        ITutil iTutil = new ITutil();
        String result = iTutil.executeCommand(urlBase, "list rootca");

        Assert.assertTrue(result.contains("MyRootCA"));

    }

}
