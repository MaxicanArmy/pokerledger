package org.pokerledger.pokerledgermobile;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.model.Break;
import org.pokerledger.pokerledgermobile.model.Session;

import java.util.ArrayList;

/**
 * Created by Max on 9/25/14.
 */
public class BreakViewerFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private ArrayList<Break> breaks = new ArrayList<Break>();
    private ArrayAdapter<Break> adapter;
    private ListView breakList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_breaks_fragment, null, false);
        breakList = (ListView) view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString("SESSION_JSON") != null) {
            Gson gson = new Gson();
            Session current = gson.fromJson(getArguments().getString("SESSION_JSON"),Session.class);
            breaks = current.getBreaks();
        }

        adapter = new ArrayAdapter<Break>(getActivity(), android.R.layout.simple_list_item_1, breaks);
        breakList.setAdapter(adapter);
        breakList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final FinishSessionActivity a = (FinishSessionActivity) getActivity();
        final int p = position;

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setTitle("Confirmation");
        dialog.setMessage("Are you sure you want to delete this break?");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                breaks.remove(p);
                a.current.setBreaks(breaks);
                adapter.notifyDataSetChanged();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }
}