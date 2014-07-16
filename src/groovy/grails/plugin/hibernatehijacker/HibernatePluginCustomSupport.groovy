package grails.plugin.hibernatehijacker

import grails.plugin.hibernatehijacker.hibernate.SessionFactoryInvocationHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.orm.hibernate.HibernateDatastore
import org.hibernate.SessionFactory
import org.springframework.context.ApplicationContext

import java.lang.reflect.InvocationHandler

import static java.lang.reflect.Proxy.getInvocationHandler
import static java.lang.reflect.Proxy.isProxyClass
import static org.codehaus.groovy.grails.plugins.orm.hibernate.HibernatePluginSupport.enhanceSessionFactory

class HibernatePluginCustomSupport {

    static final doWithDynamicMethods = { ApplicationContext ctx ->
        def grailsApplication = application
        enhanceSessionFactories(ctx, grailsApplication)
    }

    /**
     * Overriding default HibernatePluginSupport behavior
     * @param ctx
     * @param grailsApplication
     * @param source
     */
    static void enhanceSessionFactories(ApplicationContext ctx, GrailsApplication grailsApplication, source = null) {
        Map<SessionFactory, HibernateDatastore> datastores = [:]

        for (entry in ctx.getBeansOfType(SessionFactory)) {
            SessionFactory sessionFactory = entry.value
            String beanName = entry.key
            String suffix = beanName - 'sessionFactory'
            enhanceSessionFactory sessionFactory, grailsApplication, ctx, suffix, datastores, source

            SessionFactory realSessionFactory = sessionFactory
            while (isProxyClass(realSessionFactory.class)) {
                InvocationHandler handler = getInvocationHandler(realSessionFactory)
                if (handler instanceof SessionFactoryInvocationHandler) {
                    realSessionFactory = handler.realSessionFactory

                    if (!isProxyClass(realSessionFactory.class))
                        enhanceSessionFactory realSessionFactory, grailsApplication, ctx, suffix, datastores, source
                }
            }
        }

        ctx.eventTriggeringInterceptor.datastores = datastores
    }
}
