package pl.itcrowd.arquillian.mock_contexts.client;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class MockContextsExtension implements RemoteLoadableExtension {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface LoadableExtension ---------------------

    public void register(LoadableExtension.ExtensionBuilder builder)
    {
        builder.service(AuxiliaryArchiveAppender.class, MockContextsAppender.class)
            .service(ApplicationArchiveProcessor.class, MockContextsProcessor.class)
            .service(ProtocolArchiveProcessor.class, MockContextsProcessor.class);
    }
}