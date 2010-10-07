package grails.plugin.hibernatehijacker.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

public interface HibernateConfigPostProcessor {

    void doPostProcessing(final Configuration configuration) throws HibernateException;

}