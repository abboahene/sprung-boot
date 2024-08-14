package org.test.testPart;

import org.framework.annotations.After;
import org.framework.annotations.Aspect;
import org.framework.annotations.Before;
import org.framework.annotations.Service;

@Aspect
@Service
public class LoggingAspect implements LoggingAspectInterface {

    @Before(pointcut = "org.test.partK.Person.setName")
    public void logBefore() {
        System.out.println("Executing before setting name.");
    }

    @After(pointcut = "org.test.partK.Person.setName")
    public void logAfter() {
        System.out.println("Executing after setting name.");
    }
}
