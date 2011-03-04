package grails.plugin.hibernatehijacker.hibernate;

import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.grails.orm.hibernate.ConfigurableLocalSessionFactoryBean;
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.CurrentSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SpringSessionContext;

/**
 * The SessionFactory is still built the usual way, but instead of returning the
 * actual instance we're creating and returning a proxy.
 * 
 * Other features like event listeners can be enabled by injecting
 * HibernateConfigurationPostProcessor beans.
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class WrappedSessionFactoryBean extends ConfigurableLocalSessionFactoryBean {

    private static final Logger log = LoggerFactory.getLogger(WrappedSessionFactoryBean.class);

    private SessionFactoryProxyFactory sessionFactoryProxyFactory;
    private List<HibernateConfigPostProcessor> hibernateConfigPostProcessors = new ArrayList<HibernateConfigPostProcessor>();
    private Map<String, Object> listenerMap;

    /**
     * If no plugins has specified another CurrentSessionContextClass
     * (like the webflow plugin) we'll use SpringSessionContext.
     */
    private Class<? extends CurrentSessionContext> currentSessionContextClass = SpringSessionContext.class;

    @Override
    protected SessionFactory buildSessionFactory() throws Exception {
        setExposeTransactionAwareSessionFactory(false);
        SessionFactory realSessionFactory = super.buildSessionFactory();
        return sessionFactoryProxyFactory.createSessionFactoryProxy(realSessionFactory, currentSessionContextClass);
    }

    @Override
    protected void postProcessConfiguration(final Configuration config) throws HibernateException {
        for (HibernateConfigPostProcessor processor : hibernateConfigPostProcessors) {
            log.debug("Passing Hibernate configuration to: {}", processor.getClass().getSimpleName());
            processor.doPostProcessing(config);
        }

        super.postProcessConfiguration(config);
        addListenersFromMap(config);
    }

    /**
     * Important: Do not call super!
     * 
     * @see http://jira.codehaus.org/browse/GRAILS-7211
     * @see http://jira.codehaus.org/browse/GRAILS-5725
     * @param listeners
     */
    @Override
    public void setHibernateEventListeners(final HibernateEventListeners listeners) {
        if (listeners != null) {
            this.listenerMap = listeners.getListenerMap();
        }
    }

    private void addListenersFromMap(Configuration configuration) {
        if (this.listenerMap != null) {
            for (String type : this.listenerMap.keySet()) {
                Object listener = listenerMap.get(type);
                HibernateEventUtil.addListener(configuration, type, listener);
            }
        }
    }

    /**
     * Make sure not to pass the argument to ConfigurableLocalSessionFactoryBean.
     * We don't want Hibernate to make an instance of the CurrentSessionContext.
     * 
     * By listening in on this we're able to support plugins like webflow
     * without introducing a compile time dependency.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setCurrentSessionContextClass(Class<?> currentSessionContextClass) {
        this.currentSessionContextClass = (Class<? extends CurrentSessionContext>) currentSessionContextClass;
    }

    public void setSessionFactoryProxyFactory(SessionFactoryProxyFactory sessionFactoryProxyFactory) {
        this.sessionFactoryProxyFactory = sessionFactoryProxyFactory;
    }

    public void setHibernateConfigPostProcessors(List<HibernateConfigPostProcessor> configPostProcessors) {
        this.hibernateConfigPostProcessors = configPostProcessors;
    }

}