package grails.plugin.hibernatehijacker.indexdsl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;
import groovy.lang.Closure;

/**
 * Configures Hibernate indexes for domain classes
 * with a static indexes property.
 * 
 * @see HibernateIndexBuilder
 * @author Kim A. Betti
 */
public class IndexDslPostProcessor implements HibernateConfigPostProcessor {

    private GrailsApplication grailsApplication;
    private Logger log = LoggerFactory.getLogger(IndexDslPostProcessor.class);

    @Override
    public void doPostProcessing(Configuration configuration) throws HibernateException {
        for (GrailsDomainClass domainClass : getDomainClasses()) {
            Table table = findTableByName(configuration, domainClass.getName());
            if (table != null) {
                addIndexesFrom(domainClass, table);
            }
        }
    }

    protected List<GrailsDomainClass> getDomainClasses() {
        List<GrailsDomainClass> domainClasses = new ArrayList<GrailsDomainClass>();
        for (GrailsClass domainClass : grailsApplication.getArtefacts("Domain")) {
            domainClasses.add((GrailsDomainClass) domainClass);
        }

        return domainClasses;
    }

    /**
     * TODO: This is might not be good enough if the table is mapped to a different name
     */
    protected Table findTableByName(Configuration configuration, String name) {
        Iterator<?> tableIterator = configuration.getTableMappings();
        while (tableIterator.hasNext()) {
            Table table = (Table) tableIterator.next();
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }

        log.warn("Unable to find Hibernate table for " + name);
        return null;
    }

    protected void addIndexesFrom(GrailsDomainClass domainClass, Table table) {
        Closure indexClosure = (Closure) GrailsClassUtils.getStaticPropertyValue(domainClass.getClazz(), "indexes");
        if (indexClosure != null) {
            log.info("Reading indexes from " + domainClass.getName());
            HibernateIndexBuilder.from(table, indexClosure);
        }
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

}
