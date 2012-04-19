package pl.com.it_crowd.arquillian.mock_contexts.container;

import javax.enterprise.context.ConversationScoped;
import java.lang.annotation.Annotation;

public class MockConversationScopedContext extends AbstractMockContext {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Context ---------------------

    @Override
    public Class<? extends Annotation> getScope()
    {
        return ConversationScoped.class;
    }
}
