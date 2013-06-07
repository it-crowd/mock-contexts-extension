package pl.itcrowd.arquillian.mock_contexts.test;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import java.io.Serializable;

@ConversationScoped
public class ConversationalComponent implements Serializable {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private Conversation conversation;

    private int index;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Conversation getConversation()
    {
        return conversation;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

// -------------------------- OTHER METHODS --------------------------

    public void init()
    {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }
}
