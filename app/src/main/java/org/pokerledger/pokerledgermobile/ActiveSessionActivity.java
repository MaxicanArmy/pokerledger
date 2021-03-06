package org.pokerledger.pokerledgermobile;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.GameFormat;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.util.Calendar;

/**
 * Created by Max on 9/16/14.
 */
public class ActiveSessionActivity extends SessionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);


        Spinner formatsSpinner = (Spinner) findViewById(R.id.formats);

        formatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                View blinds = findViewById(R.id.blind_wrapper);
                View tourney = findViewById(R.id.tourney);

                // Check which radio button was clicked
                switch(((GameFormat) parentView.getItemAtPosition(position)).getFormatType().getId()) {
                    case 1:
                        blinds.setVisibility(View.VISIBLE);
                        tourney.setVisibility(View.GONE);
                        break;
                    case 2:
                        blinds.setVisibility(View.GONE);
                        tourney.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Calendar calender = Calendar.getInstance();
        ((Button) findViewById(R.id.start_date)).setHint(String.format("%04d-%02d-%02d", calender.get(Calendar.YEAR), calender.get(Calendar.MONTH)+1, calender.get(Calendar.DAY_OF_MONTH)));
        ((Button) findViewById(R.id.start_time)).setHint(String.format("%02d:%02d", calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE)));

        //RETRIEVE STRUCTURES, GAMES, AND LOCATIONS FROM DB AND LOAD VALUES IN TO THE SPINNERS
        new InitializeData().execute();
    }

    public void saveActiveSession(View v) {

        //begin error checking and capturing the values to be saved in the db
        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, "You must enter a buy in amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            this.current.setBuyIn(Integer.parseInt(buyinText));
        }

        Spinner formatSpinner = (Spinner) findViewById(R.id.formats);

        if (formatSpinner.getSelectedItem() != null) {
            this.current.setFormat((GameFormat) formatSpinner.getSelectedItem());

            if (this.current.getFormat().getFormatType().getId() == 2) {
                String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

                if (!entrantsText.equals("")) {
                    this.current.setEntrants(Integer.parseInt(entrantsText));
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

        this.current.setState(1);
        new SaveSession().execute(this.current);
        setResult(RESULT_OK);
        finish();
    }
}