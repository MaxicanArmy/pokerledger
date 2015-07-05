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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import org.pokerledger.pokerledgermobile.model.GameFormat;
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
public class SessionActivity extends BaseActivity {
    Session current = new Session();
    View activeView;

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

                if (!bbText.equals("") && sbText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Blinds not added. If there is only one blind enter it in SB.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!ppText.equals("") && (!sbText.equals("") || !bbText.equals("") || !strText.equals("") || !biText.equals("") || !anteText.equals(""))) {
                    Toast.makeText(getApplicationContext(), "Blinds not added. You cannot enter other blinds when using points.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!strText.equals("") && (sbText.equals("") || bbText.equals(""))) {
                    Toast.makeText(getApplicationContext(), "Blinds not added. You must enter a SB and BB to enter a straddle.", Toast.LENGTH_LONG).show();
                    return;
                }

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

                new AddBlinds().execute(new Blinds(sb, bb, straddle, bringIn, ante, perPoint, 0));
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
        if (current.getBreaks().size() == 0) {
            Toast.makeText(this, "There are no breaks associated with this session.", Toast.LENGTH_LONG).show();
        }
        else {
            FragmentManager manager = getFragmentManager();
            Gson gson = new Gson();

            Bundle b = new Bundle();
            b.putString("SESSION_JSON", gson.toJson(current));

            BreakViewerFragment dialog = new BreakViewerFragment();
            dialog.setArguments(b);
            dialog.show(manager, "ViewBreaks");
        }
    }

    public void showAddBreakDialog(View v) {
        String startDate = ((Button) this.findViewById(R.id.start_date)).getHint().toString();
        String startTime = ((Button) this.findViewById(R.id.start_time)).getHint().toString();
        String endDate = ((Button) this.findViewById(R.id.end_date)).getHint().toString();
        String endTime = ((Button) this.findViewById(R.id.end_time)).getHint().toString();

        if (startDate.equals("Start Date") || startTime.equals("Start Time") || endDate.equals("End Date") || endTime.equals("End Time")) {
            Toast.makeText(this, "Set start/end date/time before adding breaks.", Toast.LENGTH_SHORT).show();
        }
        else {
            this.current.setStartDate(startDate);
            this.current.setStartTime(startTime);
            this.current.setEndDate(endDate);
            this.current.setEndTime(endTime);
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
        ArrayList<GameFormat> formats = new ArrayList<GameFormat>();

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            structures = db.getAllStructures();
            games = db.getAllGames();
            locations = db.getAllLocations();
            blinds = db.getAllBlinds();
            formats = db.getAllFormats();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            Spinner gameSpinner = (Spinner) findViewById(R.id.game);
            Spinner structureSpinner = (Spinner) findViewById(R.id.structure);
            Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);
            Spinner formatSpinner = (Spinner) findViewById(R.id.formats);

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

            ArrayAdapter formatsAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, formats);
            formatsAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            formatSpinner.setAdapter(formatsAdapter);

