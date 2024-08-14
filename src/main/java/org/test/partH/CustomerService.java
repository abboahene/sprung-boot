package org.test.partH;

import org.framework.annotations.Autowired;
import org.framework.annotations.Service;
import org.framework.pubSub.ApplicationEventPublisher;

@Service
public class CustomerService implements ICustomerService {

    @Autowired
    private ApplicationEventPublisher publisher;
    public CustomerService() {
    }

    public void addCustomer(Customer customer){
        System.out.println("Customer added");
        publisher.publish(new CustomerAddedEvent(customer));
    }
}
