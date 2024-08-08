package org.framework;

import org.framework.annotations.Autowired;
import org.framework.annotations.SprungClassAnnotation;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SprungApplication {
    private static final HashMap<Class< ? extends Annotation>, List<Object>> sprungContext = new HashMap<>();


    public static void run(Class<?> primaryClass, String[] args) {
        try {
            instantiateAllAnnotatedClasses(primaryClass);
            performFieldDI();

            // do run() for application
            Runnable primary = (Runnable) primaryClass.getDeclaredConstructor().newInstance();
            primary.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void instantiateAllAnnotatedClasses(Class<?> primaryClass) {
        try {

            System.out.println(primaryClass.getPackageName());
            Reflections reflections = new Reflections(primaryClass.getPackageName());
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

    private static void performFieldDI() {
        try {
            for (Class< ? extends Annotation> key : sprungContext.keySet()) {
                List<Object> objects = sprungContext.get(key);
            for (Object object : objects) {
                // find annotated fields
                for (Field field : object.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        // get the type of the field
                        Class<?> theFieldType =field.getType();
                        //get the object instance of this type
                        Object instance = getBeanOfType(theFieldType);
                        //do the injection
                        field.setAccessible(true);
                        field.set(object, instance);
                    }
                }
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
