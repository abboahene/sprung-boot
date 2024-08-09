package org.test.partG;

import org.framework.annotations.EventListener;
import org.framework.annotations.Scheduled;
import org.framework.annotations.Service;

@Service
public class AdvertisementListener {
    @Scheduled(cron = "1 0")
    public void advertise(){
        System.out.println("Advertise to all customers");
    }
}
