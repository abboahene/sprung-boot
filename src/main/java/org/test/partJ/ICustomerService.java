package org.test.partJ;

import org.framework.annotations.Async;

public interface ICustomerService {
    void addCustomer(Customer customer);

    @Async
    void asyncMethod();
}
