package grails.plugin.hibernatehijacker.hibernate;

import grails.plugin.eventing.EventBroker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.codehaus.groovy.grails.orm.hibernate.ConfigurableLocalSessionFactoryBean;
import org.hibernate.SessionFactory;
import org.hibernate.context.CurrentSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.SessionFactoryImpl;
import org.springframework.orm.hibernate3.SpringSessionContext;

/**
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class WrappedSessionFactoryBean extends ConfigurableLocalSessionFactoryBean {

    private EventBroker eventBroker;
    
    /**
     * If no plugins has specified another CurrentSessionContextClass 
     * (like the webflow plugin) we'll use SpringSessionContext.
     */
    private Class<? extends CurrentSessionContext> currentSessionContextClass = SpringSessionContext.class;
    
    @Override
    protected SessionFactory buildSessionFactory() throws Exception {
        setExposeTransactionAwareSessionFactory(false);
        SessionFactory realSessionFactory = super.buildSessionFactory();
        return createSessionFactoryProxy(realSessionFactory);
    }

    private SessionFactory createSessionFactoryProxy(final SessionFactory realSessionFactory) throws Exception {
        SessionFactoryInvocationHandler handler = new SessionFactoryInvocationHandler(realSessionFactory, eventBroker);
        SessionFactoryImplementor sessionFactoryProxy = createSessionFactoryProxy(handler);
        CurrentSessionContext defaultContext = createCurrentSessionContextInstance(sessionFactoryProxy);
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
    private CurrentSessionContext createCurrentSessionContextInstance(SessionFactoryImplementor sessionFactoryProxy) throws Exception {
        Constructor<? extends CurrentSessionContext> constructor = 
            currentSessionContextClass.getConstructor(SessionFactoryImplementor.class);
        
        return constructor.newInstance(sessionFactoryProxy);
    }
 
    /**
     * Make sure not to pass the argument to ConfigurableLocalSessionFactoryBean.
     * We don't want Hibernate to make an instance of the CurrentSessionContext. 
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setCurrentSessionContextClass(Class<?> currentSessionContextClass) {
        this.currentSessionContextClass = (Class<? extends CurrentSessionContext>) currentSessionContextClass;
    }
    
    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }
    
}