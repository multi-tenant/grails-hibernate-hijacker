package grails.plugin.hibernatehijacker.demo

import geb.*
import geb.spock.GebSpec
import grails.core.GrailsApplication
import org.openqa.selenium.WebDriver
import grails.plugins.hawkeventing.*
import org.hibernate.Session
import grails.util.Holders

import grails.test.mixin.integration.Integration
import grails.transaction.*

import spock.lang.*
import geb.spock.*

@Integration
@Rollback
class BookFunctionalSpec extends GebSpec {

    String baseUrl = "http://localhost:8080"

    WebDriver createDriver() {
//        new HtmlUnitDriver()
    }

    def "Book can be persisted"() {

        given: "A subscription to new Hibernate sessions"
        
        def eventBroker = Holders.findApplication().getArtefacts("eventBroker") // DI not working with geb?
        def sessionConsumer = Mock(EventConsumer)
        eventBroker.subscribe("hibernate.sessionCreated", sessionConsumer)

        when:
        to BookCreatePage

        then:
        at BookCreatePage

        when: "We submit the form"
        nameField.value("Groovy in Action")
        createButton.click()

        then: "The book is persisted"
        Book.findByName("Groovy in Action") != null

        and: "We have intercepted at least one session"
        (1.._) * sessionConsumer.consume(_ as Event)
    }

}

class BookCreatePage extends Page {

    static url = "/hibernate-hijacker/book/create"
    static at = { $("input", name: "name") }

    static content = {
        nameField { $("input", name: "name") }
        createButton { $("input", name: "create") }
    }

}
