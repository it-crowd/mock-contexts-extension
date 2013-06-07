package pl.itcrowd.arquillian.mock_contexts.container;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMockContext implements Context {
// ------------------------------ FIELDS ------------------------------

    protected Map<Contextual<?>, Object> componentInstanceMap;

    protected Map<Contextual<?>, CreationalContext<?>> creationalContextMap;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Context ---------------------

    public abstract Class<? extends Annotation> getScope();

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

// -------------------------- OTHER METHODS --------------------------

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

    protected void assertActive()
    {
        if (!isActive()) {
            throw new ContextNotActiveException(
                "Context with scope annotation @" + getScope().getSimpleName() + " is not active with respect to the current thread");
        }
    }
}
