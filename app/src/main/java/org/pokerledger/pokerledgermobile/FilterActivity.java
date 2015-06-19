package org.pokerledger.pokerledgermobile;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.helper.SessionListStats;
import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

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
    private static final int typesIdBase = 10000;
    ArrayList<Integer> filteredBlinds = new ArrayList<Integer>();
    private static final int blindsIdBase = 20000;
    ArrayList<Integer> filteredStructures = new ArrayList<Integer>();
    private static final int structuresIdBase = 30000;
    ArrayList<Integer> filteredGames = new ArrayList<Integer>();
    private static final int gamesIdBase = 40000;
    ArrayList<Integer> filteredLocations = new ArrayList<Integer>();
    private static final int locationsIdBase = 50000;
    String filterStartDate;
    String filterEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //new LoadTypeFilter().execute();
        new LoadBlindsFilter().execute();
        new LoadStructuresFilter().execute();
        new LoadGamesFilter().execute();
        new LoadLocationsFilter().execute();
        //new LoadDateFilter().execute();
    }

    public void saveFilters(View v) {
        //reset all filters
        new SaveBlindsFilter().execute();

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
            LinearLayout blindsWrapper = (LinearLayout) findViewById(R.id.blindsWrapper);
            CheckBox current;

            for (Blinds b : result) {
                current = new CheckBox(FilterActivity.this);
                current.setId(blindsIdBase + b.getId());
                current.setText(b.toString());

                if (b.getFiltered() == 0) {
                    current.setChecked(true);
                }
                blindsWrapper.addView(current);
            }
        }
    }

    public class SaveBlindsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            ArrayList<Blinds> blinds = db.getAllBlinds();
            CheckBox current;
            int filtered;
            String query;
            String sessionQuery;

            for (Blinds b : blinds) {
                filtered = 0;

                current = (CheckBox) findViewById(blindsIdBase + b.getId());
                if (!current.isChecked()) {
                    filtered = 1;
                }
                query = "UPDATE blinds SET filtered=" + filtered + " WHERE blind_id=" + b.getId() + ";";
                sessionQuery = "UPDATE sessions SET filtered=" + filtered + " WHERE session_id IN (SELECT session_id FROM cash WHERE blinds=" + b.getId() + ");";

                db.runQuery(query);
                db.runQuery(sessionQuery);
            }

            return null;
        }
    }

    public class LoadStructuresFilter extends AsyncTask<Void, Void, ArrayList<Structure>> {
        @Override
        protected ArrayList<Structure> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getAllStructures();
        }

        @Override
        protected void onPostExecute(ArrayList<Structure> result) {
            LinearLayout structuresWrapper = (LinearLayout) findViewById(R.id.structuresWrapper);
            CheckBox current;

            for (Structure s : result) {
                current = new CheckBox(FilterActivity.this);
                current.setId(structuresIdBase + s.getId());
                current.setText(s.toString());

                if (s.getFiltered() == 0) {
                    current.setChecked(true);
                }
                structuresWrapper.addView(current);
            }
        }
    }

    public class LoadGamesFilter extends AsyncTask<Void, Void, ArrayList<Game>> {
        @Override
        protected ArrayList<Game> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getAllGames();
        }

        @Override
        protected void onPostExecute(ArrayList<Game> result) {
            LinearLayout gamesWrapper = (LinearLayout) findViewById(R.id.gamesWrapper);
            CheckBox current;

            for (Game g : result) {
                current = new CheckBox(FilterActivity.this);
                current.setId(gamesIdBase + g.getId());
                current.setText(g.toString());

                if (g.getFiltered() == 0) {
                    current.setChecked(true);
                }
                gamesWrapper.addView(current);
            }
        }
    }

    public class LoadLocationsFilter extends AsyncTask<Void, Void, ArrayList<Location>> {
        @Override
        protected ArrayList<Location> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getAllLocations();
        }

        @Override
        protected void onPostExecute(ArrayList<Location> result) {
            LinearLayout locationsWrapper = (LinearLayout) findViewById(R.id.locationsWrapper);
            CheckBox current;

            for (Location l : result) {
                current = new CheckBox(FilterActivity.this);
                current.setId(locationsIdBase + l.getId());
                current.setText(l.toString());

                if (l.getFiltered() == 0) {
                    current.setChecked(true);
                }
                locationsWrapper.addView(current);
            }
        }
    }
}
