package grails.plugin.hibernatehijacker.spring;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.ManagedList;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;
import grails.plugin.hibernatehijacker.hibernate.WrappedSessionFactoryBean;

/**
 * Replaces the default ConfigurableLocalSessionFactoryBean with our
 * WrappedSessionFactoryBean (which extends the default one). 
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class SessionFactoryPostProcessor implements BeanFactoryPostProcessor {
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDef = beanFactory.getBeanDefinition("sessionFactory");
        beanDef.setBeanClassName(WrappedSessionFactoryBean.class.getName());
        setBeanProperties(beanFactory, beanDef);
    }

    private void setBeanProperties(ConfigurableListableBeanFactory beanFactory, BeanDefinition beanDef) {
        MutablePropertyValues properties = beanDef.getPropertyValues();
        setHibernateProxyFactoryReference(properties);
        setHibernateConfigPostProcessors(beanFactory, properties);
    }

    private void setHibernateProxyFactoryReference(MutablePropertyValues properties) {
        RuntimeBeanReference reference = new RuntimeBeanReference("sessionFactoryProxyFactory");
        properties.add("sessionFactoryProxyFactory", reference);
    }
    
    /**
     * Looks up all beans implementing HibernateConfigPostProcessor
     * @param beanFactory
     * @param properties
     */
    private void setHibernateConfigPostProcessors(ConfigurableListableBeanFactory beanFactory, MutablePropertyValues properties) {
        Set<String> configPostProcessorBeanNames 
            = beanFactory.getBeansOfType(HibernateConfigPostProcessor.class).keySet();
        
        List<RuntimeBeanReference> postProcessors = new ManagedList<RuntimeBeanReference>();
        for (String postProcessorBeanName : configPostProcessorBeanNames) {
            postProcessors.add(new RuntimeBeanReference(postProcessorBeanName));
        }
        
        PropertyValue property = new PropertyValue("hibernateConfigPostProcessors", postProcessors);
        properties.addPropertyValue(property);
    }
    
}