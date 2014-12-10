package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Max on 9/17/14.
 */
public class SessionActivity extends Activity {
    Session current;
    View activeView;

    public void toggleRadio(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        View blinds = findViewById(R.id.blind_wrapper);
        View tourney = findViewById(R.id.tourney);

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_cash:
                if (checked) {
                    blinds.setVisibility(View.VISIBLE);
                    tourney.setVisibility(View.GONE);
                    break;
                }
            case R.id.radio_tourney:
                if (checked) {
                    blinds.setVisibility(View.GONE);
                    tourney.setVisibility(View.VISIBLE);
                    break;
                }
        }
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

    public void showTimePickerDialog(View v) {
        activeView = v;
        TimePickerFragment time = new TimePickerFragment();
        /**
         * Set Up Current Time Into dialog
         */
        Button timeBtn = (Button) v;
        Bundle args = new Bundle();
        if (timeBtn.getHint().toString().matches("[A-Za-z]* Time$")) {
            Calendar calender = Calendar.getInstance();
            args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
            args.putInt("min", calender.get(Calendar.MINUTE));
        }
        else {
            Pattern DATE_PATTERN = Pattern.compile("^(\\d{2}):(\\d{2})$");
            Matcher m = DATE_PATTERN.matcher(timeBtn.getHint().toString());

            while (m.find()) {
                args.putInt("hour", Integer.parseInt(m.group(1)));
                args.putInt("min", Integer.parseInt(m.group(2)));
            }

        }
        time.setArguments(args);
        /**
         * Set Call back to capture selected time
         */
        time.setCallBack(ontime);
        time.show(getFragmentManager(), "TimePicker");
    }

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int min) {
            Button b = (Button) activeView;
            b.setHint(String.format("%02d:%02d", hour, min));
        }
    };

    public void showNewLocationDialog(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add Location");

        //Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint("Location Name");
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                new AddLocation().execute(value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void showNewBlindDialog(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View blindEntryView = factory.inflate(R.layout.add_blinds_fragment, null);
        alert.setView(blindEntryView);

        alert.setTitle("Add Blinds");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String sbText = ((EditText) blindEntryView.getRootView().findViewById(R.id.small_blind)).getText().toString();
                String bbText = ((EditText) blindEntryView.getRootView().findViewById(R.id.big_blind)).getText().toString();
                String strText = ((EditText) blindEntryView.getRootView().findViewById(R.id.straddle)).getText().toString();
                String biText = ((EditText) blindEntryView.getRootView().findViewById(R.id.bring_in)).getText().toString();
                String anteText = ((EditText) blindEntryView.getRootView().findViewById(R.id.ante)).getText().toString();
                String ppText = ((EditText) blindEntryView.getRootView().findViewById(R.id.points)).getText().toString();

                int sb = 0, bb = 0, straddle = 0, bringIn = 0, ante = 0, perPoint = 0;

                if (!sbText.equals("")) {
                    sb = Integer.parseInt(sbText);
                }
                if (!bbText.equals("")) {
                    bb = Integer.parseInt(bbText);
                }
                if (!strText.equals("")) {
                    straddle = Integer.parseInt(strText);
                }
                if (!biText.equals("")) {
                    bringIn = Integer.parseInt(biText);
                }
                if (!anteText.equals("")) {
                    ante = Integer.parseInt(anteText);
                }
                if (!ppText.equals("")) {
                    perPoint = Integer.parseInt(ppText);
                }

                new AddBlinds().execute(new Blinds(sb, bb, straddle, bringIn, ante, perPoint));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void showBreaksDialog(View v) {
        if (current.getBreaks() == null) {
            Toast.makeText(this, "There are no breaks associated with this session.", Toast.LENGTH_LONG).show();
        }
        else {
            FragmentManager manager = getFragmentManager();
            Gson gson = new Gson();

            Bundle b = new Bundle();
            b.putString("SESSION_JSON", gson.toJson(current));

            ViewBreaksFragment dialog = new ViewBreaksFragment();
            dialog.setArguments(b);
            dialog.show(manager, "ViewBreaks");
        }
    }

    public void showAddBreakDialog(View v) {
        String startDate = ((Button) this.findViewById(R.id.start_date)).getHint().toString();
        String startTime = ((Button) this.findViewById(R.id.start_time)).getHint().toString();
        String endDate = ((Button) this.findViewById(R.id.end_date)).getHint().toString();
        String endTime = ((Button) this.findViewById(R.id.end_time)).getHint().toString();

        if (this.current == null) {
            this.current = new Session();
        }

        if (startDate.equals("Start Date") || startTime.equals("Start Time") || endDate.equals("End Date") || endTime.equals("End Time")) {
            Toast.makeText(this, "Set start/end date/time before adding breaks.", Toast.LENGTH_SHORT).show();
        }
        else {
            this.current.setStart(startDate + " " + startTime);
            this.current.setEnd(endDate + " " + endTime);
            FragmentManager manager = getFragmentManager();
            AddBreakFragment dialog = new AddBreakFragment();
            dialog.show(manager, "AddBreak");
        }
    }

    public class InitializeData extends AsyncTask<Void, Void, Void> {
        ArrayList<Structure> structures = new ArrayList<Structure>();
        ArrayList<Game> games = new ArrayList<Game>();
        ArrayList<Location> locations = new ArrayList <Location>();
        ArrayList<Blinds> blinds = new ArrayList <Blinds>();

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            structures = db.getAllStructures();
            games = db.getAllGames();
            locations = db.getAllLocations();
            blinds = db.getAllBlinds();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            Spinner gameSpinner = (Spinner) findViewById(R.id.game);
            Spinner structureSpinner = (Spinner) findViewById(R.id.structure);
            Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);

            ArrayAdapter locationAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, locations);
            locationAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            locationSpinner.setAdapter(locationAdapter);

            ArrayAdapter gameAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, games);
            gameAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            gameSpinner.setAdapter(gameAdapter);

            ArrayAdapter structureAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, structures);
            structureAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            structureSpinner.setAdapter(structureAdapter);

            ArrayAdapter blindsAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, blinds);
            blindsAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            blindsSpinner.setAdapter(blindsAdapter);

            if (current != null) {
                locationSpinner.setSelection(current.getLocation().getId() - 1);
                gameSpinner.setSelection(current.getGame().getId() - 1);
                structureSpinner.setSelection(current.getStructure().getId() - 1);

                if (current.getBlinds().getId() != 0) {
                    ((Spinner) findViewById(R.id.blinds)).setSelection(current.getBlinds().getId() - 1);
                }
            }
        }
    }

    public class AddLocation extends AsyncTask<String, Void, ArrayList<Location>> {
        @Override
        protected ArrayList<Location> doInBackground(String... name) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            db.addLocation(name[0]);

            return db.getAllLocations();
        }

        @Override
        protected void onPostExecute(ArrayList<Location> result) {
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            ArrayAdapter locAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, result);
            locAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            locationSpinner.setAdapter(locAdapter);

            locationSpinner.setSelection(result.size() - 1);
        }
    }

    public class AddBlinds extends AsyncTask<Blinds, Void, ArrayList<Blinds>> {
        @Override
        protected ArrayList<Blinds> doInBackground(Blinds... set) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            db.addBlinds(set[0]);

            return db.getAllBlinds();
        }

        @Override
        protected void onPostExecute(ArrayList<Blinds> result) {
            Spinner blindSpinner = (Spinner) findViewById(R.id.blinds);
            ArrayAdapter blindAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, result);
            blindAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            blindSpinner.setAdapter(blindAdapter);

            blindSpinner.setSelection(result.size() - 1);
        }
    }

    public class SaveSession extends AsyncTask<Session, Void, Void> {

        @Override
        protected Void doInBackground(Session... s) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.saveSession(s[0]);

            return null;
        }
    }
}