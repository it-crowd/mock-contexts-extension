package pl.com.it_crowd.arquillian.mock_contexts.test;

import javax.enterprise.context.ConversationScoped;
import java.io.Serializable;

@ConversationScoped
public class ConversationalComponent implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private int index;

// --------------------- GETTER / SETTER METHODS ---------------------

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
}
