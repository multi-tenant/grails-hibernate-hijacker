package grails.plugin.hibernatehijacker.hibernate.events;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Deals with all the painful things involved with adding Hibernate event listeners.
 *
 * @author Kim A. Betti
 */
public class HibernateEventUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateEventUtil.class);

    /**
     * Finds all *EventListener interfaces registered with the class
     * and adds them as listeners.
     *
     * @param sessionFactory
     * @param listener
     * @throws Exception
     */
    public static void addListener(SessionFactory sessionFactory, Object listener) {
        log.debug("Adding event listeners from: {} ", listener);

        try {
            List<EventType> listenerEventTypes = findListenerEventTypes(listener.getClass());
            for (EventType listenerEventType : listenerEventTypes) {
                addListener(sessionFactory, listenerEventType, listener);
            }
        } catch (Exception ex) {
            String exMessage = "Unable to add event listeners from instance " + listener;
            throw new RuntimeException(exMessage, ex);
        }
    }

    private static List<EventType> findListenerEventTypes(Class<?> listenerClass) {
        List<EventType> listenerEventTypes = new ArrayList<EventType>();
        findListenerEventTypes(listenerClass, listenerEventTypes);
        return listenerEventTypes;
    }

    private static void findListenerEventTypes(Class<?> listenerClass, List<EventType> listenerEventTypes) {
        for (Class<?> listenerInterface : listenerClass.getInterfaces()) {
            String canonicalName = listenerInterface.getCanonicalName();
            if (canonicalName.startsWith("org.hibernate.event.spi.") && canonicalName.endsWith("EventListener")) {
                for (EventType eventType : EventType.values()) {
                    if (eventType.baseListenerInterface().getCanonicalName().equalsIgnoreCase(canonicalName)) {
                        listenerEventTypes.add(eventType);
                    }
                }
            }
        }

        Class<?> parentClass = listenerClass.getSuperclass();
        if (parentClass != null) {
            findListenerEventTypes(parentClass, listenerEventTypes);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addListener(SessionFactory sessionFactory, EventType listenerEventType, Object newListener) {
        try {
            EventListenerRegistry registry = ((SessionFactoryImplementor) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
            registry.getEventListenerGroup(listenerEventType).appendListener(newListener);
        } catch (Exception ex) {
            ex.printStackTrace();
            String message = "Unable to add Hibernate event listener: " + newListener + " for type: " + listenerEventType.eventName();
            throw new RuntimeException(message, ex);
        }
    }
}
