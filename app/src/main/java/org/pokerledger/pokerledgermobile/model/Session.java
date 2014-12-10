package org.pokerledger.pokerledgermobile.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Max on 9/12/14.
 */
public class Session {
    private int id = 0;
    private String start = "";
    private String end = "";
    private int buyIn = 0;
    private int cashOut = 0;
    private Structure structure = new Structure();
    private Game game = new Game();
    private Location location = new Location();
    private int entrants = 0;
    private int placed = 0;
    private Blinds blinds = new Blinds();
    private String note = "";
    private ArrayList<Break> breaks = new ArrayList<Break>();
    private int state = 0;

    //constructors
    public Session() {}

    public Session(String s, String e, int bi, int co, Structure str, Game game, Location loc, int state) {
        this.start = s;
        this.end = e;
        this.buyIn = bi;
        this.cashOut = co;
        this.structure = str;
        this.game = game;
        this.location = loc;
        this.state = state;
    }

    public Session(int id, String s, String e, int bi, int co, Structure str, Game game, Location loc, int state) {
        this.id = id;
        this.start = s;
        this.end = e;
        this.buyIn = bi;
        this.cashOut = co;
        this.structure = str;
        this.game = game;
        this.location = loc;
        this.state = state;
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

    public void setState(int s) {
        this.state = s;
    }

    public void setEntrants(int e) {
        this.entrants = e;
    }

    public void setPlaced(int p) {
        this.placed = p;
    }

    public void setBlinds(Blinds b) {
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

    public int getState () {
        return this.state;
    }

    public int getEntrants() {
        return this.entrants;
    }

    public int getPlaced() {
        return this.placed;
    }

    public Blinds getBlinds() {
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
