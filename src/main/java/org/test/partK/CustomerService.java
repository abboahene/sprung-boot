package org.test.partK;

import org.framework.annotations.Service;

@Service
public class CustomerService implements ICustomerService {

    public void addCustomer(Customer customer){
        System.out.println("Customer added");
    }
}
