package pl.itcrowd.arquillian.mock_contexts.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.itcrowd.arquillian.mock_contexts.ConversationScopeRequired;
import pl.itcrowd.arquillian.mock_contexts.ViewScopeRequired;
import pl.itcrowd.arquillian.mock_contexts.container.MockConversation;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class EjbArchiveTest {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private Conversation conversation;

    @Inject
    private ConversationalComponent conversationalComponent;

    @Inject
    private ViewScopedComponent viewScopedComponent;

// -------------------------- STATIC METHODS --------------------------

    @Deployment
    public static Archive getArchive()
    {
        final String beansDescriptor = Descriptors.create(BeansDescriptor.class)
            .createAlternatives()
            .clazz(MockConversation.class.getCanonicalName())
            .up()
            .exportAsString();
        return ShrinkWrap.create(JavaArchive.class, SampleTest.class.getSimpleName() + ".jar")
            .addAsManifestResource(new StringAsset(beansDescriptor), "beans.xml")
            .addClass(ConversationalComponent.class)
            .addClass(ViewScopedComponent.class);
    }

// -------------------------- OTHER METHODS --------------------------

    @ConversationScopeRequired
    @Test
    public void conversationScopedBeanTest()
    {
        Assert.assertEquals(0, conversationalComponent.getIndex());
        conversationalComponent.setIndex(1);
        Assert.assertEquals(1, conversationalComponent.getIndex());
        Assert.assertTrue(conversation.isTransient());
        conversationalComponent.init();
        Assert.assertFalse(conversation.isTransient());
    }

    @ViewScopeRequired
    @Test
    public void viewScopedBeanTest()
    {
        Assert.assertEquals(0, viewScopedComponent.getIndex());
        viewScopedComponent.setIndex(1);
        Assert.assertEquals(1, viewScopedComponent.getIndex());
    }
}
