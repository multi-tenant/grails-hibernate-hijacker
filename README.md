The Hibernate Hijacker's Guide to the Galaxy
============================================

This plugin is a part of the re-engineering efforts going into the [Multi-Tenant plugin](http://grails.org/plugin/multi-tenant). It is very difficult to intercept new Hibernate sessions in a non-intrusive way. Multiple plugins trying to archive this are likely to step on each other's feet. This plugin publishes intercepted [Session](http://docs.jboss.org/hibernate/core/3.5/api/org/hibernate/Session.html) instances to [Hawk Eventing](http://github.com/multi-tenant/grails-hawk-eventing), a lightweight event broker, that multiple plugins can subscribe to. 

The documentation is now hosted at [GitHub Pages](https://github.com/spoonia/grails-hibernate-hijacker).

Current Issues:
---
* Other plugins will not be able to set configClass in dataSource block. So grails-hibernate-filter plugin might not work.
* Not an easy way to create sessionFactory proxy. As [GrailsHibernateTemplate](org.codehaus.groovy.grails.orm.hibernate.GrailsHibernateTemplate) constructor uses [SessionFactoryImpl](org.hibernate.internal.SessionFactoryImpl) which is a final class instead of the interface. Which makes it difficult to pass a proxy object of SessionFactoryImplementor.  