package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.helper.SessionListStats;
import org.pokerledger.pokerledgermobile.model.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Max on 9/26/14.
 */
public class HistoryActivity extends BaseActivity {
    protected int tbSpinnerPos = 1;
    protected int tfSpinnerPos = 0;
    //protected boolean init = true;

    //these populate the timeframes spinner and the Strings are the keys of the corresponding HashMaps
    protected ArrayList<String> weeklyList;
    protected ArrayList<String> monthlyList;
    protected ArrayList<String> yearlyList;
    protected ArrayList<String> allList;

    //these populate the history list and store the overview info
    protected HashMap<String, SessionListStats> weekly;
    protected HashMap<String, SessionListStats> monthly;
    protected HashMap<String, SessionListStats> yearly;
    protected HashMap<String, SessionListStats> all;

    protected ListView list;
    protected Spinner tbSpinner;
    protected Spinner tfSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (savedInstanceState != null) {
            this.tbSpinnerPos = savedInstanceState.getInt("tbSpinnerPos");
            this.tfSpinnerPos = savedInstanceState.getInt("tfSpinnerPos");
        }

        this.list = (ListView)findViewById(R.id.list);
        this.tbSpinner = (Spinner) findViewById(R.id.timeblocks);
        this.tfSpinner = (Spinner) findViewById(R.id.timeframes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeblocks_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item_view);
        this.tbSpinner.setAdapter(adapter);
        this.tbSpinner.setSelection(tbSpinnerPos);

