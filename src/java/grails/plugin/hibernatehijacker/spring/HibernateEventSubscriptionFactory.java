package grails.plugin.hibernatehijacker.spring;

import grails.plugin.hibernatehijacker.hibernate.HibernateSessionFactoryPostProcessor;
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Provides a declarative way of adding Hibernate event listeners.
 * Just create a class implementing the event listener interfaces you're interested in
 * and declare the Spring the following way (example from multi-tenant-single-db):
 * <p>
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
 * @author Sandeep Poonia
 */
public class HibernateEventSubscriptionFactory implements FactoryBean<Object>, HibernateSessionFactoryPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(HibernateEventSubscriptionFactory.class);

    private Object eventListener;

    public void doPostProcessing(SessionFactory factory) {
        log.info("Registering Hibernate event listener {}", eventListener.getClass().getName());
        HibernateEventUtil.addListener(factory, eventListener);
    }

    @Override
    public Object getObject() throws Exception {
        return eventListener;
    }

    @Override
    public Class<?> getObjectType() {
        if (eventListener == null) {
            return Object.class;  // during startup, eventListener might not be yet populated
        } else {
            return eventListener.getClass();
        }
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
