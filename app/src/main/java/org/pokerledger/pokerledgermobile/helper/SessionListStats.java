package org.pokerledger.pokerledgermobile.helper;

import org.pokerledger.pokerledgermobile.model.Session;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Max on 3/9/15.
 */
public class SessionListStats {
    private ArrayList<Session> sessions = new ArrayList<Session>();
    private int profit = 0;
    private double time = 0;

    public SessionListStats() {}

    public SessionListStats(ArrayList<Session> sessionList) {
        for (Session s : sessionList) {
            this.addSession(s);
        }
    }

    //getters
    public ArrayList<Session> getSessions() {
        return this.sessions;
    }

    public double getTimePlayed() {
        return this.time;
    }

    public int getProfit() {
        return this.profit;
    }

    //other
    public void addSession(Session s) {
        this.sessions.add(s);
        this.time += s.getTimePlayed();
        this.profit += s.getProfit();
    }


    public String profitFormatted() {
        String profitText;
        if (profit < 0 ) {
            profitText = "($" + Integer.toString(Math.abs(profit)) + ")";
        } else {
            profitText = "$" + Integer.toString(profit);
        }

        return profitText;
    }

    public String timeFormatted() {
        int hours = (int) Math.floor(time);
        int minutes = (int) Math.round((time - hours) * 100) * 60 / 100; //ridiculous additional math to defeat bizarre Double arithmetic not being precise
        String timePlayed = "";

        if (hours > 0) {
            timePlayed += Integer.toString(hours) + "h";
        }

        timePlayed += " " + minutes + "m";

        return timePlayed;
    }

    public String wageFormatted() {
        double hourly;
        String hourlyWage;

        if (time == 0) {
            hourly = 0;
        }
        else {
            hourly = profit / time;
        }

        DecimalFormat df = new DecimalFormat("0.00");

        if (hourly < 0 ) {
            hourlyWage = "($" + df.format(Math.abs(hourly)) + ")";
        } else {
            hourlyWage = "$" + df.format(hourly);
        }

        return hourlyWage;
    }
}
