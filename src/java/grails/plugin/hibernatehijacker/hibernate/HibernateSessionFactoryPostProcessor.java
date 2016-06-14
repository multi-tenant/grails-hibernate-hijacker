package grails.plugin.hibernatehijacker.hibernate;

import org.hibernate.SessionFactory;

/**
 * Spring beans implementing this interface will be able to participate
 * in post processing of Session Factory.
 *
 * @author Sandeep Poonia
 */
public interface HibernateSessionFactoryPostProcessor {

    /**
     * Implement this method to manipulate the Hibernate Configuration.
     *
     * @param factory Hibernate SessionFactory
     */
    void doPostProcessing(final SessionFactory factory);

}