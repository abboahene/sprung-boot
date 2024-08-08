package org.framework;

import org.framework.annotations.Service;

@Service
public class Counter {

    public Counter() {
        System.out.println("created");
    }


    public void print(){
        System.out.println("counting");
    }
}
