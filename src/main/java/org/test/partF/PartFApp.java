package org.test.partF;

import org.framework.SprungApplication;
import org.framework.annotations.SprungBootApplication;

@SprungBootApplication
public class PartFApp implements Runnable{

    public static void main(String... args) {
        SprungApplication.run(PartFApp.class, args);
    }

    @Override
    public void run(){}
}
