package org.pokerledger.pokerledgermobile;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.helper.SessionListStats;
import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Max on 6/8/15.
 */
public class FilterActivity extends BaseActivity {
    View activeView;

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
        new LoadDateFilter().execute();
    }

    public void saveFilters(View v) {
        new ClearFilters().execute();

        //new SaveTypeFilter().execute();
        new SaveBlindsFilter().execute();
        new SaveStructuresFilter().execute();
        new SaveGamesFilter().execute();
        new SaveLocationsFilter().execute();
        new SaveDateFilter().execute();

    }

    public void showDatePickerDialog(View v) {
        activeView = v;

        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Button dateBtn = (Button) v;
        Bundle args = new Bundle();
        if (dateBtn.getHint().toString().matches("[A-Za-z]* Date$")) {
            Calendar calender = Calendar.getInstance();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        }
        else {
            Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})");
            Matcher m = DATE_PATTERN.matcher(dateBtn.getHint().toString());

            while (m.find()) {
                args.putInt("year", Integer.parseInt(m.group(1)));
                args.putInt("month", Integer.parseInt(m.group(2)) - 1);
                args.putInt("day", Integer.parseInt(m.group(3)));
            }
        }
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "DatePicker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Button b = (Button) activeView;
            b.setHint(String.format("%04d-%02d-%02d", year, month+1, day));

        }
    };

    public class ClearFilters extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            db.runQuery("UPDATE sessions SET filtered=0;");
            db.runQuery("UPDATE blinds SET filtered=0;");
            db.runQuery("UPDATE structures SET filtered=0;");
            db.runQuery("UPDATE games SET filtered=0;");
            db.runQuery("UPDATE locations SET filtered=0;");

            return null;
        }
    }

    public class LoadBlindsFilter extends AsyncTask<Void, Void, ArrayList<Blinds>> {
        @Override
        protected ArrayList<Blinds> doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

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
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            ArrayList<Blinds> blinds = db.getAllBlinds();
            CheckBox current;
            String query;
            String sessionQuery;

            for (Blinds b : blinds) {

                current = (CheckBox) findViewById(blindsIdBase + b.getId());
                if (!current.isChecked()) {

                    query = "UPDATE blinds SET filtered=1 WHERE blind_id=" + b.getId() + ";";
                    sessionQuery = "UPDATE sessions SET filtered=1 WHERE session_id IN (SELECT session_id FROM cash WHERE blinds=" + b.getId() + ");";

                    db.runQuery(query);
                    db.runQuery(sessionQuery);
                }
            }

            return null;
        }
    }

    public class LoadStructuresFilter extends AsyncTask<Void, Void, ArrayList<Structure>> {
        @Override
        protected ArrayList<Structure> doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

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

    public class SaveStructuresFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            ArrayList<Structure> structures = db.getAllStructures();
            CheckBox current;
            String query;
            String sessionQuery;

            for (Structure s : structures) {
                current = (CheckBox) findViewById(structuresIdBase + s.getId());
                if (!current.isChecked()) {

                    query = "UPDATE structures SET filtered=1 WHERE structure_id=" + s.getId() + ";";
                    sessionQuery = "UPDATE sessions SET filtered=1 WHERE structure=" + s.getId() + ";";

                    db.runQuery(query);
                    db.runQuery(sessionQuery);
                }
            }

            return null;
        }
    }

    public class LoadGamesFilter extends AsyncTask<Void, Void, ArrayList<Game>> {
        @Override
        protected ArrayList<Game> doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

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

    public class SaveGamesFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            ArrayList<Game> games = db.getAllGames();
            CheckBox current;
            String query;
            String sessionQuery;

            for (Game g : games) {
                current = (CheckBox) findViewById(gamesIdBase + g.getId());
                if (!current.isChecked()) {

                    query = "UPDATE games SET filtered=1 WHERE game_id=" + g.getId() + ";";
                    sessionQuery = "UPDATE sessions SET filtered=1 WHERE game=" + g.getId() + ";";

                    db.runQuery(query);
                    db.runQuery(sessionQuery);
                }
            }

            return null;
        }
    }

    public class LoadLocationsFilter extends AsyncTask<Void, Void, ArrayList<Location>> {
        @Override
        protected ArrayList<Location> doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

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

    public class SaveLocationsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            ArrayList<Location> locations = db.getAllLocations();
            CheckBox current;
            String query;
            String sessionQuery;

            for (Location l : locations) {
                current = (CheckBox) findViewById(locationsIdBase + l.getId());
                if (!current.isChecked()) {

                    query = "UPDATE locations SET filtered=1 WHERE location_id=" + l.getId() + ";";
                    sessionQuery = "UPDATE sessions SET filtered=1 WHERE location=" + l.getId() + ";";

                    db.runQuery(query);
                    db.runQuery(sessionQuery);
                }
            }

            return null;
        }
    }

    public class LoadDateFilter extends AsyncTask<Void, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            return db.getFilterDates();
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            Button startBtn = (Button) findViewById(R.id.start_date);
            Button endBtn = (Button) findViewById(R.id.end_date);

            startBtn.setHint(result.get("start"));
            endBtn.setHint(result.get("end"));
        }
    }

    public class SaveDateFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString() + " 00:00";
            String endDate = ((Button) findViewById(R.id.end_date)).getHint().toString() + " 23:59";

            String query;
            String sessionQuery;

            if (startDate != "Start Date") {
                query = "UPDATE filter SET start='" + startDate + "';";
                sessionQuery = "UPDATE sessions SET filtered=1 WHERE start < '" + startDate + "';";

                db.runQuery(query);
                db.runQuery(sessionQuery);
            }

            if (endDate != "End Date") {
                query = "UPDATE filter SET end='" + endDate + "';";
                sessionQuery = "UPDATE sessions SET filtered=1 WHERE end > '" + endDate + "';";

                db.runQuery(query);
                db.runQuery(sessionQuery);
            }

            return null;
        }
    }
}
