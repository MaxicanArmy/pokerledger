package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.model.Break;
import org.pokerledger.pokerledgermobile.model.Session;

import java.text.DecimalFormat;
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

        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);
        TextView txtStart = (TextView) rowView.findViewById(R.id.start);
        TextView txtProfit = (TextView) rowView.findViewById(R.id.profit);
        TextView txtGame = (TextView) rowView.findViewById(R.id.game);
        TextView txtBlinds = (TextView) rowView.findViewById(R.id.blinds);

        txtLocation.setText(active.get(position).getLocation().getLocation());

        try {
            txtBlinds.setText(active.get(position).getBlinds().toString());
        } catch (NullPointerException e) {
            txtBlinds.setText("Tournament");
        }

        Calendar t1 = Calendar.getInstance();
        Calendar t2 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            t1.setTime(sdf.parse(active.get(position).getStart()));
            t2.setTime(sdf.parse(active.get(position).getEnd()));
        } catch (Exception e) {
            //fucking parse exception needed to be handled
        }

        int minutes = (int) (t2.getTimeInMillis() - t1.getTimeInMillis())/60000;

        //start break time code
        ArrayList<Break> bl = active.get(position).getBreaks();

        if (bl != null) {
            if (!bl.isEmpty()) {
                int breakMinutes = 0;
                for (Break b : bl) {
                    Calendar bs = Calendar.getInstance();
                    Calendar be = Calendar.getInstance();
                    try {
                        bs.setTime(sdf.parse(b.getStart()));
                        be.setTime(sdf.parse(b.getEnd()));
                    } catch (Exception e) {
                        //fucking parse exception needed to be handled
                    }

                    breakMinutes += (int) (be.getTimeInMillis() - bs.getTimeInMillis()) / 60000;
                }
                minutes -= breakMinutes;
            }
        }
        //end break time code

        int hours = minutes / 60;
        int remainder = minutes % 60;

        String timePlayed = "";

        if (hours > 0) {
            timePlayed += Integer.toString(hours) + "h";
        }

        if (remainder > 0) {
            timePlayed += " " + remainder + "m";
        }

        txtStart.setText(timePlayed);

        int total = active.get(position).getCashOut() - active.get(position).getBuyIn();
        if (total < 0 ) {
            txtProfit.setTextColor(Color.parseColor("#ff0000"));
            txtProfit.setText("($" + Integer.toString(Math.abs(total)) + ")");
        } else {
            txtProfit.setText("$" + Integer.toString(total));
        }

        txtGame.setText(active.get(position).getStructure().getStructure()+ " " + active.get(position).getGame().getGame());
        return rowView;
    }

}
