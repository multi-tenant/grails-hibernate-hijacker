package grails.plugin.hibernatehijacker

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.hibernate.MappingException
import org.hibernate.cfg.Configuration

/**
 * Add Filter to Domain Classes.
 * We are manually setting this class as default db config file. If any other plugin tries to set a different class then that won't work.
 *
 * @author Sandeep Poonia
 */
class HibernateFilterDomainConfiguration extends GrailsAnnotationConfiguration {

    private boolean configLocked

    @Override
    protected void secondPassCompile() throws MappingException {
        super.secondPassCompile()
        if (!configLocked) {
            doConfigPostProcessing(this)
            configLocked = true
        }
    }

    private void doConfigPostProcessing(Configuration configuration) {
        Collection<HibernateConfigPostProcessor> processors = applicationContext.getBeansOfType(HibernateConfigPostProcessor.class).values();
        for (HibernateConfigPostProcessor processor : processors) {
            processor.doPostProcessing(configuration);
        }
    }
}
