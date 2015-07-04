/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package org.sumanta.test.it.setup;

import java.io.File;
import java.util.Set;


import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;


import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class IntegrationTestDeploymentFactory {


  public static final File BEANS_XML_FILE = new File("src/test/resources/META-INF/beans.xml");

  /**
   * Create deployment from given maven coordinates
   * 
   * @param mavenCoordinates
   *          Maven coordinates in form of groupId:artifactId:type
   * @return Deployment archive represented by this maven artifact
   */
  public static EnterpriseArchive createEARDeploymentFromMavenCoordinates(final String mavenCoordinates) {
    //log.debug("******Creating deployment {} for test******", mavenCoordinates);
    final File archiveFile = IntegrationTestDependencies.resolveArtifactWithoutDependencies(mavenCoordinates);
    if (archiveFile == null) {
      throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
    }
    final EnterpriseArchive ear = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, archiveFile);

   // log.debug("******Created from maven artifact with coordinates {} ******", mavenCoordinates);
    return ear;
  }

  public static Archive<?> createEarTestDeployment(final Set<Package> testPackagesToAdd) {
    final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class);
    final MavenDependencyResolver resolver = getMavenResolver();
    ear.addAsLibrary(createModuleArchive(testPackagesToAdd));

    /*// (2) Libraries needed to perform some tests
    ear.addAsLibraries(resolver.artifact(Dependencies.ORG_JBOSS_SHRINKWRAP_RESOLVER_JAR).resolveAsFiles());
    ear.addAsLibraries(resolver.artifact(Dependencies.ORG_APACHE_HTTPCOMP).resolveAsFiles());
    ear.addAsLibraries(resolver.artifact(Dependencies.ORG_APACHE_HTTPCORE).resolveAsFiles());
    ear.addAsLibraries(resolver.artifact(Dependencies.ORG_APACHE_HTTPMIME).resolveAsFiles());*/
    ear.addAsLibraries(resolver.artifact(Dependencies.ORG_JBOSS___RESTEASY).resolveAsFiles());

    System.out.println("EAR content:" + ear.toString(true));
    return ear;
  }

  /**
   * This is used to setup the module configuration
   * 
   * @return Archive
   */
  public static Archive<?> createModuleArchive(final Set<Package> testPackagesToAdd) {
    final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test-bean-lib.jar");
    for (final Package p : testPackagesToAdd) {
      archive.addPackage(p);
    }
    archive.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    return archive;
  }

  public static MavenDependencyResolver getMavenResolver() {
    return DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
  }

}
