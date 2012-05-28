package pl.com.it_crowd.arquillian.mock_contexts.test;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.DependencyFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.com.it_crowd.arquillian.mock_contexts.FacesContextRequired;
import pl.com.it_crowd.arquillian.mock_contexts.MockFacesContextProducer;

import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Arquillian.class)
public class MockFacesContextTest {
// ------------------------------ FIELDS ------------------------------

    final Map<Object, Object> attributes = new HashMap<Object, Object>();

    private FacesContext mock;

// -------------------------- STATIC METHODS --------------------------

    @Deployment
    public static Archive getArchive()
    {
        return ShrinkWrap.create(MavenImporter.class, MockFacesContextTest.class.getSimpleName() + ".war")
            .loadEffectivePom("pom.xml")
            .importAnyDependencies(new DependencyFilter("org.mockito:mockito-all"))
            .as(WebArchive.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

// -------------------------- OTHER METHODS --------------------------

    @FacesContextRequired
    @Test
    public void facesContextAvailabile()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNotNull(facesContext);
        Assert.assertSame(mock, facesContext);
        Assert.assertEquals(attributes, facesContext.getAttributes());
    }

    @Test
    public void facesContextNotAvailable()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNull(facesContext);
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
}
