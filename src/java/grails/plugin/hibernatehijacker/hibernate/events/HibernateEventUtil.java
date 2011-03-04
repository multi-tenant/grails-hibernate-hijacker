package grails.plugin.hibernatehijacker.hibernate.events;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deals with all the painful things involved with adding Hibernate event listeners.
 * @see http://www.docjar.com/html/api/org/hibernate/event/EventListeners.java.html
 * @author Kim A. Betti
 */
public class HibernateEventUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateEventUtil.class);

    /**
     * Finds all *EventListener interfaces registered with the class
     * and adds them as listeners.
     * @param configuration
     * @param listener
     * @throws Exception
     */
    public static void addListener(EventListeners eventListeners, Object listener) {
        log.debug("Adding event listeners from: {} ", listener);

        try {
            List<Class<?>> listenerInterfaces = findEventListenerInterfaces(listener.getClass());
            for (Class<?> listenerInterface : listenerInterfaces) {
                Object[] existingListeners = getExistingListeners(eventListeners, listenerInterface);
                Object[] newListeners = addToExistingListeners(existingListeners, listenerInterface, listener);
                setListeners(eventListeners, listenerInterface, newListeners);
            }
        } catch (Exception ex) {
            String exMessage = "Unable to add event listeners from instance " + listener;
            throw new RuntimeException(exMessage, ex);
        }
    }

    private static List<Class<?>> findEventListenerInterfaces(Class<?> listenerClass) {
        List<Class<?>> listenerInterfaces = new ArrayList<Class<?>>();
        findEventListenerInterfaces(listenerClass, listenerInterfaces);
        return listenerInterfaces;
    }

    private static void findEventListenerInterfaces(Class<?> listenerClass, List<Class<?>> listenerInterfaces) {
        for (Class<?> listenerInterface : listenerClass.getInterfaces()) {
            String canonicalName = listenerInterface.getCanonicalName();
            if (canonicalName.startsWith("org.hibernate.event.") && canonicalName.endsWith("EventListener")) {
                listenerInterfaces.add(listenerInterface);
            }
        }

        Class<?> parentClass = listenerClass.getSuperclass();
        if (parentClass != null) {
            findEventListenerInterfaces(parentClass, listenerInterfaces);
        }
    }

    private static void setListeners(EventListeners eventListeners, Class<?> listenerInterface, Object[] newListeners) throws Exception {
        for (Method method : eventListeners.getClass().getMethods()) {
            if (method.getName().startsWith("set")) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1 && paramTypes[0].getComponentType() == listenerInterface) {
                    method.invoke(eventListeners, new Object[] { newListeners });
                    return;
                }
            }
        }

        throw new Exception("Unable to set listeners for " + listenerInterface);
    }

    public static void addListener(Configuration configuration, String type, Object listener) {
        log.debug("Adding listener for {}: {}", type, listener);
        EventListeners listeners = configuration.getEventListeners();
        Class<?> listenerClass = listeners.getListenerClassFor(type);
        addListener(configuration, type, listenerClass, listener);
    }

    @SuppressWarnings("unchecked")
    private static <T> void addListener(Configuration configuration, String type, Class<T> listenerClass, Object listener) {
        EventListeners eventListeners = configuration.getEventListeners();

        try {
            T[] existingListeners = getExistingListeners(eventListeners, listenerClass);
            T[] newListeners = (T[]) addToExistingListeners(existingListeners, listenerClass, listener);
            configuration.setListeners(type, newListeners);
        } catch (Exception ex) {
            String message = "Unable to add Hibernate event listener: " + listener + " for type: " + listenerClass.getSimpleName();
            throw new RuntimeException(message, ex);
        }
    }

    private static Object[] addToExistingListeners(Object[] existingListeners, Class<?> listenerInterface, Object newListener) {
        Object[] newListeners = (Object[]) Array.newInstance(listenerInterface, existingListeners.length + 1);
        System.arraycopy(existingListeners, 0, newListeners, 0, existingListeners.length);
        newListeners[existingListeners.length] = newListener;
        return newListeners;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] getExistingListeners(EventListeners eventListeners, Class<T> type) throws Exception {
        for (Method method : eventListeners.getClass().getMethods()) {
            if (method.getName().startsWith("get")) {
                Class<?> returnType = method.getReturnType();
                if (returnType.isArray() && returnType.getComponentType() == type) {
                    return (T[]) method.invoke(eventListeners);
                }
            }
        }

        throw new Exception("Unable to get existing listeners for " + type.getSimpleName());
    }

}