        new PopulateSpinnerDataSources().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                EditFinishedSessionFragment dialog = new EditFinishedSessionFragment();
                dialog.setArguments(b);
                dialog.show(manager, "EditHistory");
            }
        });
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tbSpinnerPos", this.tbSpinnerPos);
        outState.putInt("tfSpinnerPos", this.tfSpinnerPos);
    }

    /*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.timeblocks :
                tbSpinnerPos = pos;
                this.tfSpinner.setEnabled(true);
                ArrayAdapter tfAdapter;

                switch (tbSpinnerPos) {
                    case 0 :
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.weeklyList);
                        break;
                    case 2 :
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.yearlyList);
                        break;
                    case 3 :
                        this.tfSpinner.setEnabled(false);
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.allList);
                        break;
                    default :
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.monthlyList);
                }
                tfAdapter.setDropDownViewResource(R.layout.spinner_item_view);
                this.tfSpinner.setAdapter(tfAdapter);
                break;
            case R.id.timeframes :
                tfSpinnerPos = pos;
                displayStats();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new PopulateSpinnerDataSources().execute();
        }
    }

    public class PopulateSpinnerDataSources extends AsyncTask<Void, Void, ArrayList<Session>> {

        @Override
        protected ArrayList<Session> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getSessions(0);
        }

        @Override
        protected void onPostExecute(ArrayList<Session> result) {
            String startDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String weekOfYear;
            String month;
            String year;

            //these populate the timeframes spinner and the Strings are the keys of the corresponding HashMaps
            HistoryActivity.this.weeklyList = new ArrayList<String>();
            HistoryActivity.this.monthlyList = new ArrayList<String>();
            HistoryActivity.this.yearlyList = new ArrayList<String>();
            HistoryActivity.this.allList = new ArrayList<String>();

            //these populate the history list and store the overview info
            HistoryActivity.this.weekly = new HashMap<String, SessionListStats>();
            HistoryActivity.this.monthly = new HashMap<String, SessionListStats>();
            HistoryActivity.this.yearly = new HashMap<String, SessionListStats>();
            HistoryActivity.this.all = new HashMap<String, SessionListStats>();

            HistoryActivity.this.allList.add("N/A");
            HistoryActivity.this.all.put("N/A", new SessionListStats());

            //for each session get session start time
            for (Session s : result) {
                startDate = s.getStartDate() + " " + s.getStartTime();
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(sdf.parse(startDate));
                } catch (Exception e) {
                    //fucking parse exception needed to be handled
                }
                year = Integer.toString(cal.get(Calendar.YEAR));
                weekOfYear = "Week" + " " + cal.get(Calendar.WEEK_OF_YEAR) + " " + year;
                month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + year;

                if (HistoryActivity.this.weekly.get(weekOfYear) == null) {
                    HistoryActivity.this.weeklyList.add(weekOfYear);
                    HistoryActivity.this.weekly.put(weekOfYear, new SessionListStats());
                }

                if (HistoryActivity.this.monthly.get(month) == null) {
                    HistoryActivity.this.monthlyList.add(month);
                    HistoryActivity.this.monthly.put(month, new SessionListStats());
                }

                if (HistoryActivity.this.yearly.get(year) == null) {
                    HistoryActivity.this.yearlyList.add(year);
                    HistoryActivity.this.yearly.put(year, new SessionListStats());
                }

                HistoryActivity.this.weekly.get(weekOfYear).addSession(s);
                HistoryActivity.this.monthly.get(month).addSession(s);
                HistoryActivity.this.yearly.get(year).addSession(s);
                HistoryActivity.this.all.get("N/A").addSession(s);
            }

            HistoryActivity.this.tbSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    HistoryActivity.this.tbSpinnerPos = pos;
                    HistoryActivity.this.tfSpinner.setEnabled(true);
                    ArrayAdapter tfAdapter;

                    switch (tbSpinnerPos) {
                        case 0 :
                            tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.weeklyList);
                            break;
                        case 2 :
                            tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.yearlyList);
                            break;
                        case 3 :
                            HistoryActivity.this.tfSpinner.setEnabled(false);
                            tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.allList);
                            break;
                        default :
                            tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.monthlyList);
                    }
                    tfAdapter.setDropDownViewResource(R.layout.spinner_item_view);
                    HistoryActivity.this.tfSpinner.setAdapter(tfAdapter);
                }

                public void onNothingSelected(AdapterView<?> arg0){

                }
            });

            HistoryActivity.this.tfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    HistoryActivity.this.tfSpinnerPos = pos;
                    displayStats();
                }

                public void onNothingSelected(AdapterView<?> arg0){

                }
            });

            /*
            if (init) {
                ArrayAdapter tfAdapter;
                switch (tbSpinnerPos) {
                    case 0 :
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.weeklyList);
                        break;
                    case 2 :
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.yearlyList);
                        break;
                    case 3 :
                        HistoryActivity.this.tfSpinner.setEnabled(false);
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.allList);
                        break;
                    default :
                        tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.monthlyList);
                }
                tfAdapter.setDropDownViewResource(R.layout.spinner_item_view);
                HistoryActivity.this.tfSpinner.setAdapter(tfAdapter);
                HistoryActivity.this.tfSpinner.setSelection(tfSpinnerPos);
                init = false;
            }
            */

            ArrayAdapter tfAdapter;
            switch (tbSpinnerPos) {
                case 0 :
                    tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.weeklyList);
                    break;
                case 2 :
                    tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.yearlyList);
                    break;
                case 3 :
                    HistoryActivity.this.tfSpinner.setEnabled(false);
                    tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.allList);
                    break;
                default :
                    tfAdapter = new ArrayAdapter(HistoryActivity.this, android.R.layout.simple_spinner_item, HistoryActivity.this.monthlyList);
            }
            tfAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            HistoryActivity.this.tfSpinner.setAdapter(tfAdapter);
            HistoryActivity.this.tfSpinner.setSelection(tfSpinnerPos);
            displayStats();
        }
    }

    protected void displayStats() {
        HistoryListAdapter adapter;
        SessionListStats stats;
        if (tfSpinner.getAdapter().getCount() > 0) {
            switch (tbSpinnerPos) { 
                case 0 :
                    stats = HistoryActivity.this.weekly.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
                    break;
                case 2 :
                    stats = HistoryActivity.this.yearly.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
                    break;
                case 3 :
                    stats = HistoryActivity.this.all.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
                    break;
                default :
                    stats = HistoryActivity.this.monthly.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
            }

            if (stats.getSessions().size() > 0) {
                adapter = new HistoryListAdapter(HistoryActivity.this, stats.getSessions());
                HistoryActivity.this.list.setAdapter(adapter);
            }

            TextView profit = (TextView) findViewById(R.id.profit);
            TextView time = (TextView) findViewById(R.id.time_played);
            TextView hourly = (TextView) findViewById(R.id.hourly_wage);

            if (stats.getProfit() < 0) {
                profit.setTextColor(Color.parseColor("#ff0000"));
                hourly.setTextColor(Color.parseColor("#ff0000"));
            }
            else {
                profit.setTextColor(Color.parseColor("#008000"));
                hourly.setTextColor(Color.parseColor("#008000"));
            }

            profit.setText(stats.profitFormatted());
            time.setText(stats.timeFormatted());
            hourly.setText(stats.wageFormatted());
        }
    }

    protected void notifyListChange() {
        //this method is necessary because i cant get fragments to call async tasks
        new PopulateSpinnerDataSources().execute();
    }
}