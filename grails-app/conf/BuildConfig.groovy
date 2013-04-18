grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}

	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		compile ":hibernate:$grailsVersion", {
			export = false
		}

		compile ':hawk-eventing:0.5.1', {
			excludes 'svn'
		}

		test ':spock:0.7', {
			export = false
		}
	}
}
