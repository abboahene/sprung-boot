package org.framework.aop;

import org.framework.annotations.After;
import org.framework.annotations.Around;
import org.framework.annotations.Before;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectProxy implements InvocationHandler {

    private final Object target;
    private final List<Object> aspects;


    public AspectProxy(Object target, List<Object> aspects) {
        this.target = target;
        this.aspects = aspects == null ? new ArrayList<>() : aspects;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Method> matchingAroundMethods = findMatchingAspectMethods(method, Around.class).keySet().stream().toList();
        Map<Method, Object> matchingBeforeMethods = findMatchingAspectMethods(method, Before.class);
        Map<Method, Object> matchingAfterMethods = findMatchingAspectMethods(method, After.class);

        JoinPoint joinPoint = createJoinPoint(method, args);

        // Execute @Before advice
        for (Method beforeMethod : matchingBeforeMethods.keySet()) {
            beforeMethod.invoke(matchingBeforeMethods.get(beforeMethod), joinPoint);
        }

        Object result;
        if (!matchingAroundMethods.isEmpty()) {
            // Execute @Around advice
            result = executeAspectChain(matchingAroundMethods, 0, method, args);
        } else {
            // If no @Around advice, just invoke the method
            result = method.invoke(target, args);
        }

        // Execute @After advice
        for (Method afterMethod : matchingAfterMethods.keySet()) {
            afterMethod.invoke(matchingAfterMethods.get(afterMethod), joinPoint);
        }

        return result;
    }

    private Object executeAspectChain(List<Method> aspectMethods, int index, Method method, Object[] args) throws Throwable {
        if (index >= aspectMethods.size()) {
            return method.invoke(target, args);
        }
        Method aspectMethod = aspectMethods.get(index);
        Object aspect = aspects.get(aspectMethods.indexOf(aspectMethod));
        return aspectMethod.invoke(aspect, new ProceedingJoinPoint() {
            @Override
            public Object proceed() throws Throwable {
                return executeAspectChain(aspectMethods, index + 1, method, args);
            }
            @Override
            public Object getTarget() { return target; }
            @Override
            public Method getMethod() { return method; }
            @Override
            public Object[] getArgs() { return args; }
        });
    }

    private Map<Method, Object> findMatchingAspectMethods(Method method, Class<? extends Annotation> annotationType) {
        Map<Method, Object> matchingMethods = new HashMap<>();
        for (Object aspect : aspects) {
            for (Method aspectMethod : aspect.getClass().getDeclaredMethods()) {
                if (aspectMethod.isAnnotationPresent(annotationType)) {
                    Annotation annotation = aspectMethod.getAnnotation(annotationType);
                    String pointCut = getAnnotationPointcut(annotation);
                    if (matchesPointcut(pointCut, method)) {
                        matchingMethods.put(aspectMethod, aspect);
                    }
                }
            }
        }
        return matchingMethods;
    }

    private JoinPoint createJoinPoint(Method method, Object[] args) {
        return new JoinPoint() {
            @Override
            public Object getTarget() { return target; }
            @Override
            public Method getMethod() { return method; }
            @Override
            public Object[] getArgs() { return args; }
        };
    }

    private String getAnnotationPointcut(Annotation annotation) {
        try {
            Method valueMethod = annotation.annotationType().getMethod("pointCut");
            return (String) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return "";
        }
    }

    private boolean matchesPointcut(String pointcut, Method method) {
        return pointcut.equals(target.getClass().getSimpleName()+"."+method.getName());
    }

}
