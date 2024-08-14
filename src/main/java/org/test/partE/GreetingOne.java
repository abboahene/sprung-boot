package org.test.partE;

import org.framework.annotations.Profile;
import org.framework.annotations.Service;

@Service
@Profile(name="One")
public class GreetingOne implements Greeting{
    public String getGreeting() {
        return "Hello World";
    }
}
