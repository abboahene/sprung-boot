package org.framework.pubSub;
import org.framework.annotations.EventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationEventPublisher {

    private static final HashMap<Class<?>, List<Method>> eventRegistry = new HashMap<>();

    public ApplicationEventPublisher(List<List<Object>> listOfBeanList) {
        for (List<Object> beans : listOfBeanList) {
            for (Object bean : beans) {
                for (Method method : bean.getClass().getMethods()) {
                    if (method.isAnnotationPresent(EventListener.class)) {
                        Class<?> paramClass = method.getParameterTypes()[0];
                        if(eventRegistry.containsKey(paramClass)){
                            eventRegistry.get(paramClass).add(method);
                        }else{
                            List<Method> list = new ArrayList<>();
                            list.add(method);
                            eventRegistry.put(paramClass, list);
                        }
                    }
                }
            }
        }
    }

    public void publish(Object event){
         List<Method> methods = eventRegistry.get(event.getClass());
         for (Method method: methods){
             try {
               Class<?> parameterType =  method.getParameterTypes()[0];
                 if (parameterType.isAssignableFrom(event.getClass())) {
                     method.invoke(method.getDeclaringClass().newInstance(), event);
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }
}
