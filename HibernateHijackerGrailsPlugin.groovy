import grails.plugin.hibernatehijacker.spring.*
import grails.plugin.hibernatehijacker.template.*
import grails.plugin.hibernatehijacker.hibernate.*
import grails.plugin.hibernatehijacker.hibernate.events.*
import grails.plugin.hibernatehijacker.indexdsl.*

class HibernateHijackerGrailsPlugin {
  
    def version = "0.8.1"

    def grailsVersion = "1.3.5 > *"
    def dependsOn = [ : ]
    
    /* The webflow plugin replaces SpringSessionContext. 
     * By loading our plugin after webflow (if it's installed) we can detect this
     * and create an instance of FlowAwareCurrentSessionContext instead of SpringSessionContext. 
     */
    def loadAfter = [ 'webflow' ]
    
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "**/demo/**"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim.betti@gmail.com"
    def title = "Hibernate Hijacker"
    def documentation = "http://grails.org/plugin/hibernate-hijacker"
    def description = '''\\
This plugins are a part of the re-engineering efforts going into the Multi-Tenant plugin.
It is very difficult to intercept new Hibernate sessions in a non-intrusive way.
Multiple plugins trying to archive this are likely to step on each others feet.
This plugin publishes intercepted Session instances to a lightweight event broker. 
'''

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
    
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    def doWithWebDescriptor = { xml -> }
    
}
