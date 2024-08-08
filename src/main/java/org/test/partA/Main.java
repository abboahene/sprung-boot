package org.test.partA;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.Service;

@Service
public class Main implements Runnable{

    @Autowired
    static ICustomerService customerService;
    public static void main(String[] args) {
        SprungApplication.run(Main.class, args);
    }

    @Override
    public void run() {
        customerService.addCustomer();
    }
}