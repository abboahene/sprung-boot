package org.test.partK;

import org.framework.annotations.After;
import org.framework.annotations.Around;
import org.framework.annotations.Aspect;
import org.framework.annotations.Before;
import org.framework.aop.JoinPoint;
import org.framework.aop.ProceedingJoinPoint;


@Aspect
public class AOP {
    @Before(pointCut = "CustomerService.addCustomer")
    public void beforeAdvice(JoinPoint joinPoint) throws Throwable {
        System.out.println("before advice");
    }

    @After(pointCut = "CustomerService.addCustomer")
    public void afterAdvice(JoinPoint joinPoint) throws Throwable {
        System.out.println("after advice");
    }
}
