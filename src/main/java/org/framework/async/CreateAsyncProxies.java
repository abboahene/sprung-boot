package org.framework.async;

import org.framework.annotations.Async;
import org.framework.annotations.EnableAsync;
import org.framework.annotations.EventListener;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateAsyncProxies {

//    public CreateAsyncProxies(HashMap<Class<?>, List<Object>> sprungContext) {
//        for (Class<?> key : sprungContext.keySet()) {
//            List<Object> beans = sprungContext.get(key);
//            for (Object bean : beans) {
//                if (bean.getClass().isAnnotationPresent(EnableAsync.class)) {
//                    Object instance = clazz.getDeclaredConstructor().newInstance();
//                    Proxy.newProxyInstance(
//                            clazz.getClassLoader(),
//                            clazz.getInterfaces(),
//                            new AsyncInvocationHandler(instance)
//                    );
//                }
//            }
//        }
//    }
}
