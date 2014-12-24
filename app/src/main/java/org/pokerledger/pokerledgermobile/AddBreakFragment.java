package org.pokerledger.pokerledgermobile;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.pokerledger.pokerledgermobile.model.Break;

import java.util.ArrayList;

/**
 * Created by Max on 9/26/14.
 */
public class AddBreakFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_break_fragment, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button startDate = (Button) view.findViewById(R.id.break_start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((SessionActivity) getActivity()).showDatePickerDialog(v);
            }
        });

        Button startTime = (Button) view.findViewById(R.id.break_start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((SessionActivity) getActivity()).showTimePickerDialog(v);
            }
        });

        Button endDate = (Button) view.findViewById(R.id.break_end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((SessionActivity) getActivity()).showDatePickerDialog(v);
            }
        });

        Button endTime = (Button) view.findViewById(R.id.break_end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((SessionActivity) getActivity()).showTimePickerDialog(v);
            }
        });

        Button addBreak = (Button) view.findViewById(R.id.add_break);
        addBreak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SessionActivity a = (SessionActivity) getActivity();

                String startDate = ((Button) getView().findViewById(R.id.break_start_date)).getHint().toString();
                String endDate = ((Button) getView().findViewById(R.id.break_end_date)).getHint().toString();
                String startTime = ((Button) getView().findViewById(R.id.break_start_time)).getHint().toString();
                String endTime = ((Button) getView().findViewById(R.id.break_end_time)).getHint().toString();

                String start = startDate + " " + startTime;
                String end = endDate + " " + endTime;

                //error conditions
                //add error condition for if nothing has been entered in one of the buttons
                //1. Break end datetime is before break start datetime
                if (end.compareTo(start) <= 0) {
                    Toast.makeText(a, "End date/time must be after start date/time.", Toast.LENGTH_SHORT).show();
                }
                //2. Break starts before session start datetime
                else if (start.compareTo(a.current.getStart()) < 0) {
                    Toast.makeText(a, "The break can't start before the session's start date/time.", Toast.LENGTH_SHORT).show();
                }
                //3. Break ends after session end datetime
                else if (end.compareTo(a.current.getEnd()) > 0) {
                    Toast.makeText(a, "The break can't end after the session's end date/time.", Toast.LENGTH_SHORT).show();
                }
                //4. Any part of the break overlaps with another break
                else {
                    ArrayList<Break> ba = a.current.getBreaks();
                    if (!ba.isEmpty()) {
                        for (Break b : ba) {
                            if ((start.compareTo(b.getEnd()) < 0) && (end.compareTo(b.getStart()) > 0)) {
                                Toast.makeText(a, "The break cannot overlap with another break in this session.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    ba.add(new Break(start, end));
                    a.current.setBreaks(ba);
                    dismiss();
                }
            }
        });

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
