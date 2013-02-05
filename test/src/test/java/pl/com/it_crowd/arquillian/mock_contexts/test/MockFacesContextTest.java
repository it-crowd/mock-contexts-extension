package pl.com.it_crowd.arquillian.mock_contexts.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.com.it_crowd.arquillian.mock_contexts.FacesContextRequired;
import pl.com.it_crowd.arquillian.mock_contexts.MockFacesContextProducer;

@RunWith(Arquillian.class)
public class MockFacesContextTest {
// ------------------------------ FIELDS ------------------------------

    final Map<Object, Object> attributes = new HashMap<Object, Object>();

    final Map<Object, Object> otherAttributes = new HashMap<Object, Object>();

    private FacesContext mock;

    private FacesContext otherMock;

// -------------------------- STATIC METHODS --------------------------

    @Deployment
    public static Archive<?> getArchive()
    {
        final MavenDependencyResolver resolver =
                DependencyResolvers.use(MavenDependencyResolver.class)
                .loadMetadataFromPom("pom.xml")
                .goOffline();

        Archive<?> archive = ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(resolver.artifact("org.mockito:mockito-all").resolveAsFiles());

        return archive;
    }

// -------------------------- OTHER METHODS --------------------------

    @FacesContextRequired(name = "other")
    @Test
    public void facesContextAvailabile2()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNotNull(facesContext);
        Assert.assertSame(otherMock, facesContext);
        Assert.assertEquals(otherAttributes, facesContext.getAttributes());
    }

    @Test
    public void facesContextNotAvailable()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNull(facesContext);
    }

    @FacesContextRequired
    @Test
    public void facesContextNr1Availabile1()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNotNull(facesContext);
        Assert.assertSame(mock, facesContext);
        Assert.assertEquals(attributes, facesContext.getAttributes());
    }

    @MockFacesContextProducer
    public FacesContext mockFacesContext()
    {
        if (mock == null) {
            mock = mock(FacesContext.class);
            when(mock.getAttributes()).thenReturn(attributes);
        }
        return mock;
    }

    @MockFacesContextProducer(name = "other")
    public FacesContext otherMockFacesContext()
    {
        if (otherMock == null) {
            otherMock = mock(FacesContext.class);
            when(otherMock.getAttributes()).thenReturn(otherAttributes);
        }
        return otherMock;
    }
}
