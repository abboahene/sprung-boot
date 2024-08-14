package org.test.partD;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.SprungBootApplication;

@SprungBootApplication
public class Main implements Runnable{

    @Autowired
    private ICustomerService customerService;
    public static void main(String[] args) {
        SprungApplication.run(Main.class, args);
    }

    @Override
    public void run() {
        customerService.addCustomer();
    }
}