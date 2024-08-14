package org.test.partF;


import org.framework.annotations.Scheduled;
import org.framework.annotations.Service;

import java.time.LocalDateTime;

@Service
public class Welcome {
    @Scheduled(fixedRate = 1000)
    public void runSchedule(){
        System.out.println("This task runs every 1 seconds.." + LocalDateTime.now());
    }

}
