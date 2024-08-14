package org.test.partL;

import org.framework.annotations.Around;
import org.framework.annotations.Aspect;
import org.framework.aop.ProceedingJoinPoint;


@Aspect
public class AOP {
    @Around(pointCut = "CustomerService.addCustomer")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("around start");
        Object obj = proceedingJoinPoint.proceed();
        System.out.println("around end");
        return obj;
    }
}
