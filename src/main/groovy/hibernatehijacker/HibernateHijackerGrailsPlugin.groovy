package hibernatehijacker
import grails.plugin.hibernatehijacker.hibernate.SessionFactoryProxyFactory
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventListener
import grails.plugin.hibernatehijacker.hibernate.HibernateEventSubscriptionFactory
import grails.plugin.hibernatehijacker.indexdsl.IndexDslPostProcessor
import grails.plugin.hibernatehijacker.spring.SessionFactoryPostProcessor
import grails.plugin.hibernatehijacker.template.HibernateTemplates
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventPropertyUpdater
import grails.plugins.*

class HibernateHijackerGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
     def author = "Kim A. Betti"
    def authorEmail = "kim.betti@gmail.com"
    def title = "Hibernate Hijacker"
   
    def description = """
This plugin is a part of the re-engineering efforts going into the Multi-Tenant plugin.
It is very difficult to intercept new Hibernate sessions in a non-intrusive way.
Multiple plugins trying to archive this are likely to step on each others feet.
This plugin publishes intercepted Session instances to a lightweight event broker.
"""
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/hibernate-hijacker"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
def license = "APACHE"
//    def developers = [[name: "Joe Bloggs", email: "joe@bloggs.net"]]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/multi-tenant/grails-hibernate-hijacker/issues']
    def scm = [url: 'https://github.com/multi-tenant/grails-hibernate-hijacker']

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() { {->
            // TODO Implement runtime spring config (optional)
            // Responsible for wrapping the real SessionFactory instance
        // inside a JDK proxy so we can intercept new sessions.
        sessionFactoryProxyFactory(SessionFactoryProxyFactory) {
            
            eventBroker = ref("eventBroker")
        }

        // Register the Hibernate event listener responsible for proxying
        // Hibernate events to more accessible Hawk events.
        hibernateEventListener(HibernateEventSubscriptionFactory) {
            eventListener = { HibernateEventListener listener ->
                eventBroker = ref("eventBroker")
            }
        }

        // Reads composite database indexes from domain
        // classes with a static indexes property
        indexDslConfigurator(IndexDslPostProcessor)

        // Responsible replacing the sessionFactory
        // with our WrappedSessionFactoryBean
        sessionFactoryPostProcessor(SessionFactoryPostProcessor)

        // Implements withTransaction and withNewSession
        hibernateTemplates(HibernateTemplates) {
//            transactionManager = ref("transactionManager")
//            sessionFactory = ref("sessionFactory")
        }

        // Provides a convenient way of updating entity data
        // before it's persisted to the database.
        hibernateEventPropertyUpdater(HibernateEventPropertyUpdater)
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
