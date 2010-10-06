package grails.plugin.hibernatehijacker.spring;

import grails.plugin.hibernatehijacker.hibernate.WrappedSessionFactoryBean;
import org.springframework.beans.*;
import org.springframework.beans.factory.config.*;

/**
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
public class SessionFactoryPostProcessor implements BeanFactoryPostProcessor {
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDef = beanFactory.getBeanDefinition("sessionFactory");
        beanDef.setBeanClassName(WrappedSessionFactoryBean.class.getName());
        setBeanProperties(beanDef);
    }

    private void setBeanProperties(BeanDefinition beanDef) {
        MutablePropertyValues properties = beanDef.getPropertyValues();
        setEventBrokerReference(properties);
    }

    private void setEventBrokerReference(MutablePropertyValues properties) {
        BeanReference brokerReference = new RuntimeBeanReference("eventBroker");
        PropertyValue property = new PropertyValue("eventBroker", brokerReference);
        properties.addPropertyValue(property);
    }

}