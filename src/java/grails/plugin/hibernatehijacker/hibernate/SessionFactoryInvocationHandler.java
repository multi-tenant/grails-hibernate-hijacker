package grails.plugin.hibernatehijacker.hibernate;

import grails.plugins.hawkeventing.EventBroker;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.hibernate.SessionFactory;
import org.hibernate.context.CurrentSessionContext;

/**
 * SessionFactory method invocation will be passing through here.
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class SessionFactoryInvocationHandler implements InvocationHandler {

    public static final String OPEN_SESSION = "openSession";
    public static final String GET_CURRENT_SESSION = "getCurrentSession";
    public static final String INTERCEPTED_SESSION_EVENT_NAME = "hibernate.sessionCreated";

    private SessionFactory realSessionFactory;
    private EventBroker eventBroker;
    private CurrentSessionContext defaultCurrentSessionContext;

    public SessionFactoryInvocationHandler(SessionFactory realSessionFactory, EventBroker eventBroker) {
        this.realSessionFactory = realSessionFactory;
        this.eventBroker = eventBroker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String methodName = method.getName();

        if (methodName.equals(GET_CURRENT_SESSION)) {
            return defaultCurrentSessionContext.currentSession();
        }

        Object returnedInstance = method.invoke(realSessionFactory, args);
        if (methodName.equals(OPEN_SESSION)) {
            eventBroker.publish(INTERCEPTED_SESSION_EVENT_NAME, returnedInstance);
        }

        return returnedInstance;
    }

    public void setDefaultCurrentSessionContext(CurrentSessionContext csc) {
        this.defaultCurrentSessionContext = csc;
    }

}