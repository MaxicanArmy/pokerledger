package org.pokerledger.pokerledgermobile;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Max on 9/16/14.
 */
public class FinishSessionActivity extends SessionActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_session);

        new SessionActivity.InitializeData().execute();

        String json = getIntent().getStringExtra("SESSION_JSON");
        if (json != null) {
            Gson gson = new Gson();
            this.current = gson.fromJson(json, Session.class);

            ((EditText) findViewById(R.id.buy_in)).setText(Integer.toString(current.getBuyIn()));

            if (current.getBlinds() == null) {
                RadioButton type = (RadioButton) findViewById(R.id.radio_tourney);
                type.toggle();
                this.toggleRadio(type);

                ((EditText) findViewById(R.id.entrants)).setText(Integer.toString(current.getEntrants()));
            }
            else {
                Pattern BLIND_PATTERN = Pattern.compile("([0-9]*)SB([0-9]*)BB([0-9]*)S([0-9]*)B([0-9]*)A([0-9]*)P");
                Matcher blindMatcher = BLIND_PATTERN.matcher(current.getBlinds());
                while (blindMatcher.find()) {
                    ((EditText) findViewById(R.id.small_blind)).setText(blindMatcher.group(1));
                    ((EditText) findViewById(R.id.big_blind)).setText(blindMatcher.group(2));
                    ((EditText) findViewById(R.id.straddle)).setText(blindMatcher.group(3));
                    ((EditText) findViewById(R.id.bring_in)).setText(blindMatcher.group(4));
                    ((EditText) findViewById(R.id.ante)).setText(blindMatcher.group(5));
                    ((EditText) findViewById(R.id.points)).setText(blindMatcher.group(6));
                }
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

            Calendar cal = Calendar.getInstance();
            DecimalFormat df = new DecimalFormat("00");
            ((Button) findViewById(R.id.end_date)).setHint(cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)));
            ((Button) findViewById(R.id.end_time)).setHint(df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE)));

            if (this.current.onBreak()) {
                current.breakEnd();
            }

            ((EditText) findViewById(R.id.note)).setText(current.getNote());
        }

        //RETRIEVE STRUCTURES, GAMES, AND LOCATIONS FROM DB AND LOAD VALUES IN TO THE SPINNERS
        new InitializeData().execute();
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

        if (startDate.equals("Start Date") || startTime.equals("Start Time") || endDate.equals("End Date") || endTime.equals("End Time")) {
            Toast.makeText(this, "Set start and end date/time before adding breaks.", Toast.LENGTH_SHORT).show();
        }
        else {
            this.current.setStart(startDate + " " + startTime);
            this.current.setEnd(endDate + " " + endTime);
            FragmentManager manager = getFragmentManager();
            AddBreakFragment dialog = new AddBreakFragment();
            dialog.show(manager, "AddBreak");
        }
    }

    public void saveFinishedSession(View v) {
        if (this.current == null) {
            this.current = new Session();
        }

        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, "You must enter a buy in amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            this.current.setBuyIn(Integer.parseInt(buyinText));
        }

        String cashOutText = ((EditText) findViewById(R.id.cash_out)).getText().toString();

        if (cashOutText.equals("")) {
            Toast.makeText(this, "You must enter a cash out amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            this.current.setCashOut(Integer.parseInt(cashOutText));
        }

        RadioButton tourneyRadio = (RadioButton) findViewById(R.id.radio_tourney);

        if (tourneyRadio.isChecked()) {
            String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

            if (entrantsText.equals("")) {
                Toast.makeText(this, "You must enter the number of entrants.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                this.current.setEntrants(Integer.parseInt(entrantsText));
            }

            String placedText = ((EditText) findViewById(R.id.placed)).getText().toString();

            if (placedText.equals("")) {
                Toast.makeText(this, "You must enter what position you placed.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                this.current.setPlaced(Integer.parseInt(placedText));
            }
        }
        else {
            String sbText = ((EditText) findViewById(R.id.small_blind)).getText().toString();
            String bbText = ((EditText) findViewById(R.id.big_blind)).getText().toString();
            String straddleText = ((EditText) findViewById(R.id.straddle)).getText().toString();
            String bringInText = ((EditText) findViewById(R.id.bring_in)).getText().toString();
            String anteText = ((EditText) findViewById(R.id.ante)).getText().toString();
            String pointsText = ((EditText) findViewById(R.id.points)).getText().toString();

            if (sbText.equals("") && !bbText.equals("")) {
                Toast.makeText(this, "If there is only one blind enter it in SB field.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sbText.equals("") && bbText.equals("") && straddleText.equals("") && bringInText.equals("") && anteText.equals("") && pointsText.equals("")) {
                Toast.makeText(this, "At least one blind must be entered for cash sessions.", Toast.LENGTH_SHORT).show();
                return;
            }

            String blinds = sbText + "SB" + bbText + "BB" + straddleText + "S" + bringInText + "B" + anteText + "A" + pointsText + "P";
            this.current.setBlinds(blinds);
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

        if (this.current.getEnd().compareTo(this.current.getStart()) < 0) {
            Toast.makeText(this, "Session end time must be after start time.", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((EditText) findViewById(R.id.note)).getText().toString();

        if (!note.equals("")) {
            this.current.setNote(note);
        }

        this.current.setStructure((Structure) ((Spinner) findViewById(R.id.structure)).getSelectedItem());
        this.current.setGame((Game) ((Spinner) findViewById(R.id.game)).getSelectedItem());
        this.current.setLocation((Location) ((Spinner) findViewById(R.id.location)).getSelectedItem());

        Gson gson = new Gson();
        String json = gson.toJson(this.current);

        Intent intent = new Intent();
        intent.putExtra("SESSION_JSON", json);
        setResult(RESULT_OK, intent);
        finish();
    }
}
