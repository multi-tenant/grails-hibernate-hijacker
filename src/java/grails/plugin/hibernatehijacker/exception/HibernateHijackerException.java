package grails.plugin.hibernatehijacker.exception;

/**
 * 
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class HibernateHijackerException extends RuntimeException {

    public HibernateHijackerException() {
    }

    public HibernateHijackerException(String message) {
        super(message);
    }

    public HibernateHijackerException(Throwable cause) {
        super(cause);
    }

    public HibernateHijackerException(String message, Throwable cause) {
        super(message, cause);
    }

}