package pl.itcrowd.arquillian.mock_contexts.test;

import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ViewScoped
public class ViewScopedComponent implements Serializable {
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
