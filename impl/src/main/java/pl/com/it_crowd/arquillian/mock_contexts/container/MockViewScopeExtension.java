package pl.com.it_crowd.arquillian.mock_contexts.container;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.bean.ViewScoped;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MockViewScopeExtension implements Extension {

    @SuppressWarnings("UnusedDeclaration")
    private void addScope(@Observes final BeforeBeanDiscovery event)
    {
        event.addScope(ViewScoped.class, true, true);
    }

    private Bean<MockViewScopedContext> getContextBean(final MockViewScopedContext viewContext, BeanManager beanManager)
    {
        final InjectionTarget<MockViewScopedContext> injectionTarget = beanManager.createInjectionTarget(
            beanManager.createAnnotatedType(MockViewScopedContext.class));
        final HashSet<Type> types = new HashSet<Type>();
        types.add(MockViewScopedContext.class);
        final HashSet<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add(new AnnotationLiteral<Default>() {
        });
        qualifiers.add(new AnnotationLiteral<Any>() {
        });
        final Class<MockViewScopedContext> beanClass = MockViewScopedContext.class;
        return new Bean<MockViewScopedContext>() {
            public Set<Type> getTypes()
            {
                return types;
            }

            public Set<Annotation> getQualifiers()
            {
                return qualifiers;
            }

            public Class<? extends Annotation> getScope()
            {
                return Dependent.class;
            }

            public String getName()
            {
                return null;
            }

            public Set<Class<? extends Annotation>> getStereotypes()
            {
                return Collections.emptySet();
            }

            public Class<?> getBeanClass()
            {
                return beanClass;
            }

            public boolean isAlternative()
            {
                return false;
            }

            public boolean isNullable()
            {
                return false;
            }

            public Set<InjectionPoint> getInjectionPoints()
            {
                return injectionTarget.getInjectionPoints();
            }

            public MockViewScopedContext create(CreationalContext<MockViewScopedContext> context)
            {
                return viewContext;
            }

            public void destroy(MockViewScopedContext instance, CreationalContext<MockViewScopedContext> context)
            {
                // Call @PreDestroy
                injectionTarget.preDestroy(instance);
                // Have CDI release the bean
                injectionTarget.dispose(instance);
                // Release any dependent objects
                context.release();
            }
        };
    }

    @SuppressWarnings("UnusedDeclaration")
    private void processAnnotatedType(@Observes final ProcessAnnotatedType<MockViewScopedContext> event)
    {
        event.veto();
    }

    @SuppressWarnings("UnusedDeclaration")
    private void registerContext(@Observes final AfterBeanDiscovery event, BeanManager beanManager)
    {
        final MockViewScopedContext mockViewScopedContext = new MockViewScopedContext();
        final Bean<MockViewScopedContext> contextBean = getContextBean(mockViewScopedContext, beanManager);
        event.addBean(contextBean);
        event.addContext(mockViewScopedContext);
    }
}
