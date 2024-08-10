package org.test.partI;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.EnableConfigurationProperties;
import org.framework.annotations.SprungBootApplication;

@SprungBootApplication
@EnableConfigurationProperties(AppConfigProperties.class)
public class Main implements Runnable{

    @Autowired
    private AppConfigProperties appConfigProperties;
    public static void main(String[] args) {
        SprungApplication.run(Main.class, args);
    }

    @Override
    public void run() {
        System.out.println(appConfigProperties);
    }
}