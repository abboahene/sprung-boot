package org.framework;

import org.framework.annotations.Autowired;
import org.framework.annotations.Service;

@Service
public class Main{

    @Autowired
    static Counter counter;
    public static void main(String[] args) {
        SprungApplication.start();
        counter.print();
    }
}