package org.test.partJ;

import org.framework.annotations.Async;
import org.framework.annotations.EnableAsync;
import org.framework.annotations.Service;

@Service
@EnableAsync
public class CustomerService implements ICustomerService {

    public void addCustomer(Customer customer){
        System.out.println("Customer added");
    }

    @Async
    public void asyncMethod() {
        try {
            // Simulate a long-running task
            Thread.sleep(3000);
            System.out.println("Task completed");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Task interrupted");
        }
    }
}
