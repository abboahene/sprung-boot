package org.test.partE;

import org.framework.annotations.Profile;
import org.framework.annotations.Service;

@Service
@Profile(name="Two")
public class GreetingTwo implements Greeting {
    public String getGreeting() {
        return "Hi World";
    }
}