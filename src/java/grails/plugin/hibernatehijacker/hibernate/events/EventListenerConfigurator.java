package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;

/**
 * Responsible for adding Hibernate event listeners provided by the plugin.
 * Currently we just add one event listener, although this listener is implementing
 * most of the Hibernate event listener interfaces in order to translate them into Hawk events.
 * @author Kim A. Betti
 */
public class EventListenerConfigurator implements HibernateConfigPostProcessor {

    private HibernateEventListener hibernateEventListener;

    @Override
    public void doPostProcessing(final Configuration configuration) throws HibernateException {
        registerEventlisteners(configuration);
    }

    private void registerEventlisteners(Configuration configuration) {
        EventListeners eventListeners = configuration.getEventListeners();
        HibernateEventUtil.addListener(eventListeners, hibernateEventListener);
    }

    public void setHibernateEventListener(HibernateEventListener hibernateEventListener) {
        this.hibernateEventListener = hibernateEventListener;
    }

}