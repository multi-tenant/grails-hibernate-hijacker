The Hibernate Hijacker's Guide to the Galaxy
============================================

This plugins are a part of the re-engineering efforts going into the [Multi-Tenant plugin](http://grails.org/plugin/multi-tenant). It is very difficult to intercept new Hibernate sessions in a non-intrusive way. Multiple plugins trying to archive this are likely to step on each other's feet. This plugin publishes intercepted [Session](http://docs.jboss.org/hibernate/core/3.5/api/org/hibernate/Session.html) instances to a [lightweight event broker](http://github.com/multi-tenant/grails-eventing) that multiple plugins can subscribe to. 

Dependencies
------------

 * The Hibernate plugin (dah..)
 * [Grails Eventing](http://github.com/multi-tenant/grails-eventing)

How does it work?
-----------------

[See this post for a summary of some of the difficulties involved with this](http://grails.1312388.n4.nabble.com/Why-aren-t-the-session-bound-to-thread-during-requests-when-I-proxy-the-sessionFactory-bean-td2893502.html#a2893502).

Hibernate's SessionFactory is constructed as usual and wrapped with a proxy. 

 1. Publishes session instances returned by the openSession methods via the event broker. 
 2. Intercepts calls to getCurrentSession and delegates them to an instance of CurrentSessionContext we have initiated with a reference to the SessionFactory **proxy** instead of the actual object. This is great because it allows us to support webflows without introducing a compile time dependency to the webflow plugin. 

Warning
--------

This plugin is a work in progress and is very likely to contain bugs. Expect the API to break. 

Subscribing to Hibernate sessions from other plugins
---------------------------------------------------

One way is to add an doWithEvents closure to the plugin description file (...GrailsPlugin.groovy)

	def doWithEvents = { ApplicationContext ctx ->
	
		hibernate.sessionCreated { org.hibernate.Session session -> 
			// Do something with the session
		}
		
	}
	
More information
----------------

Have a look at the tests
