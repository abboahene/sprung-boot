package org.framework.aop;

import java.lang.reflect.Method;

public interface ProceedingJoinPoint {
    Object proceed() throws Throwable;
    Object getTarget();
    Method getMethod();
    Object[] getArgs();
}
