package grails.plugin.hibernatehijacker.indexdsl;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;
import groovy.lang.Closure;

import java.util.Iterator;

import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures Hibernate indexes for domain classes
 * with a static indexes property.
 * 
 * @see HibernateIndexBuilder
 * @author Kim A. Betti
 */
public class IndexDslPostProcessor implements HibernateConfigPostProcessor {

    private static Logger log = LoggerFactory.getLogger(IndexDslPostProcessor.class);

    @Override
    @SuppressWarnings("unchecked")
    public void doPostProcessing(Configuration configuration) throws HibernateException {
        Iterator<PersistentClass> mappingIterator = configuration.getClassMappings();
        while (mappingIterator.hasNext()) {
            PersistentClass persistentClass = mappingIterator.next();
            if (hasIndexClosure(persistentClass)) {
                addIndexesFrom(persistentClass);
            }
        }
    }

    protected void addIndexesFrom(PersistentClass persistentClass) {
        log.info("Reading indexes from " + persistentClass.getClassName());

        Table table = persistentClass.getTable();
        Closure indexClosure = getIndexClosure(persistentClass.getMappedClass());
        HibernateIndexBuilder.from(table, indexClosure);
    }

    protected boolean hasIndexClosure(PersistentClass persistentClass) {
        Class<?> domainClass = persistentClass.getMappedClass();
        Closure indexes = getIndexClosure(domainClass);
        return indexes != null;
    }

    protected Closure getIndexClosure(Class<?> domainClass) {
        return (Closure) GrailsClassUtils.getStaticPropertyValue(domainClass, "indexes");
    }

}
