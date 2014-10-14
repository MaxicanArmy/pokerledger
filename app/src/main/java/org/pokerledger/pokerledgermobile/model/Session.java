package org.pokerledger.pokerledgermobile.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Max on 9/12/14.
 */
public class Session {
    private int id;
    private String start;
    private String end;
    private int buyIn;
    private int cashOut;
    private Structure structure;
    private Game game;
    private Location location;
    private int entrants;
    private int placed;
    private String blinds;
    private String note;
    private ArrayList<Break> breaks;

    private boolean active;

    //constructors
    public Session() {}

    public Session(String s, String e, int bi, int co, Structure str, Game game, Location loc, boolean a) {
        this.start = s;
        this.end = e;
        this.buyIn = bi;
        this.cashOut = co;
        this.structure = str;
        this.game = game;
        this.location = loc;
        this.active = a;
    }

    public Session(int id, String s, String e, int bi, int co, Structure str, Game game, Location loc, boolean a) {
        this.id = id;
        this.start = s;
        this.end = e;
        this.buyIn = bi;
        this.cashOut = co;
        this.structure = str;
        this.game = game;
        this.location = loc;
        this.active = a;
    }

    @Override
    public String toString() {
        return Integer.toString(this.id) + " " + this.start + " " + this.location.getLocation();
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

    public void setBuyIn(int i) {
        this.buyIn = i;
    }

    public void setCashOut(int i) {
        this.cashOut = i;
    }

    public void setStructure(Structure i) {
        this.structure = i;
    }

    public void setGame(Game i) {
        this.game = i;
    }

    public void setLocation(Location i) {
        this.location = i;
    }

    public void setActive(boolean b) {
        this.active = b;
    }

    public void setEntrants(int e) {
        this.entrants = e;
    }

    public void setPlaced(int p) {
        this.placed = p;
    }

    public void setBlinds(String b) {
        this.blinds = b;
    }

    public void setNote(String n) {
        this.note = n;
    }

    public void setBreaks(ArrayList<Break> b) {
        this.breaks = b;
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

    public int getBuyIn() {
        return this.buyIn;
    }

    public int getCashOut() {
        return this.cashOut;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public Game getGame() {
        return this.game;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean getActive() {
        return this.active;
    }

    public int getEntrants() {
        return this.entrants;
    }

    public int getPlaced() {
        return this.placed;
    }

    public String getBlinds() {
        return this.blinds;
    }

    public String getNote() {
        return this.note;
    }

    public ArrayList<Break> getBreaks() {
        return this.breaks;
    }

    //other
    public boolean onBreak() {
        if (this.breaks == null || this.breaks.size() == 0 || !(this.breaks.get(this.breaks.size() - 1).getEnd() == null)) {
            return false;
        }
        return true;
    }

    public void breakEnd() {
        Calendar cal = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("00");
        String datetime = cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)) + " " + df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE));
        int position = this.breaks.size() - 1;

        this.breaks.get(position).setEnd(datetime);
    }
}
