package org.pokerledger.pokerledgermobile;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.Session;

import java.util.ArrayList;

/**
 * Created by Max on 11/3/14.
 */
public class EditHistoryFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    HistoryActivity activity;
    private Session active;
    private ArrayList<String> options = new ArrayList<String>();
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_session_fragment, null, false);
        list = (ListView) view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString("SESSION_JSON") != null) {
            Gson gson = new Gson();
            this.active = gson.fromJson(getArguments().getString("SESSION_JSON"), Session.class);

            options.add("Edit Session");
            options.add("Delete Session");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.activity = (HistoryActivity) getActivity();
        dismiss();

        if (position == 0) {
            Intent intent = new Intent(this.activity, UpdateSessionActivity.class);
            intent.putExtra("SESSION_JSON", getArguments().getString("SESSION_JSON"));
            this.activity.startActivityForResult(intent, 2);
        }
        else if (position == 1) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setTitle("Confirmation");
            dialog.setMessage("Are you sure you want to delete this session?");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    new DeleteFinished().execute();
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

    public class DeleteFinished extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(EditHistoryFragment.this.activity);
            db.deleteSession(EditHistoryFragment.this.active.getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EditHistoryFragment.this.activity.notifyListChange();
        }
    }
}