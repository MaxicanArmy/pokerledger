package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 6/21/15.
 */
public class FormatType {
    private int id = 0;
    private String formatType = "";

    public FormatType() {
        this(0, "");
    }

    public FormatType(int id, String ft) {
        this.id = id;
        this.formatType = ft;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setFormatType(String s) {
        this.formatType = s;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getFormatType() {
        return this.formatType;
    }
}
