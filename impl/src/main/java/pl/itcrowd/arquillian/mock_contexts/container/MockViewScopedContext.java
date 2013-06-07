package pl.itcrowd.arquillian.mock_contexts.container;

import javax.faces.bean.ViewScoped;
import java.lang.annotation.Annotation;

public class MockViewScopedContext extends AbstractMockContext {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Context ---------------------

    public Class<? extends Annotation> getScope()
    {
        return ViewScoped.class;
    }
}