            if (current.getId() != 0) {
                locationSpinner.setSelection(current.getLocation().getId() - 1);
                gameSpinner.setSelection(current.getGame().getId() - 1);
                structureSpinner.setSelection(current.getStructure().getId() - 1);
                formatSpinner.setSelection(current.getFormat().getId() - 1);

                if (current.getFormat().getFormatType().getId() == 1) {
                    int count = 0;
                    int SpinnerPos = -1;
                    while (SpinnerPos == -1 && count < blinds.size()) {
                        if (current.getBlinds().getId() == blinds.get(count).getId()) {
                            SpinnerPos = count;
                        }
                        count++;
                    }

                    blindsSpinner.setSelection(SpinnerPos);
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

    public class AddBlinds extends AsyncTask<Blinds, Void, Blinds> {
        ArrayList<Blinds> allBlinds = new ArrayList<Blinds>();

        @Override
        protected Blinds doInBackground(Blinds... set) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            Blinds newSet = db.addBlinds(set[0]);
            allBlinds = db.getAllBlinds();

            return newSet;
        }

        @Override
        protected void onPostExecute(Blinds result) {
            Spinner blindSpinner = (Spinner) findViewById(R.id.blinds);
            ArrayAdapter blindAdapter = new ArrayAdapter(SessionActivity.this, android.R.layout.simple_spinner_item, allBlinds);
            blindAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            blindSpinner.setAdapter(blindAdapter);

            int count = 0;
            int SpinnerPos = -1;
            while (SpinnerPos == -1 && count < allBlinds.size()) {
                if (result.getId() == allBlinds.get(count).getId()) {
                    SpinnerPos = count;
                }
                count++;
            }

            blindSpinner.setSelection(SpinnerPos);
        }
    }

    public void saveFinishedSession(View v) {
        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, "You must enter a buy in amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            this.current.setBuyIn(Integer.parseInt(buyinText));
        }

        String cashOutText = ((EditText) findViewById(R.id.cash_out)).getText().toString();

        if (cashOutText.equals("")) {
            Toast.makeText(this, "You must enter a cash out amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.cash_out).requestFocus();
            return;
        }
        else {
            this.current.setCashOut(Integer.parseInt(cashOutText));
        }

        String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();

        if (startDate.equals("Start Date")) {
            Toast.makeText(this, "Select a start date for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        String startTime = ((Button) findViewById(R.id.start_time)).getHint().toString();

        if (startTime.equals("Start Time")) {
            Toast.makeText(this, "Select a start time for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.current.setStartDate(startDate);
        this.current.setStartTime(startTime);

        String endDate = ((Button) findViewById(R.id.end_date)).getHint().toString();

        if (endDate.equals("End Date")) {
            Toast.makeText(this, "Select an end date for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        String endTime = ((Button) findViewById(R.id.end_time)).getHint().toString();

        if (endTime.equals("End Time")) {
            Toast.makeText(this, "Select an end time for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.current.setEndDate(endDate);
        this.current.setEndTime(endTime);

        if ((this.current.getEndDate() + this.current.getEndTime()).compareTo(this.current.getStartDate() + this.current.getStartTime()) <= 0) {
            Toast.makeText(this, "Session end time must be after start time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner formatSpinner = (Spinner) findViewById(R.id.formats);

        if (formatSpinner.getSelectedItem() != null) {
            this.current.setFormat((GameFormat) formatSpinner.getSelectedItem());

            if (this.current.getFormat().getFormatType().getId() == 2) {
                String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

                if (entrantsText.equals("")) {
                    Toast.makeText(this, "You must enter the number of entrants.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.entrants).requestFocus();
                    return;
                }
                else {
                    this.current.setEntrants(Integer.parseInt(entrantsText));
                }

                String placedText = ((EditText) findViewById(R.id.placed)).getText().toString();

                if (placedText.equals("")) {
                    Toast.makeText(this, "You must enter what position you placed.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.placed).requestFocus();
                    return;
                }
                else {
                    this.current.setPlaced(Integer.parseInt(placedText));
                }
            }
            else {
                Spinner blinds = (Spinner) findViewById(R.id.blinds);

                if (blinds.getSelectedItem() != null) {
                    this.current.setBlinds((Blinds) blinds.getSelectedItem());
                } else {
                    Toast.makeText(this, "You must enter the blinds.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(this, "You must select a format.", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((EditText) findViewById(R.id.note)).getText().toString();

        if (!note.equals("")) {
            this.current.setNote(note);
        }

        this.current.setStructure((Structure) ((Spinner) findViewById(R.id.structure)).getSelectedItem());
        this.current.setGame((Game) ((Spinner) findViewById(R.id.game)).getSelectedItem());

        Spinner location = (Spinner) findViewById(R.id.location);

        if (location.getSelectedItem() != null) {
            this.current.setLocation((Location) location.getSelectedItem());
        } else {
            Toast.makeText(this, "You must enter the location.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.current.setState(0);

        if (this.current.getId() == 0) {
            new SaveSession().execute(this.current);
        } else {
            new EditSession().execute(this.current);
        }

        setResult(RESULT_OK);
        finish();
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

    public class EditSession extends AsyncTask<Session, Void, Void> {

        @Override
        protected Void doInBackground(Session... s) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.editSession(s[0]);

            return null;
        }
    }
}