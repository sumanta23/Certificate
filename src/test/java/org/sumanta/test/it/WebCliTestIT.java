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

  static String urlBase = "http://localhost:8080/Certificate/rest/certapi/execute";

  @Test
  @OperateOnDeployment("web-cli-test-ear")
  public void testEServiceRefNotNull() throws Exception {

    Assert.assertNotNull("PkiCliService @EService ref should not be null.", "df");

  }

  @Test
  @OperateOnDeployment("web-cli-test-ear")
  public void testListRootCA() {

    ITutil iTutil = new ITutil();
    String result = iTutil.executeCommand(urlBase, "list rootca");

    Assert.assertEquals("", result);

  }

}
