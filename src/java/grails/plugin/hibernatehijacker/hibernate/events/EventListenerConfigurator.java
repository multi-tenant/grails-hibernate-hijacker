package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.eventing.EventBroker;
import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;

/**
 * 
 * @author Kim A. Betti
 */
public class EventListenerConfigurator implements HibernateConfigPostProcessor {
    
    public static final String EVENT_NAMES = "autoFlush,delete,dirtyCheck,evict,flush,flushEntity,"
        + "load,initializeCollection,lock,merge,persist,postDelete,postInsert,postLoad,postUpdate,"
        + "preDelete,preInsert,preLoad,preUpdate,refresh,replicate,saveOrUpdate";
    
    private EventBroker eventBroker;

    @Override
    public void doPostProcessing(final Configuration configuration) throws HibernateException {
        EventListener listener = new EventListener(eventBroker);
        EventListeners eventListeners = configuration.getEventListeners();
        addListenersByName(eventListeners, listener);
    }
    
    private void addListenersByName(EventListeners eventListeners, EventListener listener) {
        for (String eventName : EVENT_NAMES.split("\\,")) 
            HibernateEventUtil.addListener(eventListeners, eventName, listener);
    }
 
    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }
    
}