package de.unidue.inf.is.domain;

public class Vehicle {

    private final int tid;
    private final String name;
    private final String icon;

    public Vehicle(int tid, String name, String icon) {
        this.tid = tid;
        this.name = name;
        this.icon = icon;
    }

    public int getTid() {
        return tid;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}
