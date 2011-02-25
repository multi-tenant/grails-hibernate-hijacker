package grails.plugin.hibernatehijacker.hibernate;

import grails.plugins.hawkeventing.EventBroker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.hibernate.SessionFactory;
import org.hibernate.context.CurrentSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.SessionFactoryImpl;

/**
 * This class will create a proxy for Hibernate's session factory.
 * 
 * Important:
 * ----------------
 * This class will create a new instance of CurrentSessionContext.
 * SessionFactoryInovactionHandler will intercept calls to getCurrentSession
 * and return our instance. This way we don't run into the annoying
 * "no session bound to thread" problem.
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class SessionFactoryProxyFactory {

    private EventBroker eventBroker;

    public SessionFactory createSessionFactoryProxy(final SessionFactory realSessionFactory,
            Class<? extends CurrentSessionContext> currentSessionContextClass) throws Exception {

        SessionFactoryInvocationHandler handler = new SessionFactoryInvocationHandler(realSessionFactory, eventBroker);
        SessionFactoryImplementor sessionFactoryProxy = createSessionFactoryProxy(handler);
        CurrentSessionContext defaultContext = createCurrentSessionContextInstance(sessionFactoryProxy, currentSessionContextClass);
        handler.setDefaultCurrentSessionContext(defaultContext);

        return sessionFactoryProxy;
    }

    private SessionFactoryImplementor createSessionFactoryProxy(InvocationHandler handler) {
        Class<?>[] interfaces = SessionFactoryImpl.class.getInterfaces();
        ClassLoader classloader = SessionFactory.class.getClassLoader();
        return (SessionFactoryImplementor) Proxy.newProxyInstance(classloader, interfaces, handler);
    }

    /**
     * Usually we Hibernate is responsible for making an instance of CurrentSessionContext, but that would
     * cause the implementation to contain a reference to the actual SessionFactory and not the proxy.
     * By creating this instance ourself we can make sure to it a reference to the proxy instead.
     */
    private CurrentSessionContext createCurrentSessionContextInstance(SessionFactoryImplementor sessionFactoryProxy,
            Class<? extends CurrentSessionContext> currentSessionContextClass) throws Exception {

        return currentSessionContextClass.getConstructor(SessionFactoryImplementor.class)
        .newInstance(sessionFactoryProxy);
    }

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

}
