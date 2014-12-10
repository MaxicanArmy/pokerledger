package org.pokerledger.pokerledgermobile.model;

/**
 * Created by Max on 9/12/14.
 */
public class Game {
    private int id = 0;
    private String game = "";

    //constructors
    public Game() {}

    public Game(int i, String s) {
        this.id = i;
        this.game = s;
    }

    @Override
    public String toString() {
        return this.game;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setGame(String s) {
        this.game = s;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getGame() {
        return this.game;
    }
}