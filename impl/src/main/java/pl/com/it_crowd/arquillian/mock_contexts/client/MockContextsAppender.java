package pl.com.it_crowd.arquillian.mock_contexts.client;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import pl.com.it_crowd.arquillian.mock_contexts.MockContextsManager;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockContextsRemoteExtension;

public class MockContextsAppender implements AuxiliaryArchiveAppender {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AuxiliaryArchiveAppender ---------------------

    public Archive<?> createAuxiliaryArchive()
    {
        return ShrinkWrap.create(JavaArchive.class, "arquillian-testenricher-mockcontexts.jar")
            .addPackages(false, MockContextsRemoteExtension.class.getPackage(), MockContextsManager.class.getPackage())
            .addAsServiceProvider(RemoteLoadableExtension.class, MockContextsRemoteExtension.class);
    }
}
