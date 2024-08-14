package org.framework;

import org.framework.annotations.*;
import org.framework.aop.AspectProxy;
import org.framework.async.AsyncProxy;
import org.framework.configProperties.ConfigPropertiesProcessor;
import org.framework.pubSub.ApplicationEventPublisher;
import org.framework.schedule.Schedule;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
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
    private static Properties properties = new Properties();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    // Add this method to get the active profile
    private static String getActiveProfile() {
        return properties.getProperty("active.profile");
    }
    public static void run(Class<?> primaryClass, String[] args) {
        try {
            // Load properties from application.properties
            loadProperties();

            processConfiguration(primaryClass);

            instantiateAllAnnotatedClasses(primaryClass);
            instantiateApplicationEventPublisher();


            Object primaryClassInstance = createInstanceWithConstructorDI(primaryClass);
            // Call the constructor injection method first
            performDI(primaryClassInstance);

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
            String activeProfile = getActiveProfile();
//            AspectHandler aspectHandler = new AspectHandler();
            for(SprungClassAnnotation annotation: SprungClassAnnotation.values()){
                Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotation.value());
                for (Class<?> annotatedClass : annotatedClasses) {


                    // Check if the class has a Profile annotation
                    if (annotatedClass.isAnnotationPresent(Profile.class)) {
                        Profile profileAnnotation = annotatedClass.getAnnotation(Profile.class);
                        String profileName = profileAnnotation.name();

                        // If the active profile does not match, skip this class
                        if (!profileName.equals(activeProfile)) {
                            continue;
                        }
                    }

                    //Object object = (Object) annotatedClass.getDeclaredConstructor().newInstance();
                    // Instantiate and apply aspect if needed

                    Object object = createInstanceWithConstructorDI(annotatedClass);

                    if(sprungContext.containsKey(annotation.value())){
                        sprungContext.get(annotation.value()).add(object);
                    }else{
                        List<Object> list = new ArrayList<>();
                        list.add(object);
                        sprungContext.put(annotation.value(), list);
                    }
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

    private static void performDI(Object primaryInstance) {
        try {
            injectConstructors(primaryInstance);
            injectSetters(primaryInstance);
            injectFields(primaryInstance);

            for (Class<?> key : sprungContext.keySet()) {
                List<Object> objects = sprungContext.get(key);
                for (Object object : objects) {
                    injectConstructors(object);
                    injectSetters(object);
                    injectFields(object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void injectFields(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object bean = null;

                if (field.isAnnotationPresent(Qualifier.class)) {
                    // Handle Qualifier annotation
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    bean = getBeanWithQualifier(field.getType(), qualifier.name());
                } else {
                    // Regular Autowired injection
                    Class<?> fieldType = field.getType();
                    bean = getBeanOfType(fieldType);
                }

                if (bean != null) {
                    injectProxyIfNeeded(instance, field, bean);
                } else {
                    System.out.println("No bean found for field: " + field.getName());
                }
            }

            if (field.isAnnotationPresent(Value.class)) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                String propertyValue = properties.getProperty(valueAnnotation.value());
                field.setAccessible(true);
                field.set(instance, convertToFieldType(propertyValue, field.getType()));
            }
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
                Object aspectBean = Proxy.newProxyInstance(
                        fieldTypeBean.getClass().getClassLoader(),
                        fieldTypeBean.getClass().getInterfaces().length > 0 ? fieldTypeBean.getClass().getInterfaces() : new Class<?>[]{fieldTypeBean.getClass()},
                        new AspectProxy(asyncBean, sprungContext.get(Aspect.class))
                );
                field.setAccessible(true);
                field.set(object, aspectBean);
            } else {
                try {
                    Object aspectBean = Proxy.newProxyInstance(
                            fieldTypeBean.getClass().getClassLoader(),
                            fieldTypeBean.getClass().getInterfaces().length > 0 ? fieldTypeBean.getClass().getInterfaces() : new Class<?>[]{fieldTypeBean.getClass()},
                            new AspectProxy(fieldTypeBean, sprungContext.get(Aspect.class))
                    );
                    field.setAccessible(true);
                    field.set(object, aspectBean);
                }catch (Exception e){
                    field.setAccessible(true);
                    field.set(object, fieldTypeBean);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static Object convertToFieldType(String value, Class<?> fieldType) {
        if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            return Double.parseDouble(value);
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        // Add more conversions as needed
        return value;
    }
    public static Object getBeanOfType(Class fieldType) {
        Object bean = null;
        try {
            for (List<Object> objectList : sprungContext.values()) {
                for (Object object : objectList) {
                    Class<?>[] interfaces = object.getClass().getInterfaces();

                    // search for interfaces
                    for (Class<?> theInterface : interfaces) {
                        if (theInterface.getName().contentEquals(fieldType.getName())) {
                            bean = object;
                            return bean;
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

    private static Object getBeanWithQualifier(Class<?> fieldType, String qualifierName) {
        try {
            for (List<Object> objectList : sprungContext.values()) {
                for (Object object : objectList) {
                    if (fieldType.isInstance(object)) {
                        // Check if the class-level @Qualifier annotation matches
                        Qualifier qualifier = object.getClass().getAnnotation(Qualifier.class);
                        if (qualifier != null && qualifier.name().equals(qualifierName)) {
                            return object;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void injectConstructors(Object instance) {
        for (Constructor<?> constructor : instance.getClass().getDeclaredConstructors()) {

            if (constructor.isAnnotationPresent(Autowired.class)) {
                try {
                    Parameter[] parameters = constructor.getParameters();
                    Object[] params = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        Object o = getBeanOfType(parameters[i].getType());
                        params[i] = o;
                    }
                    constructor.setAccessible(true);
                    constructor.newInstance(params);
                    Object newInstance = constructor.newInstance(params);

                    // Replace the old instance with the new instance
                    instance = newInstance;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectSetters(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Autowired.class) && method.getParameterCount() == 1) {
                try {
                    Class<?> paramType = method.getParameterTypes()[0];
                    Object param = getBeanOfType(paramType);

                    method.setAccessible(true);
                    method.invoke(instance, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Object createInstanceWithConstructorDI(Class<?> annotatedClass) {
        for (Constructor<?> constructor : annotatedClass.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                try {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    Object[] params = new Object[paramTypes.length];

                    for (int i = 0; i < paramTypes.length; i++) {
                        params[i] = getBeanOfType(paramTypes[i]);
                    }


                    constructor.setAccessible(true);
                    Object instance = constructor.newInstance(params);
                    return instance;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If no constructor is annotated with @Autowired, try the default constructor
        try {
//            return annotatedClass.getDeclaredConstructor().newInstance();
            Object instance = annotatedClass.getDeclaredConstructor().newInstance();
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void loadProperties() {
        try (InputStream input = SprungApplication.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
