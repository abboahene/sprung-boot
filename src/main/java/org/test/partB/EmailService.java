package org.test.partB;

import org.framework.annotations.Service;

@Service
public class EmailService {
    public void sendEmail(){
        System.out.println("sendEmail");
    }
}
