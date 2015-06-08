package org.pokerledger.pokerledgermobile;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.helper.SessionListStats;
import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Session;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Max on 6/8/15.
 */
public class FilterActivity extends BaseActivity {
    //need variables for all the currently set filters loaded from the filters DB table
    ArrayList<Integer> filteredType = new ArrayList<Integer>();
    ArrayList<Integer> filteredBlinds = new ArrayList<Integer>();
    ArrayList<Integer> filteredStructures = new ArrayList<Integer>();
    ArrayList<Integer> filteredGames = new ArrayList<Integer>();
    ArrayList<Integer> filteredLocations = new ArrayList<Integer>();
    String filterStartDate;
    String filterEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //new LoadTypeFilter().execute();
        new LoadBlindsFilter().execute();
        //new LoadStructuresFilter().execute();
        //new LoadGamesFilter().execute();
        //new LoadLocationsFilter().execute();
        //new LoadDateFilter().execute();
    }

    public class LoadBlindsFilter extends AsyncTask<Void, Void, ArrayList<Blinds>> {
        @Override
        protected ArrayList<Blinds> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getAllBlinds();
        }

        @Override
        protected void onPostExecute(ArrayList<Blinds> result) {

        }
    }
    //BSG stands for Blind - Structure - Game Statistics
    public class LoadBSGStatistics extends AsyncTask<Void, Void, ArrayList<Session>> {
        @Override
        protected ArrayList<Session> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getSessions(0);
        }

        @Override
        protected void onPostExecute(ArrayList<Session> result) {

            HashMap<String, SessionListStats> bsgStats = new HashMap<String, SessionListStats>();
            HashMap<String, SessionListStats> dayOfWeekStats = new HashMap<String, SessionListStats>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar cal;
            String key;
            String label;
            SessionListStats current;

            LinearLayout bsgWrapper = (LinearLayout) findViewById(R.id.bsgWrapper);
            LinearLayout dayWrapper = (LinearLayout) findViewById(R.id.dayWrapper);
            LinearLayout rowWrapper;
            TextView currentBSG;
            TextView currentHourly;
            SessionListStats stats;

            for (Session s : result) {
                if (s.getBlinds() != null) {
                    key = s.getGame().toString() + " " + s.getStructure().toString() + " " + s.getBlinds().toString();
                }
                else {
                    key = s.getGame().toString() + " " + s.getStructure().toString() + " " + " Tournament";
                }

                current = bsgStats.get(key);

                if (current != null) {
                    current.addSession(s);
                } else {
                    bsgStats.put(key, new SessionListStats(s));
                }

                cal = Calendar.getInstance();
                try {
                    cal.setTime(sdf.parse(s.getStart()));
                } catch (Exception e) {
                    //fucking parse exception needed to be handled
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
                key = cal.get(Calendar.DAY_OF_WEEK) + dateFormat.format(cal.getTime());

                current = dayOfWeekStats.get(key);

                if (current != null) {
                    current.addSession(s);
                } else {
                    dayOfWeekStats.put(key, new SessionListStats(s));
                }
            }

            Map<String, SessionListStats> sortedBSG = new TreeMap<String, SessionListStats>(bsgStats);
            Map<String, SessionListStats> sortedDay = new TreeMap<String, SessionListStats>(dayOfWeekStats);

            LinearLayout.LayoutParams llpLabel = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .75f);
            LinearLayout.LayoutParams llpStat = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .25f);

            for (Map.Entry<String, SessionListStats> entry : sortedBSG.entrySet()) {
                rowWrapper = new LinearLayout(FilterActivity.this);
                currentBSG = new TextView(FilterActivity.this);
                currentHourly = new TextView(FilterActivity.this);
                currentHourly.setGravity(Gravity.END);

                stats = entry.getValue();

                Session ex = stats.getSessions().get(0); //get an example session to retrieve the blinds, structure and game
                if (ex.getBlinds() == null) {
                    label = ex.getStructure().toString() + " " + ex.getGame().toString() + " Tournament";
                }
                else {
                    label = ex.getBlinds().toString() + " " + ex.getStructure().toString() + " " + ex.getGame().toString();
                }
                currentBSG.setText(label);

                if (stats.getProfit() < 0 ) {
                    currentHourly.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    currentHourly.setTextColor(Color.parseColor("#008000"));
                }
                currentHourly.setText(stats.wageFormatted());

                currentBSG.setLayoutParams(llpLabel);
                currentHourly.setLayoutParams(llpStat);

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (8*scale + 0.5f);

                rowWrapper.setPadding(0,0,0,dpAsPixels);
                rowWrapper.addView(currentBSG);
                rowWrapper.addView(currentHourly);

                bsgWrapper.addView(rowWrapper);
            }

            for (Map.Entry<String, SessionListStats> entry : sortedDay.entrySet()) {
                rowWrapper = new LinearLayout(FilterActivity.this);
                currentBSG = new TextView(FilterActivity.this);
                currentHourly = new TextView(FilterActivity.this);
                currentHourly.setGravity(Gravity.END);

                stats = entry.getValue();

                key = entry.getKey();
                currentBSG.setText(key.substring(1, key.length()) + " (" + stats.timeFormatted() + ")");

                if (stats.getProfit() < 0 ) {
                    currentHourly.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    currentHourly.setTextColor(Color.parseColor("#008000"));
                }
                currentHourly.setText(stats.wageFormatted());

                currentBSG.setLayoutParams(llpLabel);
                currentHourly.setLayoutParams(llpStat);

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (8*scale + 0.5f);

                rowWrapper.setPadding(0,0,0,dpAsPixels);
                rowWrapper.addView(currentBSG);
                rowWrapper.addView(currentHourly);

                dayWrapper.addView(rowWrapper);
            }
        }
    }
}
