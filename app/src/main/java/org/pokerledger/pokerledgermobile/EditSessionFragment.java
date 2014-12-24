package org.pokerledger.pokerledgermobile;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;
import org.pokerledger.pokerledgermobile.model.Session;

import java.util.ArrayList;

/**
 * Created by Max on 9/22/14.
 */
public class EditSessionFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    MainActivity activity;
    private Session current;
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
            this.current = gson.fromJson(getArguments().getString("SESSION_JSON"), Session.class);

            if (current.onBreak()) {
                options.add("Resume Session");
            }
            else {
                options.add("Pause Session");
            }
            options.add("Rebuy / Addon");
            options.add("Finish Session");
            options.add("Delete Session");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.activity = (MainActivity) getActivity();
        dismiss();

        if (position == 0) {
            new ToggleBreak().execute();
        }
        else if (position == 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Rebuy/Addon");

            //Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setHint("Amount");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int value = Integer.parseInt(input.getText().toString());
                    new RebuyAddon().execute(value);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            AlertDialog dialog = alert.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }
        else if (position == 2) {
            Intent intent = new Intent(this.activity, FinishSessionActivity.class);
            intent.putExtra("SESSION_JSON", getArguments().getString("SESSION_JSON"));
            this.activity.startActivityForResult(intent, 2);
        }
        else if (position == 3) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setTitle("Confirmation");
            dialog.setMessage("Are you sure you want to delete this session?");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    new DeleteActive().execute();
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

    public class ToggleBreak extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(EditSessionFragment.this.getActivity());
            db.toggleBreak(EditSessionFragment.this.current.getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EditSessionFragment.this.activity.notifyListChange();
        }
    }

    public class DeleteActive extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(EditSessionFragment.this.activity);
            db.deleteSession(EditSessionFragment.this.current.getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EditSessionFragment.this.activity.notifyListChange();
        }
    }

    public class RebuyAddon extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... amount) {
            DatabaseHelper db = new DatabaseHelper(EditSessionFragment.this.activity);
            db.rebuyAddon(EditSessionFragment.this.current.getId(), amount[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EditSessionFragment.this.activity.notifyListChange();
        }
    }
}