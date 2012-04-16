package pl.com.it_crowd.arquillian.mock_contexts;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockViewScopedContext;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.HashMap;

public class MockContextsManager {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private Instance<BeanManager> beanManagerInstance;

// -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings("UnusedDeclaration")
    public void startContexts(@Observes Before event)
    {
        if (event.getTestMethod().getAnnotation(ConversationScoped.class) != null) {
            final BoundConversationContext context = getConversationContext();
            if (context != null && !context.isActive()) {
                context.associate(new MutableBoundRequest(new HashMap<String, Object>(), new HashMap<String, Object>()));
                context.activate();
            }
        }
        if (event.getTestMethod().getAnnotation(ViewScoped.class) != null) {
            final MockViewScopedContext context = getViewScopedContext();
            if (context != null && !context.isActive()) {
                context.activate();
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void stopContexts(@Observes After event)
    {
        if (event.getTestMethod().getAnnotation(ConversationScoped.class) != null) {
            final BoundConversationContext context = getConversationContext();
            if (context != null && context.isActive()) {
                context.invalidate();
                context.deactivate();
            }
        }
        if (event.getTestMethod().getAnnotation(ViewScoped.class) != null) {
            final MockViewScopedContext context = getViewScopedContext();
            if (context != null && !context.isActive()) {
                context.invalidate();
                context.deactivate();
            }
        }
    }

    private <T extends Context> T getContext(Class<T> contextClass)
    {
        final BeanManager beanManager = beanManagerInstance.get();
        @SuppressWarnings("unchecked") final Bean<T> bean = (Bean<T>) beanManager.getBeans(contextClass).iterator().next();
        return bean.create(beanManager.createCreationalContext(bean));
    }

    private BoundConversationContext getConversationContext()
    {
        return getContext(BoundConversationContext.class);
    }

    private MockViewScopedContext getViewScopedContext()
    {
        return getContext(MockViewScopedContext.class);
    }
}
