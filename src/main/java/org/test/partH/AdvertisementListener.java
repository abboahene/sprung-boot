package org.test.partH;

import org.framework.annotations.EventListener;
import org.framework.annotations.Service;

@Service
public class AdvertisementListener {
    @EventListener
    public void advertise(CustomerAddedEvent event){
        System.out.println("Advertise to customer: "+ event.getCustomer().getName());
    }
}
