package org.test.partA;

import org.framework.annotations.Service;

@Service
public class CustomerService implements ICustomerService {

    public CustomerService() {
    }

    public void addCustomer(){
        System.out.println("Customer added");
    }
}
