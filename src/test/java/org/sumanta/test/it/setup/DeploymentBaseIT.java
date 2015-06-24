package org.sumanta.test.it.setup;

import java.util.HashSet;
import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.sumanta.test.it.ITutil;

public class DeploymentBaseIT {

  @Deployment(name = "certificate", testable = true)
  public static Archive<?> createDeployablePKIWebCliEAR() {
    return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.WEB_CLI_WAR);
  }

  @Deployment(testable = true, name = "certificate-test-ear")
  public static Archive<?> createEarWithIntegrationTest() {
    final Set<Package> testPackages = new HashSet<Package>();

    // Common
    testPackages.add(Dependencies.class.getPackage());
    testPackages.add(DeploymentBaseIT.class.getPackage());
    testPackages.add(ITutil.class.getPackage());

    return IntegrationTestDeploymentFactory.createEarTestDeployment(testPackages);
  }

}
