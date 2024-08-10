package org.framework;

import org.framework.annotations.*;
import org.framework.async.AsyncProxy;
import org.framework.configProperties.ConfigPropertiesProcessor;
import org.framework.pubSub.ApplicationEventPublisher;
import org.framework.schedule.Schedule;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SprungApplication{
    private static final HashMap<Class<?>, List<Object>> sprungContext = new HashMap<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();;

    public static void run(Class<?> primaryClass, String[] args) {
        try {
            processConfiguration(primaryClass);

            instantiateAllAnnotatedClasses(primaryClass);
            instantiateApplicationEventPublisher();

            Object primaryClassInstance =  primaryClass.getDeclaredConstructor().newInstance();
            // Call the constructor injection method first
            performConstructorDI();
            performFieldDI(primaryClassInstance);
            performSetterDI();

            // do run() for application
            Runnable primary = (Runnable) primaryClassInstance;
            primary.run();
            startAllScheduledMethods();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void instantiateAllAnnotatedClasses(Class<?> primaryClass) {
        try {

            System.out.println(primaryClass.getPackageName());
            Reflections reflections = new Reflections(primaryClass.getPackageName(), SprungApplication.class.getPackageName());

            for(SprungClassAnnotation annotation: SprungClassAnnotation.values()){
                Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotation.value());
                for (Class<?> annotatedClass : annotatedClasses) {
//                    Object object = (Object) annotatedClass.getDeclaredConstructor().newInstance();
                    Object object = createInstanceWithConstructorDI(annotatedClass);
                    if(sprungContext.containsKey(annotation.value())){
                        sprungContext.get(annotation.value()).add(object);
                    }else{
                        List<Object> list = new ArrayList<>();
                        list.add(object);
                        sprungContext.put(annotation.value(), list);
                    }
                    // Adding instances to the context
                }
            }

//            System.out.println(sprungContext);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void startAllScheduledMethods(){
       new Schedule(sprungContext.values().stream().toList());
    }
    private static void processConfiguration(Class<?> primaryClass){
        if (primaryClass.isAnnotationPresent(EnableConfigurationProperties.class)) {
            ConfigPropertiesProcessor configPropertiesProcessor = new ConfigPropertiesProcessor();
            Object object = configPropertiesProcessor.process(primaryClass);
            List<Object> list = new ArrayList<>();
            list.add(object);
            sprungContext.put(ConfigurationProperties.class, list);
        }
    }

    private static void instantiateApplicationEventPublisher(){
        ApplicationEventPublisher publisher = new ApplicationEventPublisher(sprungContext.values().stream().toList());
        if (sprungContext.containsKey(ApplicationEventPublisher.class)) {
            sprungContext.get(ApplicationEventPublisher.class).add(publisher);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(publisher);
            sprungContext.put(ApplicationEventPublisher.class, list);
        }
    }

    // Helper method to create an instance considering constructor DI
    private static Object createInstanceWithConstructorDI(Class<?> clazz) {
        try {
            Constructor<?> autowiredConstructor = findConstructorWithAutowired(clazz);
            if (autowiredConstructor != null) {
                Class<?>[] parameterTypes = autowiredConstructor.getParameterTypes();
                Object[] parameters = Arrays.stream(parameterTypes)
                        .map(SprungApplication::getBeanOfType)
                        .toArray();
                autowiredConstructor.setAccessible(true);
                return autowiredConstructor.newInstance(parameters);
            } else {
                // Use the default constructor if no @Autowired constructor is found
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate class: " + clazz.getName(), e);
        }
    }

    private static Constructor<?> findConstructorWithAutowired(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }
        return null;
    }

    private static void performConstructorDI() {
        try {
            for (List<Object> objects : sprungContext.values()) {
                for (Object object : objects) {
                    Constructor<?> constructor = findConstructorWithAutowired(object.getClass());
                    if (constructor != null) {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        Object[] parameters = Arrays.stream(parameterTypes)
                                .map(SprungApplication::getBeanOfType)
                                .toArray();
                        constructor.setAccessible(true);
                        constructor.newInstance(parameters);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performFieldDI(Object primaryInstance) {
        try {
            injectFields(primaryInstance);

            for (Class<?> key : sprungContext.keySet()) {
                List<Object> objects = sprungContext.get(key);
                for (Object object : objects) {
                    injectFields(object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void injectFields(Object object) throws IllegalAccessException {
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> fieldType = field.getType();
                    Object fieldTypeBean = getBeanOfType(fieldType);
                    field.setAccessible(true);
                    //check if we should use proxy
                    injectProxyIfNeeded(object,field, fieldTypeBean);

                    System.out.println("Proxy needed");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void injectProxyIfNeeded(Object object,Field field, Object fieldTypeBean) {
        try {
            if (fieldTypeBean.getClass().isAnnotationPresent(EnableAsync.class)) {
                Object asyncBean = Proxy.newProxyInstance(
                        fieldTypeBean.getClass().getClassLoader(),
                        fieldTypeBean.getClass().getInterfaces().length > 0 ? fieldTypeBean.getClass().getInterfaces() : new Class<?>[]{fieldTypeBean.getClass()},
                        new AsyncProxy(fieldTypeBean, executorService)
                );
                field.set(object, asyncBean);
            } else {
                field.set(object, fieldTypeBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void performSetterDI() {
        try {
            for (List<Object> objects : sprungContext.values()) {
                for (Object object : objects) {
                    for (Method method : object.getClass().getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Autowired.class) && method.getName().startsWith("set") && method.getParameterCount() == 1)
                        {
                            Object parameter = getBeanOfType(method.getParameterTypes()[0]);
                            if (parameter != null) {
                                method.setAccessible(true);
                                method.invoke(object, parameter);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getBeanOfType(Class<?> fieldType) {
        Object bean = null;
        try {
            for (List<Object> objectList : sprungContext.values()) {
                for (Object object : objectList) {
                    Class<?>[] interfaces = object.getClass().getInterfaces();

                    // search for interfaces
                    for (Class<?> theInterface : interfaces) {
                        if (theInterface.getName().contentEquals(fieldType.getName())) {
                            bean = object;
                        }
                    }

                    // use class in no interfaces
                    if (bean == null) {
                        if (object.getClass().getName().contentEquals(fieldType.getName())) {
                            bean = object;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
