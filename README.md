The Hibernate Hijacker's Guide to the Galaxy
============================================

This plugins are a part of the re-engineering efforts going into the [Multi-Tenant plugin](http://grails.org/plugin/multi-tenant). It is very difficult to intercept new Hibernate sessions in a non-intrusive way. Multiple plugins trying to archive this are likely to step on each other's feet. This plugin publishes intercepted [Session](http://docs.jboss.org/hibernate/core/3.5/api/org/hibernate/Session.html) instances to a [lightweight event broker](http://github.com/multi-tenant/grails-eventing) that multiple plugins can subscribe to. 

Dependencies
------------

 * The Hibernate plugin (dah..)
 * [Hawk Eventing](http://github.com/multi-tenant/grails-hawk-eventing)

How does it work?
-----------------

[See this post for a summary of some of the difficulties involved with this](http://grails.1312388.n4.nabble.com/Why-aren-t-the-session-bound-to-thread-during-requests-when-I-proxy-the-sessionFactory-bean-td2893502.html#a2893502).

Hibernate's SessionFactory is constructed as usual and wrapped with a proxy. The proxy does two things:

 1. Publishes session instances returned by the openSession methods via the event broker. 
 2. Intercepts calls to getCurrentSession and delegates them to an instance of CurrentSessionContext we have initiated with a reference to the SessionFactory **proxy** instead of the actual object. This is great because it allows us to support webflows without introducing a compile time dependency to the webflow plugin. 

Hibernate events
----------------

I was a little unsure whether this was within the scope of this plugin or not. Grails 1.3.4 and [JIRA 5725](http://jira.codehaus.org/browse/GRAILS-5725) made it a bit easier to add Hibernate event listeners, but this solution is a bit brittle and not very dynamic. If multiple plugins and/or applications declare this Spring bean they'll end up overwriting each other. So in the end I decided to implement this functionality. 

It's implementation is very similar to the one found in FalconeUtil, but with a few exceptions. Sessions are not wrapped so there are no events for things like new criteria. 

Have a look at the tests for examples. 

Participating in post processing of Hibernate's Configuration
-------------------------------------------------------------

This is a very convenient way of doing things like adding Hibernate filters. No need to specify a Hibernate configClass and risk having another plugin replacing it. 

The only thing needed to participate in the post processing of Hibernate's Configuration is to create a Spring bean implementing HibernateConfigPostProcessor. Your bean will be invoked when during construction of the SessionFactory.

Warning
--------

This plugin is a work in progress and is very likely to contain bugs. Expect the API to break. 

Subscribing to Hibernate sessions from other plugins
---------------------------------------------------

One way is to add an doWithEvents closure to the plugin description file (...GrailsPlugin.groovy)

	def doWithEvents = { ApplicationContext ctx ->
	
		hibernate.sessionCreated { Event event -> 
			// Do something with the session (event.payload)
		}
		
	}

Changelog
---------

### v0.2.5 - November the 28th. 2010

 * Integrated Hawk Eventing v0.4

Roadmap / todo:
---------------

 * Add logging for simplified debugging
 * Configuration option for turning of Hibernate event listening
 * More tests (a lot of these things are difficult to test)
 * Find better names for some of the classes / concepts.
	
More information
----------------

Have a look at the tests
