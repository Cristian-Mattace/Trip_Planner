package com.example.tripplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView data = null;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        //controllo che il bundle abbia qualche argomento
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if(bundle.containsKey("Text")) {//se contiene text lo recupero come text view
                //imposto la text locale come quella che è stata cliccata
                data = (TextView) getActivity().findViewById(bundle.getInt("Text"));
            }
            else if(bundle.containsKey("EditText")){//se contiene edit text lo recupero come edi text
                data = (EditText) getActivity().findViewById(bundle.getInt("EditText"));
            }
        }

        //creo la data sotto forma di stringa
        String strData = (String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year));
        //stampo il risultato
        data.setText(strData);
    }
}
