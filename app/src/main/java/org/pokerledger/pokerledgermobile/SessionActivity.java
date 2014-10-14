package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
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
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
    public void toggleRadio(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        View blinds1 = findViewById(R.id.blinds1);
        View blinds2 = findViewById(R.id.blinds2);
        View tourney = findViewById(R.id.tourney);

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_cash:
                if (checked) {
                    blinds1.setVisibility(View.VISIBLE);
                    blinds2.setVisibility(View.VISIBLE);
                    tourney.setVisibility(View.GONE);
                    break;
                }
            case R.id.radio_tourney:
                if (checked) {
                    blinds1.setVisibility(View.GONE);
                    blinds2.setVisibility(View.GONE);
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
                new AddLocation(value).execute();
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

    public class InitializeData extends AsyncTask<Void, Void, Void> {
        ArrayList<Structure> structures = new ArrayList<Structure>();
        ArrayList<Game> games = new ArrayList<Game>();
        ArrayList<Location> locations = new ArrayList <Location>();

        public InitializeData() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            structures = db.getAllStructures();
            games = db.getAllGames();
            locations = db.getAllLocations();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            ArrayAdapter locAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, locations);
            locationSpinner.setAdapter(locAdapter);

            Spinner gameSpinner = (Spinner) findViewById(R.id.game);
            ArrayAdapter gameAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, games);
            gameSpinner.setAdapter(gameAdapter);

            Spinner structureSpinner = (Spinner) findViewById(R.id.structure);
            ArrayAdapter structureAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, structures);
            structureSpinner.setAdapter(structureAdapter);


            if (current != null) {
                locationSpinner.setSelection(current.getLocation().getId() - 1);
                gameSpinner.setSelection(current.getGame().getId() - 1);
                structureSpinner.setSelection(current.getStructure().getId() - 1);
            }
        }
    }

    public class AddLocation extends AsyncTask<Void, Void, Void> {
        private String location;
        ArrayList<Location> locations = new ArrayList <Location>();

        public AddLocation(String name) {
            this.location = name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            db.addLocation(location);

            locations = db.getAllLocations();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            ArrayAdapter locAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, locations);
            locationSpinner.setAdapter(locAdapter);

            locationSpinner.setSelection(locations.size() - 1);
        }
    }
}