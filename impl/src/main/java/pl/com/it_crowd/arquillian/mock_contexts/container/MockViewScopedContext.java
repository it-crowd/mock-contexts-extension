package pl.com.it_crowd.arquillian.mock_contexts.container;

import org.jboss.weld.context.ManagedContext;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.faces.bean.ViewScoped;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockViewScopedContext implements ManagedContext {
// ------------------------------ FIELDS ------------------------------

    private Map<Contextual<?>, Object> componentInstanceMap;

    private Map<Contextual<?>, CreationalContext<?>> creationalContextMap;

    private boolean initialized = false;

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean isInitialized()
    {
        return initialized;
    }

    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Context ---------------------

    public Class<? extends Annotation> getScope()
    {
        return ViewScoped.class;
    }

    public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext)
    {
        T instance = get(component);
        if (instance == null) {
            if (creationalContext != null) {
                //noinspection SynchronizeOnNonFinalField
                synchronized (componentInstanceMap) {
                    //noinspection unchecked
                    instance = (T) componentInstanceMap.get(component);
                    if (instance == null) {
                        instance = component.create(creationalContext);
                        if (instance != null) {
                            componentInstanceMap.put(component, instance);
                            creationalContextMap.put(component, creationalContext);
                        }
                    }
                }
            }
        }

        return instance;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(final Contextual<T> component)
    {
        assertActive();
        return (T) componentInstanceMap.get(component);
    }

    public boolean isActive()
    {
        return componentInstanceMap != null && creationalContextMap != null;
    }

// --------------------- Interface ManagedContext ---------------------

    public void activate()
    {
        componentInstanceMap = new ConcurrentHashMap<Contextual<?>, Object>();
        creationalContextMap = new ConcurrentHashMap<Contextual<?>, CreationalContext<?>>();
    }

    public void deactivate()
    {
        if (componentInstanceMap != null) {
            for (Map.Entry<Contextual<?>, Object> componentEntry : componentInstanceMap.entrySet()) {
                /*
                * No way to inform the compiler of type <T> information, so it has to be abandoned here :(
                */
                Contextual contextual = componentEntry.getKey();
                Object instance = componentEntry.getValue();
                CreationalContext creational = creationalContextMap.get(contextual);
                //noinspection unchecked
                contextual.destroy(instance, creational);
            }
        }
        componentInstanceMap = null;
    }

    public void invalidate()
    {
//        TODO what should be done here?
    }

    private void assertActive()
    {
        if (!isActive()) {
            throw new ContextNotActiveException("Context with scope annotation @ViewScoped is not active with respect to the current thread");
        }
    }
}
