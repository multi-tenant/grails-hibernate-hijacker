package grails.plugin.hibernatehijacker.hibernate;

import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Provides a declarative way of adding Hibernate event listeners.
 * Just create a class implementing the event listener interfaces you're interested in
 * and declare the Spring the following way (example from multi-tenant-single-db):
 * 
 * <pre>
 * doWithSpring = {
 * 
 *     tenantHibernateEventListener(HibernateEventSubscriptionFactory) {
 *          eventListener = { TenantHibernateEventListener listener ->
 *              currentTenant = ref("currentTenant")
 *          }
 *      }
 * 
 * }
 * </pre>
 * 
 * @author Kim A. Betti
 */
public class HibernateEventSubscriptionFactory implements HibernateConfigPostProcessor, FactoryBean<Object> {

    private static final Logger log = LoggerFactory.getLogger(HibernateEventSubscriptionFactory.class);

    private Object eventListener;

    @Override
    public void doPostProcessing(Configuration configuration) throws HibernateException {
        log.info("Registering Hibernate event listener {}", eventListener.getClass().getName());
        EventListeners eventListeners = configuration.getEventListeners();
        HibernateEventUtil.addListener(eventListeners, eventListener);
    }

    @Override
    public Object getObject() throws Exception {
        return eventListener;
    }

    @Override
    public Class<?> getObjectType() {
        return eventListener.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Required
    public void setEventListener(Object eventListener) {
        this.eventListener = eventListener;
    }

}
