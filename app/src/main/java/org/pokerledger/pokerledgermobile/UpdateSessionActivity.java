package org.pokerledger.pokerledgermobile;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Max on 12/4/14.
 */
public class UpdateSessionActivity extends SessionActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_session);

        //RETRIEVE STRUCTURES, GAMES, AND LOCATIONS FROM DB AND LOAD VALUES IN TO THE SPINNERS
        new InitializeData().execute();

        String json = getIntent().getStringExtra("SESSION_JSON");
        if (json != null) {
            Gson gson = new Gson();
            this.current = gson.fromJson(json, Session.class);

            ((EditText) findViewById(R.id.buy_in)).setText(Integer.toString(current.getBuyIn()));
            ((EditText) findViewById(R.id.cash_out)).setText(Integer.toString(current.getCashOut()));

            if (current.getBlinds() == null) {
                RadioButton type = (RadioButton) findViewById(R.id.radio_tourney);
                type.toggle();
                this.toggleRadio(type);

                ((EditText) findViewById(R.id.entrants)).setText(Integer.toString(current.getEntrants()));
                ((EditText) findViewById(R.id.placed)).setText(Integer.toString(current.getPlaced()));
            }

            String startDateTime = current.getStart();

            Pattern DATE_PATTERN = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2})");
            Matcher m = DATE_PATTERN.matcher(startDateTime);
            Button startDateBtn = (Button) findViewById(R.id.start_date);
            String startDate;

            while (m.find()) {
                startDate = m.group(1);
                startDateBtn.setHint(startDate);
            }

            Pattern TIME_PATTERN = Pattern.compile(" (\\d{2}:\\d{2})$");
            m = TIME_PATTERN.matcher(startDateTime);
            Button startTimeBtn = (Button) findViewById(R.id.start_time);
            String startTime;

            while (m.find()) {
                startTime = m.group(1);
                startTimeBtn.setHint(startTime);
            }

            String endDateTime = current.getEnd();

            m = DATE_PATTERN.matcher(endDateTime);
            Button endDateBtn = (Button) findViewById(R.id.end_date);
            String endDate;

            while (m.find()) {
                endDate = m.group(1);
                endDateBtn.setHint(endDate);
            }

            m = TIME_PATTERN.matcher(endDateTime);
            Button endTimeBtn = (Button) findViewById(R.id.end_time);
            String endTime;

            while (m.find()) {
                endTime = m.group(1);
                endTimeBtn.setHint(endTime);
            }

            ((EditText) findViewById(R.id.note)).setText(current.getNote());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.add_session :
                FragmentManager manager = getFragmentManager();

                AddSessionFragment dialog = new AddSessionFragment();
                dialog.show(manager, "AddSession");
                break;
            case R.id.history :
                Intent history = new Intent(this, HistoryActivity.class);
                this.startActivity(history);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveFinishedSession(View v) {
        if (this.current == null) {
            this.current = new Session();
        }

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

        RadioButton tourneyRadio = (RadioButton) findViewById(R.id.radio_tourney);

        if (tourneyRadio.isChecked()) {
            //next line is required to ensure that the database helper knows for what kind of session to generate queries (in case the user changed the session type)
            this.current.setBlinds(null);

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

        this.current.setStart(startDate + " " + startTime);

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

        this.current.setEnd(endDate + " " + endTime);

        if (this.current.getEnd().compareTo(this.current.getStart()) <= 0) {
            Toast.makeText(this, "Session end time must be after start time.", Toast.LENGTH_SHORT).show();
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
        new SaveSession().execute(this.current);
        setResult(RESULT_OK);
        finish();
    }
}
