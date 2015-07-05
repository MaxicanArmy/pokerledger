package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 6/21/15.
 */
public class GameFormat {
    private int id = 0;
    private String format = "";
    private FormatType formatType = new FormatType();
    private int filtered = 0;

    public GameFormat() {

    }

    public GameFormat(int id, String f, FormatType ft) {
        this(id, f, ft, 0);
    }

    public GameFormat(int id, String f, FormatType ft, int flt) {
        this.id = id;
        this.format = f;
        this.formatType = ft;
        this.filtered = flt;
    }

    @Override
    public String toString() {
        return this.format + " " + formatType.getFormatType();
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFormat(String s) {
        this.format = s;
    }

    public void setFormatType(FormatType ft) {
        this.formatType = ft;
    }

    public void setFiltered(int i) {
        this.filtered = i;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getFormat() {
        return this.format;
    }

    public FormatType getFormatType() {
        return this.formatType;
    }

    public int getFiltered() {
        return this.filtered;
    }
}
