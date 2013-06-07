package pl.itcrowd.arquillian.mock_contexts;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD})
public @interface MockFacesContextProducer {
// ------------------------------ FIELDS ------------------------------
    static final String DEFAULT_NAME = "DEFAULT";

// -------------------------- OTHER METHODS --------------------------

    String name() default DEFAULT_NAME;
}
