package com.bistro.shared.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayJpaDependencyConfig implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory.containsBeanDefinition("entityManagerFactory")
                && beanFactory.containsBeanDefinition("flywayInitializer")) {
            beanFactory.getBeanDefinition("entityManagerFactory")
                    .setDependsOn("flywayInitializer");
        }
    }
}
