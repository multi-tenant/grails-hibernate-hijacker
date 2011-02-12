package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

/**
 * Responsible for adding Hibernate event listeners.
 * 
 * 
 * @see http://www.docjar.com/html/api/org/hibernate/event/EventListeners.java.html
 * @author Kim A. Betti
 */
public class EventListenerConfigurator implements HibernateConfigPostProcessor {

    /**
     * create == persist
     * See the link below for more information on how Hibernate name their event types
     * @see http://www.docjar.com/html/api/org/hibernate/event/EventListeners.java.html
     */
    public static final String EVENT_NAMES = "auto-flush,delete,dirty-check,evict,flush,flush-entity,"
        + "load,load-collection,lock,merge,create,post-delete,post-insert,post-load,post-update,"
        + "pre-delete,pre-insert,pre-load,pre-update,refresh,replicate,save-update";

    private HibernateEventListener hibernateEventListener;

    @Override
    public void doPostProcessing(final Configuration configuration) throws HibernateException {
        registerEventlisteners(configuration);
    }

    private void registerEventlisteners(Configuration configuration) {
        for (String eventName : EVENT_NAMES.split("\\,")) {
            HibernateEventUtil.addListener(configuration, eventName, hibernateEventListener);
        }
    }

    public void setHibernateEventListener(HibernateEventListener hibernateEventListener) {
        this.hibernateEventListener = hibernateEventListener;
    }

}