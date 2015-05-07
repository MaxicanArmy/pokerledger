package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 2/28/15.
 */
public class BSGStats {
    private Blinds blinds = new Blinds();
    private Structure structure = new Structure();
    private Game game = new Game();

    private double timePlayed = 0;
    private double profit = 0;

    public BSGStats() {
    }

    public BSGStats(Session s) {
        this.blinds = s.getBlinds();
        this.structure = s.getStructure();
        this.game = s.getGame();
        timePlayed = s.getTimePlayed();
        profit = s.getProfit();
    }

    //setters
    public void setBlinds(Blinds b) {
        this.blinds = b;
    }

    public void setStructure(Structure i) {
        this.structure = i;
    }

    public void setGame(Game i) {
        this.game = i;
    }

    //getters
    public Blinds getBlinds() {
        return this.blinds;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public Game getGame() {
        return this.game;
    }

    //manipulators
    public void updateInfo(Session s) {
        this.timePlayed += s.getTimePlayed();
        this.profit += s.getProfit();
    }

    public double getHourlyWage() {
        return (double) Math.round((profit / timePlayed) * 100) /100;
    }
}
