package org.framework.async;

import org.framework.annotations.Async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncProxy implements InvocationHandler {

    private final Object target;
    private final ExecutorService executorService;


    public AsyncProxy(Object target) {
        this.target = target;
        this.executorService = Executors.newCachedThreadPool();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Async.class)) {
            // Run asynchronously
            System.out.println("asynceee");
            executorService.submit(() -> {
                try {
                    method.invoke(target, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return null;
        } else {
            // Run synchronously
            return method.invoke(target, args);
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
