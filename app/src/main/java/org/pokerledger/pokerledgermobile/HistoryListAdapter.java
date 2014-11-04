package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.pokerledger.pokerledgermobile.model.Session;

import java.util.ArrayList;
/**
 * Created by Max on 11/2/14.
 */
public class HistoryListAdapter extends ArrayAdapter<Session> {
    private final Activity context;
    private final ArrayList<Session> active;

    public HistoryListAdapter(Activity context, ArrayList<Session> active) {
        super(context, R.layout.list_history, active);
        this.context = context;
        this.active= active;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_history, null, true);

        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);
        TextView txtStart = (TextView) rowView.findViewById(R.id.start);
        TextView txtProfit = (TextView) rowView.findViewById(R.id.profit);
        TextView txtGame = (TextView) rowView.findViewById(R.id.game);

        txtLocation.setText(active.get(position).getLocation().getLocation());
        txtStart.setText(active.get(position).getStart());
        txtProfit.setText("$" + Integer.toString(active.get(position).getCashOut() - active.get(position).getBuyIn()));
        txtGame.setText(active.get(position).getStructure().getStructure()+ " " + active.get(position).getGame().getGame());
        return rowView;
    }

}
