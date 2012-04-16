package pl.com.it_crowd.arquillian.mock_contexts.client;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockViewScopeExtension;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockViewScopedContext;

import javax.enterprise.inject.spi.Extension;

public class MockContextsProcessor implements ApplicationArchiveProcessor {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ApplicationArchiveProcessor ---------------------

    public void process(Archive<?> appArchive, TestClass testClass)
    {
        if (appArchive instanceof ClassContainer) {
            ((ClassContainer) appArchive).addClass(MockViewScopedContext.class);
        }
        if (appArchive instanceof ServiceProviderContainer) {
            ((ServiceProviderContainer) appArchive).addAsServiceProviderAndClasses(Extension.class, MockViewScopeExtension.class);
        }
    }
}
