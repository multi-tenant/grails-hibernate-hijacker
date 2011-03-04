package grails.plugin.hibernatehijacker.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

/**
 * Spring beans implementing this interface will be able to participate
 * in post processing of Hibernate's Configuration.
 * 
 * @author Kim A. Betti
 */
public interface HibernateConfigPostProcessor {

    /**
     * Implement this method to manipulate the Hibernate Configuration.
     * @param configuration Hibernate configuration
     * @throws HibernateException
     */
    void doPostProcessing(final Configuration configuration) throws HibernateException;

}