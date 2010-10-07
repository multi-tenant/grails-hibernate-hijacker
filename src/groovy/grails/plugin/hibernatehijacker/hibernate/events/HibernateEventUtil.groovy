package grails.plugin.hibernatehijacker.hibernate.events

import org.hibernate.event.EventListeners

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Mar 22, 2009
 * Time: 12:26:11 AM
 */
public class HibernateEventUtil {
    
    public static void addListener(EventListeners eventListeners, String type, def listener) {
        Object[] listeners = eventListeners."${type}EventListeners"
        Object[] expandedListeners = new Object[listeners.length + 1];
        System.arraycopy(listeners, 0, expandedListeners, 0, listeners.length)
        expandedListeners[-1] = listener;
        eventListeners."${type}EventListeners" = expandedListeners
    }

}