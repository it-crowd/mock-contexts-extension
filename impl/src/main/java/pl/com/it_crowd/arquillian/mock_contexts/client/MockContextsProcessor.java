package pl.com.it_crowd.arquillian.mock_contexts.client;

import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockContextsCDIExtension;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockConversation;

import javax.enterprise.inject.spi.Extension;

public class MockContextsProcessor implements ApplicationArchiveProcessor, ProtocolArchiveProcessor {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ApplicationArchiveProcessor ---------------------

    public void process(Archive<?> archive, TestClass testClass)
    {
        //        Workaround for SHRINKWRAP-419
        if (archive instanceof EnterpriseArchive) {
            for (Node node : archive.get("/").getChildren()) {
                final Asset asset = node.getAsset();
                if (asset != null && asset instanceof ArchiveAsset && Testable.isArchiveToTest((Archive) ((ArchiveAsset) asset).getArchive())) {
                    process(((ArchiveAsset) asset).getArchive(), testClass);
                }
            }
            return;
        }
        if (archive.getName().endsWith(".war")) {
            if (archive instanceof ClassContainer) {
                ((ClassContainer) archive).addClass(MockConversation.class);
            }
            if (archive instanceof ServiceProviderContainer) {
                ((ServiceProviderContainer) archive).addAsServiceProviderAndClasses(Extension.class, MockContextsCDIExtension.class);
            }
        }
    }

// --------------------- Interface ProtocolArchiveProcessor ---------------------

    public void process(TestDeployment testDeployment, Archive<?> archive)
    {
        /**
         * This is for jar deployment wrapped with war
         */
        final Archive<?> applicationArchive = testDeployment.getApplicationArchive();
        if (archive.getName().endsWith(".war") && !applicationArchive.equals(archive)) {
            final String jarBeansDescriptorPath = "/META-INF/beans.xml";
            final String warBeansDescriptorPath = "/WEB-INF/beans.xml";
            final Node node = applicationArchive.get(jarBeansDescriptorPath);
            if (node != null) {
                archive.delete(warBeansDescriptorPath);
                archive.add(node.getAsset(), warBeansDescriptorPath);
            }
            if (archive instanceof ClassContainer) {
                ((ClassContainer) archive).addClass(MockConversation.class);
            }
            if (archive instanceof ServiceProviderContainer) {
                ((ServiceProviderContainer) archive).addAsServiceProviderAndClasses(Extension.class, MockContextsCDIExtension.class);
            }
        }
    }
}
