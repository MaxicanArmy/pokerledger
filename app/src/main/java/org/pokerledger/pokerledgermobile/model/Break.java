package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 9/12/14.
 */
public class Break {
    private int id = 0;
    private String start = "";
    private String end = "";

    //constructors
    public Break() {}

    public Break(String s, String e) {
        this.start = s;
        this.end = e;
    }

    public Break(int i, String s, String e) {
        this.id = i;
        this.start = s;
        this.end = e;
    }

    @Override
    public String toString() {
        return "start: " + this.start + "\n  end: " + this.end;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setStart(String c) {
        this.start = c;
    }

    public void setEnd(String c) {
        this.end = c;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }
}

