package org.test.partE;

import org.framework.annotations.Autowired;
import org.framework.annotations.Service;

@Service
public class GreetingService {
    @Autowired
    private Greeting greeting;
    public String getTheGreeting() {
        return greeting.getGreeting();
    }
}
