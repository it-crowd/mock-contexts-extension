package pl.com.it_crowd.arquillian.mock_contexts.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.com.it_crowd.arquillian.mock_contexts.ConversationScopeRequired;
import pl.com.it_crowd.arquillian.mock_contexts.ViewScopeRequired;
import pl.com.it_crowd.arquillian.mock_contexts.container.MockConversation;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class EnterpriseArchiveTest {
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
        String beansDescriptor = Descriptors.create(BeansDescriptor.class)
            .createAlternatives()
            .clazz(MockConversation.class.getCanonicalName())
            .up()
            .exportAsString();
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, EnterpriseArchiveTest.class.getSimpleName() + ".war")
            .addAsWebInfResource(new StringAsset(beansDescriptor), "beans.xml")
            .addClass(EnterpriseArchiveTest.class)
            .addClass(ViewScopedComponent.class);

        beansDescriptor = Descriptors.create(BeansDescriptor.class).createAlternatives().clazz(MockConversation.class.getCanonicalName()).up().exportAsString();
        final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, SampleTest.class.getSimpleName() + ".jar")
            .addAsManifestResource(new StringAsset(beansDescriptor), "beans.xml")
            .addClass(ConversationalComponent.class);
        /**
         * Beware! jars that don't contain ejb's won't be scanned even by CDI
         */
        return ShrinkWrap.create(EnterpriseArchive.class, EnterpriseArchiveTest.class.getSimpleName() + ".ear")
            .addAsManifestResource("META-INF/jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
            .addAsLibraries(Testable.archiveToTest(javaArchive))
            .addAsModule(Testable.archiveToTest(webArchive));
    }

// -------------------------- OTHER METHODS --------------------------

    @ConversationScopeRequired
    @Test
    public void conversationScopedBeanTest()
    {
        Assert.assertEquals(0, conversationalComponent.getIndex());
        conversationalComponent.setIndex(1);
        Assert.assertEquals(1, conversationalComponent.getIndex());
        Assert.assertTrue(conversationalComponent.getConversation().isTransient());
        Assert.assertTrue(conversation.isTransient());
        conversationalComponent.init();
        Assert.assertFalse(conversationalComponent.getConversation().isTransient());
//        Watch out! injected conversation is the one from WAR and it's different one then in JAR in EAR!
        Assert.assertTrue(conversation.isTransient());
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
