The Hibernate Hijacker's Guide to the Galaxy
============================================

This plugin is a part of the re-engineering efforts going into the [Multi-Tenant plugin](http://grails.org/plugin/multi-tenant). It is very difficult to intercept new Hibernate sessions in a non-intrusive way. Multiple plugins trying to archive this are likely to step on each other's feet. This plugin publishes intercepted [Session](http://docs.jboss.org/hibernate/core/3.5/api/org/hibernate/Session.html) instances to [Hawk Eventing](http://github.com/multi-tenant/grails-hawk-eventing), a lightweight event broker, that multiple plugins can subscribe to. 

The documentation is now hosted at [GitHub Pages](http://multi-tenant.github.com/grails-hibernate-hijacker/).
