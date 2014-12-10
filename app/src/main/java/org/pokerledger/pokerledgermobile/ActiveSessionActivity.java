package org.pokerledger.pokerledgermobile;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.Blinds;
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

        //RETRIEVE STRUCTURES, GAMES, AND LOCATIONS FROM DB AND LOAD VALUES IN TO THE SPINNERS
        new InitializeData().execute();
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
            //next line is required to ensure that the database helper knows for what kind of session to generate queries (in case the user changed the session type)
            session.setBlinds(null);

            String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

            if (!entrantsText.equals("")) {
                session.setEntrants(Integer.parseInt(entrantsText));
            }
        }
        else {
            Spinner blinds = (Spinner) findViewById(R.id.blinds);

            if (blinds.getSelectedItem() != null) {
                session.setBlinds((Blinds) blinds.getSelectedItem());
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