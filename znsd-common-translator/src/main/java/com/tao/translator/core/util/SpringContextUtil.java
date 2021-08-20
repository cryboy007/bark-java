package com.tao.translator.core.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 用来获取spring实例
 *
 * @author luozhan
 * @create 2020-04
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext()
            throws BeansException {
        return context;
    }

    public static boolean containsBean(String beanId){
        return context.containsBean(beanId);
    }
    public static Object getBean(String beanId) {
        if ((beanId == null) || (beanId.length() == 0)) {
            return null;
        }
        Object object = null;
        object = context.getBean(beanId);
        return object;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return context.getBean(clazz);
    }

    public static void removeBean(String beanId) {
        if ((beanId == null) || (beanId.isEmpty())) {
            return;
        }
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) context;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts
                .getBeanFactory();
        beanFactory.removeBeanDefinition(beanId);
    }

    public static void removeBeans(String[] beanIds) {
        if ((beanIds == null) || (beanIds.length == 0)) {
            return;
        }
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) context;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts
                .getBeanFactory();
        for (String beanId : beanIds) {
            if ((beanId != null) && (!beanId.isEmpty())
                    && (beanFactory.isBeanNameInUse(beanId))) {
                beanFactory.removeBeanDefinition(beanId);
            }
        }
    }
}
