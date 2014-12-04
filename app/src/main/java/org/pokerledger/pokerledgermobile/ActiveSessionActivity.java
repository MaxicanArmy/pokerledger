package org.pokerledger.pokerledgermobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

/**
 * Created by Max on 9/16/14.
 */
public class ActiveSessionActivity extends SessionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);

        new InitializeData().execute();
    }

    public void saveActiveSession(View v) {
        Session session = new Session();

        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, "You must enter a buy in amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            session.setBuyIn(Integer.parseInt(buyinText));
        }

        RadioButton tourneyRadio = (RadioButton) findViewById(R.id.radio_tourney);

        if (tourneyRadio.isChecked()) {
            String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

            if (!entrantsText.equals("")) {
                session.setEntrants(Integer.parseInt(entrantsText));
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
                this.setContentView(findViewById(R.id.small_blind));
                findViewById(R.id.small_blind).requestFocus();
                return;
            }

            if (sbText.equals("") && bbText.equals("") && straddleText.equals("") && bringInText.equals("") && anteText.equals("") && pointsText.equals("")) {
                Toast.makeText(this, "At least one blind must be entered for cash sessions.", Toast.LENGTH_SHORT).show();
                findViewById(R.id.small_blind).requestFocus();
                return;
            }

            String blinds = sbText + "SB" + bbText + "BB" + straddleText + "S" + bringInText + "B" + anteText + "A" + pointsText + "P";
            session.setBlinds(blinds);
        }

        String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();

        if (startDate.equals("Start Date")) {
            Toast.makeText(this, "Select a start date for this session.", Toast.LENGTH_SHORT).show();
            //((Button) findViewById(R.id.start_date)).focus
            return;
        }

        String startTime = ((Button) findViewById(R.id.start_time)).getHint().toString();

        if (startTime.equals("Start Time")) {
            Toast.makeText(this, "Select a start time for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        session.setStart(startDate + " " + startTime);

        String note = ((EditText) findViewById(R.id.note)).getText().toString();

        if (!note.equals("")) {
            session.setNote(note);
        }

        session.setStructure((Structure) ((Spinner) findViewById(R.id.structure)).getSelectedItem());
        session.setGame((Game) ((Spinner) findViewById(R.id.game)).getSelectedItem());

        Spinner location = (Spinner) findViewById(R.id.location);

        if (location.getSelectedItem() != null) {
            session.setLocation((Location) location.getSelectedItem());
        } else {
            Toast.makeText(this, "You must enter the location.", Toast.LENGTH_SHORT).show();
            return;
        }

        session.setState(1);
        new SaveSession().execute(session);
        setResult(RESULT_OK);
        finish();
    }
}