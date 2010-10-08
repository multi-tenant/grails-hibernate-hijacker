package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;

/**
 * Responsible for adding Hibernate event listeners. 
 * @author Kim A. Betti
 */
public class EventListenerConfigurator implements HibernateConfigPostProcessor {
    
    public static final String EVENT_NAMES = "autoFlush,delete,dirtyCheck,evict,flush,flushEntity,"
        + "load,initializeCollection,lock,merge,persist,postDelete,postInsert,postLoad,postUpdate,"
        + "preDelete,preInsert,preLoad,preUpdate,refresh,replicate,saveOrUpdate";
    
    private HibernateEventListener hibernateEventListener;

    @Override
    public void doPostProcessing(final Configuration configuration) throws HibernateException {
        EventListeners eventListeners = configuration.getEventListeners();
        registerEventlisteners(eventListeners);
    }
    
    private void registerEventlisteners(EventListeners eventListeners) {
        assert hibernateEventListener != null;
        for (String eventName : EVENT_NAMES.split("\\,")) 
            HibernateEventUtil.addListener(eventListeners, eventName, hibernateEventListener);
    }
   
    public void setHibernateEventListener(HibernateEventListener hibernateEventListener) {
        this.hibernateEventListener = hibernateEventListener;
    }
    
}