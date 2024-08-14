package org.test.partH;

import org.framework.annotations.EventListener;
import org.framework.annotations.Service;

@Service
public class EmailListener {
    @EventListener
    public void sendEmail(CustomerAddedEvent event){
        System.out.println("Email sent to customer: "+ event.customer.getName());
    }
}
