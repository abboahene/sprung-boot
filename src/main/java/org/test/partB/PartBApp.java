package org.test.partB;

import org.framework.SprungApplication;
import org.framework.annotations.Autowired;
import org.framework.annotations.Inject;
import org.framework.annotations.Qualifier;
import org.framework.annotations.SprungBootApplication;
import org.test.partA.ICustomerService;

@SprungBootApplication
public class PartBApp implements Runnable{

    private IAccountService accountServiceOne;
    private EmailService emailService;
    @Autowired
    @Qualifier(name="testQualifier")
    private TestQualifier testQualifier;

    @Autowired
    public PartBApp(EmailService emailService){
        this.emailService = emailService;
    }

    @Autowired
    public void setAccountServiceOne(IAccountService accountService){
        this.accountServiceOne = accountService;
    }

    public static void main(String... args) {
        SprungApplication.run(PartBApp.class, args);
    }

    @Override
    public void run() {
        System.out.println("AccountService before usage: " + accountServiceOne);
        accountServiceOne.addAccount();
        emailService.sendEmail();
        testQualifier.testQualifier();
    }

}
