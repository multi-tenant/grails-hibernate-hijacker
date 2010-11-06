package grails.plugin.hibernatehijacker.demo

import spock.lang.*
import grails.plugin.spock.*
import grails.plugin.eventing.*

import org.codehaus.groovy.grails.commons.ApplicationHolder;
import org.hibernate.Session
import org.hibernate.event.*

/**
 *
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class BookSpec extends IntegrationSpec {
    
	def eventBroker
	
	def setup() {
		println "eventBroker: " + eventBroker
		assert eventBroker, "EventBroker not found"
	}
    
    def "Hibernate sessions are intercepted"() {
        given: "A subscription to new Hibernate sessions"
        def sessionConsumer = Mock(EventConsumer)
        eventBroker.subscribe("hibernate.sessionCreated", sessionConsumer)
        
        when: "We make Hibernate create three new sessions"
        3.times { n ->
            Book.withNewSession {
                new Book(name: "Book $n")
                    .save(flush: true, failOnError: true)
            }
        }
        
        then: "At least three sessions are intercepted"
        (3.._) * sessionConsumer.consume(_ as Event)
    }
    
    def "Book listeners works"() {
        given: "Subscriptions to the expected events"
	        def eventConsumer = Mock(EventConsumer)
	        eventBroker.subscribe("hibernate.preInsert.book", eventConsumer)
	        eventBroker.subscribe("hibernate.postInsert.book", eventConsumer)
	        eventBroker.subscribe("hibernate.saveOrUpdate.book", eventConsumer)
	        eventBroker.subscribe("hibernate.flushEntity.book", eventConsumer)
	        eventBroker.subscribe("hibernate.flush", eventConsumer)
        
        when: "We insert a new Book"
	        new Book(name: "Groovy in Actoin")
	            .save(flush: true, failOnError: true)
        
        then: "We've intercepted the events we suspected"
	        5 * eventConsumer.consume(_ as Event)
    }
    
}