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

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.GameFormat;
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


        Spinner formatsSpinner = (Spinner) findViewById(R.id.formats);

        formatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                View blinds = findViewById(R.id.blind_wrapper);
                View tourney = findViewById(R.id.tourney);

                // Check which radio button was clicked
                switch (((GameFormat) parentView.getItemAtPosition(position)).getFormatType().getId()) {
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

        //RETRIEVE STRUCTURES, GAMES, AND LOCATIONS FROM DB AND LOAD VALUES IN TO THE SPINNERS
        new InitializeData().execute();

        String json = getIntent().getStringExtra("SESSION_JSON");
        if (json != null) {
            Gson gson = new Gson();
            this.current = gson.fromJson(json, Session.class);

            ((EditText) findViewById(R.id.buy_in)).setText(Integer.toString(this.current.getBuyIn()));

            if (this.current.getEntrants() != 0) {
                ((EditText) findViewById(R.id.entrants)).setText(Integer.toString(this.current.getEntrants()));
            }

            ((Button) findViewById(R.id.start_date)).setHint(this.current.getStartDate());
            ((Button) findViewById(R.id.start_time)).setHint(this.current.getStartTime());

            Calendar cal = Calendar.getInstance();
            DecimalFormat df = new DecimalFormat("00");
            ((Button) findViewById(R.id.end_date)).setHint(cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)));
            ((Button) findViewById(R.id.end_time)).setHint(df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE)));

            if (this.current.onBreak()) {
                this.current.breakEnd();
            }

            if (this.current.getNote() != "") {
                ((EditText) findViewById(R.id.note)).setText(this.current.getNote());
            }
        }
    }
}
