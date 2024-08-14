package org.test.partE;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.SprungBootApplication;
import org.framework.annotations.Value;

@SprungBootApplication
public class PartEApp implements Runnable{

    @Autowired
    private GreetingService greetingService;

    public static void main(String... args) {
        SprungApplication.run(PartEApp.class, args);
    }

    @Override
    public void run() {
        System.out.println(greetingService.getTheGreeting());
    }
}
