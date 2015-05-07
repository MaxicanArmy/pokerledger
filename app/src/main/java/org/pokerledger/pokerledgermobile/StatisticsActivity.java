package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.BSGStats;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Max on 9/26/14.
 */
public class StatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        new LoadBSGStatistics().execute();
    }

    public class LoadBSGStatistics extends AsyncTask<Void, Void, HashMap<String, BSGStats>> {
        @Override
        protected HashMap<String, BSGStats> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());

            return db.getBSGStats();
        }

        @Override
        protected void onPostExecute(HashMap<String, BSGStats> result) {

            LinearLayout bsgWrapper = (LinearLayout) findViewById(R.id.bsgWrapper);
            double hourly;
            LinearLayout rowWrapper;
            TextView currentBSG;
            TextView currentHourly;
            DecimalFormat df = new DecimalFormat("0.00");

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, .75f);

            LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, .25f);

            for (Map.Entry<String, BSGStats> entry : result.entrySet()) {
                rowWrapper = new LinearLayout(StatisticsActivity.this);
                currentBSG = new TextView(StatisticsActivity.this);
                currentHourly = new TextView(StatisticsActivity.this);
                currentHourly.setGravity(Gravity.END);

                BSGStats stats = entry.getValue();

                currentBSG.setText(stats.getBlinds().toString() + " " + stats.getStructure().toString() + " " + stats.getGame().toString());
                hourly = stats.getHourlyWage();

                if (hourly < 0 ) {
                    currentHourly.setTextColor(Color.parseColor("#ff0000"));
                    currentHourly.setText("($" + df.format(Math.abs(hourly)) + ")");
                } else {
                    currentHourly.setTextColor(Color.parseColor("#008000"));
                    currentHourly.setText("$" + df.format(hourly));
                }

                currentBSG.setLayoutParams(llp);
                currentHourly.setLayoutParams(llp2);

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (8*scale + 0.5f);

                rowWrapper.setPadding(0,0,0,dpAsPixels);
                rowWrapper.addView(currentBSG);
                rowWrapper.addView(currentHourly);

                bsgWrapper.addView(rowWrapper);
            }
        }
    }
}
