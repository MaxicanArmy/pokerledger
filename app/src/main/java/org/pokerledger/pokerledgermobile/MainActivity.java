package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.helper.SessionListStats;
import org.pokerledger.pokerledgermobile.model.Session;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    TextView profit;
    TextView timePlayed;
    TextView hourlyWage;
    TextView listHeader;
    ListView list;
    private static final int ACTIVE_RESULT = 1;
    private static final int FINISHED_RESULT = 2;
    private ProgressDialog pdia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int v = 0;

        listHeader = (TextView) findViewById(R.id.active_games_header);
        list = (ListView)findViewById(R.id.list);

        new LoadActiveSessions().execute();

        this.profit = (TextView) findViewById(R.id.profit);
        this.timePlayed = (TextView) findViewById(R.id.time_played);
        this.hourlyWage = (TextView) findViewById(R.id.hourly_wage);
        new LoadStatistics().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                EditActiveSessionFragment dialog = new EditActiveSessionFragment();
                dialog.setArguments(b);
                dialog.show(manager, "EditSession");
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if ((pdia != null) && pdia.isShowing())
            pdia.dismiss();
        pdia = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new LoadActiveSessions().execute();

            if (requestCode == FINISHED_RESULT) {
                new LoadStatistics().execute();
            }
        }
    }

    protected void notifyListChange() {
        //this method is necessary because i cant get fragments to call async tasks
        new LoadActiveSessions().execute();
    }

    public class LoadActiveSessions extends AsyncTask<Void, Void, Void> {
        ArrayList<Session> sessions = new ArrayList<Session>();

        public LoadActiveSessions() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            sessions = dbHelper.getSessions(1);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SessionListAdapter adapter = new SessionListAdapter(MainActivity.this, sessions);

            //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (sessions.size() > 0) {
                //float scale = getResources().getDisplayMetrics().density;
                //int dpAsPixels = (int) (8*scale + 0.5f);

                //params.setMargins(0,0,0,dpAsPixels);
                //params.addRule(RelativeLayout.BELOW, R.id.active_games_header);
                //list.setLayoutParams(params);
                listHeader.setVisibility(View.VISIBLE);
            } else {
                //params.setMargins(0,0,0,0);
                //list.setLayoutParams(params);
                listHeader.setVisibility(View.GONE);
            }

            list.setAdapter(adapter);
        }
    }

    public class LoadStatistics extends AsyncTask<Void, Void, SessionListStats> {

        @Override
        protected SessionListStats doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            return new SessionListStats(dbHelper.getSessions(0));
        }

        @Override
        protected void onPostExecute(SessionListStats stats) {
            if (stats.getProfit() < 0 ) {
                MainActivity.this.profit.setTextColor(Color.parseColor("#ff0000"));
                MainActivity.this.hourlyWage.setTextColor(Color.parseColor("#ff0000"));
            }

            MainActivity.this.profit.setText(stats.profitFormatted());
            MainActivity.this.timePlayed.setText(stats.timeFormatted());
            MainActivity.this.hourlyWage.setText(stats.wageFormatted());
        }
    }
}