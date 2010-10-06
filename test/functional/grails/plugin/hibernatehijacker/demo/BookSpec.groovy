package grails.plugin.hibernatehijacker.demo

import geb.*
import geb.spock.GebSpec
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import grails.plugin.eventing.*
import org.hibernate.Session
import org.codehaus.groovy.grails.commons.ApplicationHolder

class BookSpec extends GebSpec {
    
    String baseUrl = "http://localhost:8080"
    
    WebDriver createDriver() {
        new HtmlUnitDriver()
    }
    
    // Doesn't dependency injection work with Geb?
    def eventBroker = ApplicationHolder.application.mainContext.getBean("eventBroker")
    
    def "Book can be persisted"() {
        
        given: "A subscription to new Hibernate sessions"
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
        (1.._) * sessionConsumer.consume(_ as Session, _)
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