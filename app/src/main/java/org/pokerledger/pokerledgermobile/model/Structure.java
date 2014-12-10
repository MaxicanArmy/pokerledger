package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 9/12/14.
 */
public class Structure {
    private int id = 0;
    private String structure = "";

    //constructors
    public Structure() {}

    public Structure(int i, String s) {
        this.id = i;
        this.structure = s;
    }

    @Override
    public String toString() {
        return this.structure;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setStructure(String s) {
        this.structure = s;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getStructure() {
        return this.structure;
    }
}
