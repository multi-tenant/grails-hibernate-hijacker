package grails.plugin.hibernatehijacker.demo

import spock.lang.*
import grails.plugin.spock.*
import grails.plugin.eventing.*
import org.hibernate.Session

/**
 *
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class BookSpec extends IntegrationSpec {
    
    def eventBroker
    
    def "Hibernate sessions are intercepted"() {
        
        given: "A subscription to new Hibernate sessions"
        def sessionConsumer = Mock(EventConsumer)
        eventBroker.subscribe("hibernate.sessionCreated", sessionConsumer)
        
        when: "We make Hibernate create three new sessions"
        3.times { n ->
            Book.withNewSession {
                new Book(title: "Book $n").save(flush: true)
            }
        }
        
        then: "At least three sessions are intercepted"
        (3.._) * sessionConsumer.consume(_ as Session, _)
        
    }
    
}