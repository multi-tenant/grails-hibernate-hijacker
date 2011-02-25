package grails.plugin.hibernatehijacker.spring;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;
import grails.plugin.hibernatehijacker.hibernate.WrappedSessionFactoryBean;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Ordered;

/**
 * Replaces the default ConfigurableLocalSessionFactoryBean with WrappedSessionFactoryBean.
 * It will also make sure that our replacement is wired with the required dependencies.
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class SessionFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

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
     * Looks up all beans implementing HibernateConfigPostProcessor and makes
     * sure that they're injected into WrappedSessionFactoryBean
     * 
     * @param beanFactory
     * @param properties
     */
    private void setHibernateConfigPostProcessors(ConfigurableListableBeanFactory beanFactory, MutablePropertyValues properties) {
        Collection<HibernateConfigPostProcessor> configPostProcessors = beanFactory.getBeansOfType(HibernateConfigPostProcessor.class).values();

        PropertyValue property = new PropertyValue("hibernateConfigPostProcessors", configPostProcessors);
        properties.addPropertyValue(property);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}