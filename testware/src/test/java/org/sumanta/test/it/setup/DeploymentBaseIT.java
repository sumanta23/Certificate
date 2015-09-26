package org.sumanta.test.it.setup;

import java.util.HashSet;
import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.sumanta.test.it.ITutil;
import org.sumanta.test.it.util.PropertyHolder;

public class DeploymentBaseIT {

  @Deployment(name = "certificate-ear", testable = true)
  public static Archive<?> createDeployablePKIWebCliEAR() {
    return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.WEB_CLI_EAR);
  }

  @Deployment(testable = true, name = "certificate-test")
  public static Archive<?> createEarWithIntegrationTest() {
    final Set<Package> testPackages = new HashSet<Package>();

    // Common
    testPackages.add(Dependencies.class.getPackage());
    testPackages.add(DeploymentBaseIT.class.getPackage());
    testPackages.add(ITutil.class.getPackage());
    testPackages.add(PropertyHolder.class.getPackage());

    return IntegrationTestDeploymentFactory.createEarTestDeployment(testPackages);
  }

}
