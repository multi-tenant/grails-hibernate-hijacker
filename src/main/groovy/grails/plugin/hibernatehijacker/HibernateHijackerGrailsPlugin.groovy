package grails.plugin.hibernatehijacker
import grails.plugin.hibernatehijacker.hibernate.HibernateEventSubscriptionFactory
import grails.plugin.hibernatehijacker.hibernate.SessionFactoryProxyFactory
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventListener
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventPropertyUpdater
import grails.plugin.hibernatehijacker.indexdsl.IndexDslPostProcessor
import grails.plugin.hibernatehijacker.spring.SessionFactoryPostProcessor
import grails.plugin.hibernatehijacker.template.HibernateTemplates
import grails.plugins.*

class HibernateHijackerGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
		"**/demo/**"
    ]

    
    /* The webflow plugin replaces SpringSessionContext.
     * By loading our plugin after webflow (if it's installed) we can detect this
     * and create an instance of FlowAwareCurrentSessionContext instead of SpringSessionContext.
     */
    def loadAfter = ['webflow']


    def author = "Kim A. Betti"
    def authorEmail = "kim.betti@gmail.com"
    def title = "Hibernate Hijacker"
    def documentation = "http://grails.org/plugin/hibernate-hijacker"
    def description = '''\
This plugin is a part of the re-engineering efforts going into the Multi-Tenant plugin.
It is very difficult to intercept new Hibernate sessions in a non-intrusive way.
Multiple plugins trying to archive this are likely to step on each others feet.
This plugin publishes intercepted Session instances to a lightweight event broker.
'''

    def license = "APACHE"
//    def developers = [[name: "Joe Bloggs", email: "joe@bloggs.net"]]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/multi-tenant/grails-hibernate-hijacker/issues']
    def scm = [url: 'https://github.com/multi-tenant/grails-hibernate-hijacker']

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
}
