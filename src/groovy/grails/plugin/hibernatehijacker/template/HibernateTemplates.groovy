package grails.plugin.hibernatehijacker.template

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.orm.hibernate4.HibernateCallback
import org.springframework.orm.hibernate4.HibernateTemplate
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

/**
 * Much of this code is from HibernatePluginSupport (by Graeme Rocher) where
 * it's attached to all domain classes meta class. I've moved it in here
 * so it can be used without knowing about a specific domain class at compile time.
 *
 * @author Kim A. Betti
 */
class HibernateTemplates {

    PlatformTransactionManager transactionManager
    SessionFactory sessionFactory

    def withTransaction(Closure callback) {
        withTransaction(TransactionDefinition.PROPAGATION_REQUIRED, callback)
    }

    def withTransaction(int propagationBehavior, Closure callback) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager)
        transactionTemplate.propagationBehavior = propagationBehavior

        transactionTemplate.execute({ status ->
            try {
                callback.call(status)
            } catch (Throwable throwable) {
                status.setRollbackOnly()
                throw throwable
            }
        } as TransactionCallback)
    }

    /**
     * Run a closure in a new session. Taken from HibernatePluginSupport
     * so we can use it without having to know about a domain class at compile time.
     */
    def withNewSession(Closure callback) {
        HibernateTemplate template = new HibernateTemplate(sessionFactory)
        template.execute({ Session session ->
            callback(session)
        } as HibernateCallback)
    }
}
