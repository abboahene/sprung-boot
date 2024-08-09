package org.framework;

import org.framework.annotations.Autowired;
import org.framework.annotations.SprungClassAnnotation;
import org.framework.pubSub.ApplicationEventPublisher;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SprungApplication {
    private static final HashMap<Class<?>, List<Object>> sprungContext = new HashMap<>();


    public static void run(Class<?> primaryClass, String[] args) {
        try {
            instantiateAllAnnotatedClasses(primaryClass);
            instantiateApplicationEventPublisher();

            Object primaryClassInstance =  primaryClass.getDeclaredConstructor().newInstance();
            performFieldDI(primaryClassInstance);

            // do run() for application
            Runnable primary = (Runnable) primaryClassInstance;
            primary.run();
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
                    Object object = (Object) annotatedClass.newInstance();
                    if(sprungContext.containsKey(annotation.value())){
                        sprungContext.get(annotation.value()).add(object);
                    }else{
                        List<Object> list = new ArrayList<>();
                        list.add(object);
                        sprungContext.put(annotation.value(), list);
                    }
                }
            }

            System.out.println(sprungContext);
        }catch (Exception e){
            e.printStackTrace();
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

    private static void injectFields(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                Object bean = getBeanOfType(fieldType);
                field.setAccessible(true);
                field.set(instance, bean);
            }
        }
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
