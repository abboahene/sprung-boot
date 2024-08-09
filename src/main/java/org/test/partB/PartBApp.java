package org.test.partB;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.Inject;
import org.framework.annotations.SprungBootApplication;
import org.test.partA.ICustomerService;

@SprungBootApplication
public class PartBApp implements Runnable{
    private IAccountService accountService;
    @Autowired
    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    private IAccountService accountServiceOne;

    public static void main(String... args) {
        SprungApplication.run(PartBApp.class, args);
    }

    @Override
    public void run() {
        System.out.println("AccountService before usage: " + accountService);
        System.out.println("AccountService before usage: " + accountServiceOne);
        accountServiceOne.addAccount();
        accountService.addAccount();

    }

}
