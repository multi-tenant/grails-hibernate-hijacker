grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
        // excludes 'ehcache'
    }
    
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        
		mavenLocal()
        mavenCentral()
		
        grailsPlugins()
        grailsHome()
        grailsCentral()

        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    
    dependencies {
		test 'apache-httpclient:commons-httpclient:3.1' // Required by Geb..?
        test 'org.seleniumhq.selenium:selenium-htmlunit-driver:2.0a6', {
			exclude 'xml-apis'
        }
		
		
    }
    
	plugins {
		compile ":hawk-eventing:0.4.1"
	}
	
}