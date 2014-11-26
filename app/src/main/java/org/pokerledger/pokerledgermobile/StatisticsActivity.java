package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;

/**
 * Created by Max on 9/26/14.
 */
public class StatisticsActivity extends Activity {
    TextView profit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        this.profit = (TextView) findViewById(R.id.profit);

        new LoadStatistics().execute();
    }

    public class LoadStatistics extends AsyncTask<Void, Void, Void> {
        int totalProfit;

        public LoadStatistics() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            totalProfit = db.getProfit();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            StatisticsActivity.this.profit.setText("$" + Integer.toString(this.totalProfit));
        }
    }
}
