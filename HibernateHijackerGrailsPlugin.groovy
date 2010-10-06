import grails.plugin.hibernatehijacker.spring.*;

class HibernateHijackerGrailsPlugin {
    
    def version = "0.2"
    def grailsVersion = "1.3.5 > *"
    def dependsOn = [
        'hibernate': '1.3.4 > *',
        'eventing': '0.1 > *'
    ]
    
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
        sessionFactoryPostProcessor(SessionFactoryPostProcessor)
    }
    
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    def doWithWebDescriptor = { xml -> }
    
}
