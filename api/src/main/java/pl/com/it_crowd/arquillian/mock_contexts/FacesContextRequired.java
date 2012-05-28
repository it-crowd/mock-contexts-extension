package pl.com.it_crowd.arquillian.mock_contexts;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD})
public @interface FacesContextRequired {
// -------------------------- OTHER METHODS --------------------------

    String name() default MockFacesContextProducer.DEFAULT_NAME;
}
