package pl.com.it_crowd.arquillian.mock_contexts;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockConversationScopedContext;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockViewScopedContext;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.bean.ViewScoped;
import java.util.Iterator;

public class MockContextsManager {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private Instance<BeanManager> beanManagerInstance;

// -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings("UnusedDeclaration")
    public void startContexts(@Observes Before event)
    {
        if (event.getTestMethod().getAnnotation(ConversationScopeRequired.class) != null) {
            try {
                beanManagerInstance.get().getContext(ConversationScoped.class);
            } catch (ContextNotActiveException e) {
                final MockConversationScopedContext context = getConversationContext();
                context.activate();
            }
        }
        if (event.getTestMethod().getAnnotation(ViewScopeRequired.class) != null) {
            try {
                beanManagerInstance.get().getContext(ViewScoped.class);
            } catch (ContextNotActiveException e) {
                final MockViewScopedContext context = getViewScopedContext();
                context.activate();
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void stopContexts(@Observes After event)
    {
        if (event.getTestMethod().getAnnotation(ConversationScopeRequired.class) != null) {
            try {
                beanManagerInstance.get().getContext(ConversationScoped.class);
                final MockConversationScopedContext context = getConversationContext();
                context.invalidate();
                context.deactivate();
            } catch (ContextNotActiveException e) {
//              We dont need to do anything if scope is inactive
            }
        }
        if (event.getTestMethod().getAnnotation(ViewScopeRequired.class) != null) {
            try {
                beanManagerInstance.get().getContext(ConversationScoped.class);
                final MockViewScopedContext context = getViewScopedContext();
                context.invalidate();
                context.deactivate();
            } catch (ContextNotActiveException e) {
//              We dont need to do anything if scope is inactive
            }
        }
    }

    private <T extends Context> T getContext(Class<T> contextClass)
    {
        final BeanManager beanManager = beanManagerInstance.get();
        final Iterator<Bean<?>> iterator = beanManager.getBeans(contextClass).iterator();
        if (!iterator.hasNext()) {
//            TODO log warining
            return null;
        }
        @SuppressWarnings("unchecked") final Bean<T> bean = (Bean<T>) iterator.next();
        if (iterator.hasNext()) {
//            TODO log warning that more than one bean is available
        }
        return bean.create(beanManager.createCreationalContext(bean));
    }

    private MockConversationScopedContext getConversationContext()
    {
        return getContext(MockConversationScopedContext.class);
    }

    private MockViewScopedContext getViewScopedContext()
    {
        return getContext(MockViewScopedContext.class);
    }
}
