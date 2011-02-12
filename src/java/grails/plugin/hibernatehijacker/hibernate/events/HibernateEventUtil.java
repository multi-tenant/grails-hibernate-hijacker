package grails.plugin.hibernatehijacker.hibernate.events;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;



/**
 * 
 * @see http://www.docjar.com/html/api/org/hibernate/event/EventListeners.java.html
 * @author Kim A. Betti
 */
public class HibernateEventUtil {

    public static void addListener(Configuration configuration, String type, Object listener) {
        EventListeners listeners = configuration.getEventListeners();
        Class<?> listenerClass = listeners.getListenerClassFor(type);
        addListener(configuration, type, listenerClass, listener);
    }

    @SuppressWarnings("unchecked")
    private static <T> void addListener(Configuration configuration, String type, Class<T> listenerClass, Object listener) {
        EventListeners eventListeners = configuration.getEventListeners();

        try {
            T[] existingListeners = getExistingListeners(eventListeners, listenerClass);
            T[] newListeners = (T[]) Array.newInstance(listenerClass, existingListeners.length + 1);
            System.arraycopy(existingListeners, 0, newListeners, 0, existingListeners.length);

            newListeners[existingListeners.length] = (T) listener;
            configuration.setListeners(type, newListeners);
        } catch (Exception ex) {
            String message = "Unable to add Hibernate event listener: " + listener + " for type: " + listenerClass.getSimpleName();
            throw new RuntimeException(message, ex);
        }
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
