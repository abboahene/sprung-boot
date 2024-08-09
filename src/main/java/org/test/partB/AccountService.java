package org.test.partB;

import org.framework.annotations.Autowired;
import org.framework.annotations.Inject;
import org.framework.annotations.Qualifier;
import org.framework.annotations.Service;
import org.test.partA.CustomerService;

@Service
public class AccountService implements IAccountService {

    @Autowired
    @Qualifier(name="emailService")
    private EmailService emailService;
    private CustomerService customerService;
    @Autowired
    public AccountService(CustomerService customerService){
        this.customerService = customerService;
    }

    public void addAccount(){
        emailService.sendEmail();
        customerService.addCustomer();
    }
}
