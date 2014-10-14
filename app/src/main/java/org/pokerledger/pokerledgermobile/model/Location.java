package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 9/12/14.
 */
public class Location {
    private int id;
    private String location;

    //constructors
    public Location() {}

    public Location(int i, String s) {
        this.id = i;
        this.location = s;
    }

    @Override
    public String toString() {
        return this.location;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setLocation(String s) {
        this.location = s;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getLocation() {
        return this.location;
    }
}
