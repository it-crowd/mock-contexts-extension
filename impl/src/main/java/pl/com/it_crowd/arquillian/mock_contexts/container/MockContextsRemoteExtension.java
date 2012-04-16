package pl.com.it_crowd.arquillian.mock_contexts.container;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.spi.LoadableExtension;
import pl.com.it_crowd.arquillian.mock_contexts.MockContextsManager;

public class MockContextsRemoteExtension implements RemoteLoadableExtension {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface LoadableExtension ---------------------

    public void register(LoadableExtension.ExtensionBuilder builder)
    {
        builder.observer(MockContextsManager.class);
    }
}
