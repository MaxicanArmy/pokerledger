package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.helper.SessionListStats;
import org.pokerledger.pokerledgermobile.model.Break;
import org.pokerledger.pokerledgermobile.model.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Max on 11/2/14.
 */
public class HistoryListAdapter extends ArrayAdapter<Session> {
    private final Activity context;
    private final ArrayList<Session> active;

    public HistoryListAdapter(Activity context, ArrayList<Session> active) {
        super(context, R.layout.list_history, active);
        this.context = context;
        this.active = active;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_history, null, true);

        TextView txtDate = (TextView) rowView.findViewById(R.id.date);
        TextView txtProfit = (TextView) rowView.findViewById(R.id.profit);
        TextView txtGame = (TextView) rowView.findViewById(R.id.game);
        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);
        TextView txtTimePlayed = (TextView) rowView.findViewById(R.id.time_played);

        Session current = active.get(position);
        SessionListStats stats = new SessionListStats(active.get(position));

        txtDate.setText(current.getStartDate() + " " + current.getStartTime());

        txtGame.setText(current.displayFormat());

        if (stats.getProfit() < 0 ) {
            txtProfit.setTextColor(Color.parseColor("#ff0000"));
        }

        txtProfit.setText(stats.profitFormatted());

        txtLocation.setText(current.getLocation().getLocation());

        txtTimePlayed.setText(stats.timeFormatted());

        return rowView;
    }

}
