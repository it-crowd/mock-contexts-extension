package pl.com.it_crowd.arquillian.mock_contexts;

public class ProducerNotFoundException extends RuntimeException {
// --------------------------- CONSTRUCTORS ---------------------------

    public ProducerNotFoundException(String message)
    {
        super(message);
    }

    public ProducerNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
