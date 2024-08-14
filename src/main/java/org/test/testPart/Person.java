package org.test.testPart;

public class Person implements PersonInterface {
    private String name;

    @Override
    public void setName(String name) {
        System.out.println("Name set to: " +name);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

