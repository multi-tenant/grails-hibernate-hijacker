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

    void doPostProcessing(final Configuration configuration) throws HibernateException;

}