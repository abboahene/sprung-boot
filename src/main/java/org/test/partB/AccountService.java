package org.test.partB;

import org.framework.annotations.Autowired;
import org.framework.annotations.Inject;
import org.framework.annotations.Qualifier;
import org.framework.annotations.Service;
import org.test.partA.CustomerService;

@Service
public class AccountService implements IAccountService {

    public void addAccount(){
        System.out.println("Adding Account");
    }
}
