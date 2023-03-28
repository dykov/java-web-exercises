package com.bobocode;

import com.bobocode.annotation.Trimmed;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * This is processor class implements {@link BeanPostProcessor}, looks for a beans where method parameters are marked with
 * {@link Trimmed} annotation, creates proxy of them, overrides methods and trims all {@link String} arguments marked with
 * {@link Trimmed}. For example if there is a string " Java   " as an input parameter it has to be automatically trimmed to "Java"
 * if parameter is marked with {@link Trimmed} annotation.
 * <p>
 * <p>
 * Note! This bean is not marked as a {@link Component} to avoid automatic scanning, instead it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported to a {@link Configuration} class by annotation
 * {@link EnableStringTrimming}
 */
public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, Class<?>> beansWithTrimmedAnnot = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        if (hasTrimmedAnnotatedMethodParameters(bean)) {
            beansWithTrimmedAnnot.put(beanName, bean.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> classToProxy = beansWithTrimmedAnnot.get(beanName);
        if (classToProxy != null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(classToProxy);
            enhancer.setCallback(trimmingInterceptor());
            return enhancer.create();
        }
        return bean;
    }

    private MethodInterceptor trimmingInterceptor() {
        return (originalBean, method, methodArgs, methodProxy) ->
                methodProxy.invokeSuper(originalBean, trimParams(method, methodArgs));
    }

    private Object[] trimParams(Method method, Object[] methodArgs) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Trimmed.class)) {
                methodArgs[i] = trimMarkedString(methodArgs[i]);
            }
        }
        return methodArgs;
    }

    private Object trimMarkedString(Object arg) {
        if (arg instanceof String string) {
            return StringUtils.hasLength(string) ? string.trim() : arg;
        }
        return arg;
    }

    private boolean hasTrimmedAnnotatedMethodParameters(Object bean) {
        return Arrays.stream(bean.getClass().getDeclaredMethods())
                .flatMap(method -> Arrays.stream(method.getParameters()))
                .anyMatch(parameter -> parameter.isAnnotationPresent(Trimmed.class));
    }
}
