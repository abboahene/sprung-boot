package org.framework.aop;

import java.lang.reflect.Method;

public interface JoinPoint {
    Object getTarget();
    Method getMethod();
    Object[] getArgs();
}
