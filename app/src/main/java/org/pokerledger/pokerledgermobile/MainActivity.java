package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.Session;

import java.util.ArrayList;


public class MainActivity extends Activity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(R.id.list);

        new LoadActiveSessions().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                EditSessionFragment dialog = new EditSessionFragment();
                dialog.setArguments(b);
                dialog.show(manager, "EditSession");
            }
        });

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
            case R.id.action_settings :
                return true;
            case R.id.add_session :
                FragmentManager manager = getFragmentManager();

                AddSessionFragment dialog = new AddSessionFragment();
                dialog.show(manager, "AddSession");
                break;
            case R.id.history :
                Intent history = new Intent(this, HistoryActivity.class);
                this.startActivity(history);
                break;
            case R.id.statistics :
                Intent statistics = new Intent(this, StatisticsActivity.class);
                this.startActivity(statistics);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String json = "";
            json = data.getStringExtra("SESSION_JSON");

            if (requestCode == 1) {
                new SaveActiveSession(new Gson().fromJson(json, Session.class)).execute();
            }
            else if (requestCode == 2) {
                new SaveFinishedSession(new Gson().fromJson(json, Session.class)).execute();
            }
        }
    }

    protected void notifyListChange() {
        new LoadActiveSessions().execute();
    }

    public class LoadActiveSessions extends AsyncTask<Void, Void, Void> {
        ArrayList<Session> sessions = new ArrayList<Session>();

        public LoadActiveSessions() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            sessions = db.getActiveSessions();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SessionListAdapter adapter = new SessionListAdapter(MainActivity.this, sessions);

            list.setAdapter(adapter);
        }
    }

    public class SaveActiveSession extends AsyncTask<Void, Void, Void> {
        Session current;

        public SaveActiveSession(Session s) {
            this.current = s;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.saveActive(this.current);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new LoadActiveSessions().execute();
        }
    }

    public class SaveFinishedSession extends AsyncTask<Void, Void, Void> {
        Session current;

        public SaveFinishedSession(Session s) {
            this.current = s;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.saveFinished(this.current);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new LoadActiveSessions().execute();
        }
    }
}
