package pl.com.it_crowd.arquillian.mock_contexts;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
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
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Logger;

public class MockContextsManager {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = Logger.getLogger(MockContextsManager.class.getName());

    @Inject
    private Instance<BeanManager> beanManagerInstance;

// -------------------------- STATIC METHODS --------------------------

    private static void setFacesContextCurrentInstance(Object objMock)
    {

        FacesContext mock = (FacesContext) objMock;

        try {
            Method setCurrentInstance = FacesContext.class.getDeclaredMethod("setCurrentInstance", FacesContext.class);
            setCurrentInstance.setAccessible(true);
            setCurrentInstance.invoke(null, mock);
            setCurrentInstance.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
    public void startFacesContext(@Observes EventContext<Before> eventContext)
    {
        eventContext.proceed();
        Before event = eventContext.getEvent();
        FacesContextRequired facesContextRequired = event.getTestMethod().getAnnotation(FacesContextRequired.class);
        if (facesContextRequired != null) {
            if (FacesContext.getCurrentInstance() == null) {
                boolean mockFacesContextProducerFound = false;
                FacesContext mock = null;
                for (Method method : event.getTestClass().getJavaClass().getMethods()) {
                    MockFacesContextProducer mockFacesContextProducer = method.getAnnotation(MockFacesContextProducer.class);
                    if (mockFacesContextProducer != null && facesContextRequired.name().equals(mockFacesContextProducer.name())) {
                        mockFacesContextProducerFound = true;
                        try {
                            mock = (FacesContext) method.invoke(event.getTestInstance());
                        } catch (Throwable e) {
                            throw new ProducerNotFoundException("Cannot invoke method " + method.toGenericString(), e);
                        }
                        if (mock == null) {
                            throw new IllegalStateException("Mock FacesContext producer method return null");
                        }
                        break;
                    }
                }
                if (!mockFacesContextProducerFound) {
                    throw new ProducerNotFoundException(
                        "Mock faces context producer method not found on class. It should be public FacesContext anyMethodName()");
                }
                setFacesContextCurrentInstance(mock);
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
                beanManagerInstance.get().getContext(ViewScoped.class);
                final MockViewScopedContext context = getViewScopedContext();
                context.invalidate();
                context.deactivate();
            } catch (ContextNotActiveException e) {
//              We dont need to do anything if scope is inactive
            }
        }
        if (event.getTestMethod().isAnnotationPresent(FacesContextRequired.class)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            setFacesContextCurrentInstance(null);
        }
    }

    private <T extends Context> T getContext(Class<T> contextClass)
    {
        final String canonicalName = contextClass == null ? null : contextClass.getCanonicalName();
        final BeanManager beanManager = beanManagerInstance.get();
        final Iterator<Bean<?>> iterator = beanManager.getBeans(contextClass).iterator();
        if (!iterator.hasNext()) {
            LOGGER.warning("No bean for context " + canonicalName + " found");
            return null;
        }
        @SuppressWarnings("unchecked") final Bean<T> bean = (Bean<T>) iterator.next();
        if (iterator.hasNext()) {
            LOGGER.warning("More beans for context " + canonicalName + " found");
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
