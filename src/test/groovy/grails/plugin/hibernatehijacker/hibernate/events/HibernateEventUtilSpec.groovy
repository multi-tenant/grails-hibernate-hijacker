package grails.plugin.hibernatehijacker.hibernate.events

import org.hibernate.event.EventListeners
import org.hibernate.event.PostUpdateEvent
import org.hibernate.event.PostUpdateEventListener
import org.hibernate.event.PreUpdateEvent
import org.hibernate.event.PreUpdateEventListener
import org.hibernate.cfg.Configuration

import grails.plugin.spock.UnitSpec

/**
 * @author Kim A. Betti
 */
class HibernateEventUtilSpec extends UnitSpec {

    def "add event listeners from object"() {
        given:
        EventListeners eventListeners = new EventListeners()

        when:
        def listener = new TestListener()
        HibernateEventUtil.addListener(eventListeners, listener)
        def preUpdateListeners = eventListeners.preUpdateEventListeners
        def postUpdateListeners = eventListeners.postUpdateEventListeners

        then: "the pre update listener is added"
        preUpdateListeners.length == 1
        preUpdateListeners[0] == listener

        and: "the post update listener from the base class is added"
        postUpdateListeners.length == 1
        postUpdateListeners[0] == listener
    }

    static class BaseListener implements PreUpdateEventListener {
        boolean onPreUpdate(PreUpdateEvent event) {
        }
    }

    static class TestListener extends BaseListener implements PostUpdateEventListener {
        void onPostUpdate(PostUpdateEvent event) {
        }
    }

}
