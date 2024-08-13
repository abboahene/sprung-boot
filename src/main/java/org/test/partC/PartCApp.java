package org.test.partC;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.Qualifier;
import org.framework.annotations.SprungBootApplication;
import org.framework.annotations.Value;
import org.test.partB.EmailService;
import org.test.partB.IAccountService;
import org.test.partB.TestQualifier;

@SprungBootApplication
public class PartCApp implements Runnable{
    @Value("email.server")
    private String emailServer;

    @Value("email.port")
    private int port;

    public static void main(String... args) {
        SprungApplication.run(PartCApp.class, args);
    }

    @Override
    public void run() {

        System.out.println("EmailServer: " + emailServer);
        System.out.println("Port: " + port);
    }

}
