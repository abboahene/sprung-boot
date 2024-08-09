package org.test.partH;

public class CustomerAddedEvent {
    Customer customer;

    public CustomerAddedEvent(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
