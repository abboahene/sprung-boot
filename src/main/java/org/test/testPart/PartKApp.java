package org.test.testPart;

import org.framework.SprungApplication;
import org.framework.annotations.SprungBootApplication;

@SprungBootApplication
public class PartKApp implements Runnable{

    public static void main(String... args) {
        SprungApplication.run(PartKApp.class, args);
    }

    @Override
    public void run() {
        Person person = new Person();
        person.setName("John Doe");
    }
}
