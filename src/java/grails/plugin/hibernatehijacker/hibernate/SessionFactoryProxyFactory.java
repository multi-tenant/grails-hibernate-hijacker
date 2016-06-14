package grails.plugin.hibernatehijacker.hibernate;

import grails.plugins.hawkeventing.EventBroker;
import org.codehaus.groovy.grails.orm.hibernate.GrailsSessionContext;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;

/**
 * This class will create a proxy for Hibernate's session factory.
 * <p>
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

    SessionFactory createSessionFactoryProxy(final SessionFactory realSessionFactory,
                                             Class<? extends CurrentSessionContext> currentSessionContextClass) throws Exception {

        SessionFactoryProxy sessionFactoryProxy = createSessionFactoryProxy(realSessionFactory);
        CurrentSessionContext defaultContext = createCurrentSessionContextInstance(sessionFactoryProxy, currentSessionContextClass);
        sessionFactoryProxy.setDefaultCurrentSessionContext(defaultContext);

        return sessionFactoryProxy;
    }

    private SessionFactoryProxy createSessionFactoryProxy(final SessionFactory realSessionFactory) {
        return new grails.plugin.hibernatehijacker.hibernate.SessionFactoryProxy(eventBroker, (SessionFactoryImpl) realSessionFactory);
    }

    /**
     * Usually we Hibernate is responsible for making an instance of CurrentSessionContext, but that would
     * cause the implementation to contain a reference to the actual SessionFactory and not the proxy.
     * By creating this instance ourself we can make sure to it a reference to the proxy instead.
     */
    private CurrentSessionContext createCurrentSessionContextInstance(SessionFactoryImplementor sessionFactoryProxy,
                                                                      Class<? extends CurrentSessionContext> currentSessionContextClass) throws Exception {
        CurrentSessionContext context;

        if (currentSessionContextClass == null) {
            currentSessionContextClass = GrailsSessionContext.class;
        }
        try {
            Constructor<? extends CurrentSessionContext> constructor = currentSessionContextClass.getConstructor(SessionFactoryImplementor.class);
            context = BeanUtils.instantiateClass(constructor, sessionFactoryProxy);
        } catch (NoSuchMethodException e) {
            context = new GrailsSessionContext(sessionFactoryProxy);
        }

        return context;
    }

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

}
