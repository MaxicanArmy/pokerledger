package org.pokerledger.pokerledgermobile;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Max on 9/16/14.
 */
public class AddSessionFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private ArrayList<String> options = new ArrayList<String>();
    private ListView myList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_session_fragment, null, false);
        myList = (ListView) view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        options.add("Active Session");
        options.add("Finished Session");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();

        if (position == 0) {
            Intent intent = new Intent(getActivity(), ActiveSessionActivity.class);
            getActivity().startActivityForResult(intent, 1);
        }
        else if (position == 1) {
            Intent intent = new Intent(getActivity(), FinishSessionActivity.class);
            getActivity().startActivityForResult(intent, 2);
        }
    }
}