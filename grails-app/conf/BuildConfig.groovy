grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {

    inherits("global") {
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
        test 'org.seleniumhq.selenium:selenium-htmlunit-driver:2.0a7', { exclude 'xml-apis' }
        //runtime 'mysql:mysql-connector-java:5.1.13'
    }

    plugins { 
        compile ":hawk-eventing:0.5.1"
    }
    
}