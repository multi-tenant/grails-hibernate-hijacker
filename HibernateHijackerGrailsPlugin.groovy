import grails.plugin.hibernatehijacker.hibernate.SessionFactoryProxyFactory
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventListener
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventPropertyUpdater
import grails.plugin.hibernatehijacker.indexdsl.IndexDslPostProcessor
import grails.plugin.hibernatehijacker.spring.HibernateEventSubscriptionFactory
import grails.plugin.hibernatehijacker.spring.SessionFactoryPostProcessor
import grails.plugin.hibernatehijacker.template.HibernateTemplates

class HibernateHijackerGrailsPlugin {
    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "**/demo/**"
    ]

    // TODO Fill in these fields
    def title = "Hibernate Hijacker" // Headline display name of the plugin
    def author = "Sandeep Poonia"
    def authorEmail = "sandeep.poonia.90@gmail.com"
    def description = '''\
This plugin is a part of the re-engineering efforts going into the Multi-Tenant plugin.
It is very difficult to intercept new Hibernate sessions in a non-intrusive way.
Multiple plugins trying to archive this are likely to step on each others feet.
This plugin publishes intercepted Session instances to a lightweight event broker.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/spoonia/grails-hibernate-hijacker"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: 'GitHub', url: 'https://github.com/spoonia/grails-hibernate-hijacker/issues']

    // Online location of the plugin's browseable source code.
    def scm = [url: 'https://github.com/spoonia/grails-hibernate-hijacker']

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
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
            transactionManager = ref("transactionManager")
            sessionFactory = ref("sessionFactory")
        }

        // Provides a convenient way of updating entity data
        // before it's persisted to the database.
        hibernateEventPropertyUpdater(HibernateEventPropertyUpdater)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
