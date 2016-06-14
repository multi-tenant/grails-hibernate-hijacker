package grails.plugin.hibernatehijacker.hibernate;

import grails.plugin.hibernatehijacker.HibernateFilterDomainConfiguration;
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;
import org.codehaus.groovy.grails.orm.hibernate.ConfigurableLocalSessionFactoryBean;
import org.codehaus.groovy.grails.orm.hibernate.GrailsSessionContext;
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners;
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.event.spi.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * The SessionFactory is still built the usual way, but instead of returning the
 * actual instance we're creating and returning a proxy.
 * <p>
 * Other features like event listeners can be enabled by injecting
 * HibernateConfigurationPostProcessor beans.
 *
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class WrappedSessionFactoryBean extends ConfigurableLocalSessionFactoryBean {

    private static final Logger log = LoggerFactory.getLogger(WrappedSessionFactoryBean.class);

    private SessionFactoryProxyFactory sessionFactoryProxyFactory;
    private Map<String, Object> listenerMap;

    /**
     * If no plugins has specified another CurrentSessionContextClass
     * (like the webflow plugin) we'll use SpringSessionContext.
     */
    private Class<? extends CurrentSessionContext> currentSessionContextClass = GrailsSessionContext.class;

    /**
     * @return Proxied session factory
     */
    @Override
    protected GrailsAnnotationConfiguration newConfiguration() throws Exception {
        super.configClass = HibernateFilterDomainConfiguration.class;
        return super.newConfiguration();
    }

    protected void buildSessionFactoryProxy() {
        try {
            SessionFactory factoryProxy = sessionFactoryProxyFactory.createSessionFactoryProxy(getObject(), currentSessionContextClass);
            Field field = ConfigurableLocalSessionFactoryBean.class.getDeclaredField("sessionFactory");
            ReflectionUtils.makeAccessible(field);
            field.set(this, factoryProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        doSessionFactoryPostProcessing(getObject());
    }

    private void doSessionFactoryPostProcessing(SessionFactory factory) {
        Collection<HibernateSessionFactoryPostProcessor> processors = applicationContext.getBeansOfType(HibernateSessionFactoryPostProcessor.class).values();
        for (HibernateSessionFactoryPostProcessor processor : processors) {
            log.debug("Passing Hibernate configuration and Session Factory to: {}", processor.getClass().getSimpleName());
            processor.doPostProcessing(factory);
        }
        addListenersFromMap(factory);
    }

    /**
     * Important: Do not call super!
     *
     * @param listeners
     */
    @Override
    public void setHibernateEventListeners(final HibernateEventListeners listeners) {
        if (listeners != null) {
            this.listenerMap = listeners.getListenerMap();
        }
    }

    private void addListenersFromMap(SessionFactory factory) {
        if (this.listenerMap != null) {
            for (String type : this.listenerMap.keySet()) {
                Object listener = listenerMap.get(type);
                HibernateEventUtil.addListener(factory, EventType.resolveEventTypeByName(type), listener);
            }
        }
    }

    @Override
    public void setConfigClass(Class<?> configClass) {
        super.setConfigClass(configClass);
    }

    /**
     * Make sure not to pass the argument to ConfigurableLocalSessionFactoryBean.
     * We don't want Hibernate to make an instance of the CurrentSessionContext.
     * <p>
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
}