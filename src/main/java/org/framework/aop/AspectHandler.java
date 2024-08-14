//package org.framework.aop;
//
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AspectHandler {
//
//    private final Map<String, Method> beforeAdvice = new HashMap<>();
//    private final Map<String, Method> afterAdvice = new HashMap<>();
//
//    public void registerBeforeAdvice(String pointcut, Method advice) {
//        beforeAdvice.put(pointcut, advice);
//    }
//
//    public void registerAfterAdvice(String pointcut, Method advice) {
//        afterAdvice.put(pointcut, advice);
//    }
//
//    public Object createProxy(Object target) {
//        Class<?> targetClass = target.getClass();
//        System.out.println("TargetClas..." + targetClass);
//        Class<?>[] interfaces = targetClass.getInterfaces();
//
//        if (interfaces.length == 0) {
//            throw new UnsupportedOperationException("Target class must implement at least one interface.");
//        }
//
//        return Proxy.newProxyInstance(
//                targetClass.getClassLoader(),
//                interfaces,
//                new InvocationHandler() {
//                    @Override
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                        String pointcutKey = targetClass.getName() + "." + method.getName();
//
//                        System.out.println("Pointcutkey:" + pointcutKey);
//                        // Before advice
//                        Method beforeMethod = beforeAdvice.get(pointcutKey);
//                        if (beforeMethod != null) {
//                            beforeMethod.setAccessible(true);
//                            beforeMethod.invoke(null);  // Assuming static advice methods
//                        }
//
//                        // Invoke target method
//                        Object result;
//                        try {
//                            result = method.invoke(target, args);
//                        } catch (InvocationTargetException e) {
//                            throw e.getTargetException();
//                        }
//
//                        // After advice
//                        Method afterMethod = afterAdvice.get(pointcutKey);
//                        if (afterMethod != null) {
//                            afterMethod.setAccessible(true);
//                            afterMethod.invoke(null);  // Assuming static advice methods
//                        }
//
//                        return result;
//                    }
//                }
//        );
//    }
//}
