Map<String, String> ENV = System.getenv();
grails.project.work.dir = 'target'
String mvnRepoHost = ENV['MVN_REPO_HOST']
String mvnRepoUser = ENV['MVN_REPO_USER']
String mvnRepoPassword = ENV['MVN_REPO_PASSWORD']

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		mavenRepo 'https://repo1.maven.org/maven2/'
		mavenRepo 'https://grails.jfrog.io/grails/plugins'
	}

	dependencies {
		test('org.spockframework:spock-grails-support:0.7-groovy-2.0') {
			excludes 'groovy', 'groovy-all'
		}
	}
	credentials {
		realm = ENV['MVN_REPO_REALM']
		host = mvnRepoHost
		username = mvnRepoUser
		password = mvnRepoPassword
	}
	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		compile ":hibernate:3.6.10.16", {
			export = false
		}

		compile ':hawk-eventing:0.5.1', {
			excludes 'svn'
		}

		test ':spock:0.7', {
			export = false
			exclude 'spock-grails-support'
		}
	}
}

grails.project.repos.releases.url = ENV['MVN_REPO_REPOSITORIES_URL_WARS_RELEASE']
grails.project.repos.releases.username = mvnRepoUser
grails.project.repos.releases.password = mvnRepoPassword

grails.project.repos.snapshots.url = ENV['MVN_REPO_REPOSITORIES_URL_WARS_SNAPSHOT']
grails.project.repos.snapshots.username = mvnRepoUser
grails.project.repos.snapshots.password = mvnRepoPassword

grails.project.repos.default = 'releases'
