package pl.com.it_crowd.arquillian.mock_contexts.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.com.it_crowd.arquillian.mock_contexts.ViewScoped;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class SampleTest {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private ConversationalComponent conversationalComponent;

    @Inject
    private ViewScopedComponent viewScopedComponent;

// -------------------------- STATIC METHODS --------------------------

    @Deployment
    public static Archive getArchive()
    {
        return ShrinkWrap.create(WebArchive.class, SampleTest.class.getSimpleName() + ".war")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClass(ConversationalComponent.class)
            .addClass(ViewScopedComponent.class);
    }

// -------------------------- OTHER METHODS --------------------------

    @ConversationScoped
    @Test
    public void conversationScopedBeanTest()
    {
        Assert.assertEquals(0, conversationalComponent.getIndex());
        conversationalComponent.setIndex(1);
        Assert.assertEquals(1, conversationalComponent.getIndex());
    }

    @ViewScoped
    @Test
    public void viewScopedBeanTest()
    {
        Assert.assertEquals(0, viewScopedComponent.getIndex());
        viewScopedComponent.setIndex(1);
        Assert.assertEquals(1, viewScopedComponent.getIndex());
    }
}
