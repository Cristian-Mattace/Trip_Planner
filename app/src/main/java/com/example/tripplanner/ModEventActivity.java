package com.example.tripplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ModEventActivity extends AppCompatActivity {

    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private Trip trip = null;
    private Tappa event = null;
    private static String fileName = null;

    private TextView t1 = null;
    private TextView t2 = null;
    private TextView t3 = null;
    private TextView t4 = null;

    private EditText e1 = null;
    private EditText e2 = null;
    private EditText e22 = null;
    private EditText e3 = null;
    private EditText e4 = null;

    private ImageButton data1 = null;
    private ImageButton data2 = null;
    private Button modifica = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_event);

        event = (Tappa) getIntent().getExtras().getSerializable("eventObject");
        fileName = (String) getIntent().getExtras().get("nomeFile");
        trip = (Trip) getIntent().getExtras().getSerializable("tripObject");

        setTitle("Modifica tappa");

        t1 = (TextView)findViewById(R.id.textViewModLuogo);
        t2 = (TextView)findViewById(R.id.textViewModEvent);
        t3 = (TextView)findViewById(R.id.textViewModEvent2);
        t4 = (TextView)findViewById(R.id.textViewModEvent3);

        e1 = (EditText)findViewById(R.id.editTextModificaLuogo);
        e3 = (EditText)findViewById(R.id.editTextModEvent3);
        e4 = (EditText)findViewById(R.id.editTextModEvent4);

        data1 = (ImageButton)findViewById(R.id.imageButtonModData);
        data2 = (ImageButton)findViewById(R.id.imageButtonModData2);

        modifica = (Button)findViewById(R.id.buttonModEvent);

        //rendo visibile il primo bottone data in base al tipo di tappa
        if(event.getTipoTappa().equals("SPOSTAMENTO")){
            e2 = (EditText)findViewById(R.id.editTextModEvent);
            e2.setVisibility(View.VISIBLE);
            e22 = (EditText)findViewById(R.id.editTextModEvent2);
            e22.setVisibility(View.INVISIBLE);
        }
        else{
            e2 = (EditText)findViewById(R.id.editTextModEvent2);
            e2.setVisibility(View.VISIBLE);
            e22 = (EditText)findViewById(R.id.editTextModEvent);
            e22.setVisibility(View.INVISIBLE);
            data1.setVisibility(View.VISIBLE);
        }

        setWidgetText(event.getTipoTappa(), t1, t2, t3, t4, e1, e2, e3, e4);


        //gestisco l'onClick dell'image button partenza
        data1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //creo il nuovo fragment
                DialogFragment newFragment = new DatePickerFragment();
                //creo il bundle, inserisco l'id della text view e lo passo come argomento al fragment
                Bundle bundle = new Bundle();
                bundle.putInt("EditText", R.id.editTextModEvent2);
                newFragment.setArguments(bundle);
                //chiamo il fragment
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        //gestisco l'onClick dell'image button ritorno
        data2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //creo il nuovo fragment
                DialogFragment newFragment = new DatePickerFragment();
                //creo il bundle, inserisco l'id della text view e lo passo come argomento al fragment
                Bundle bundle = new Bundle();
                bundle.putInt("EditText", R.id.editTextModEvent3);
                newFragment.setArguments(bundle);
                //chiamo il fragment
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //gestisco il click del button modifica
        modifica.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if(checkErrori())
                        finish();
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "C'è stato un problema", Toast.LENGTH_SHORT).show();
                    Log.e("", "exception", e);
                }
            }
        });


    }


    //setto il testo delle textview e delle edit text in base al tipo di tappa
    public void setWidgetText(String tipo, TextView t1, TextView t2, TextView t3, TextView t4,
                              EditText e1, EditText e2, EditText e3, EditText e4){




        if(tipo.equals("SPOSTAMENTO")) {
            t1.setText("Luogo di partenza");
            t2.setText("Luogo di destinazione");
            t3.setText("Data di spostamento");
            t4.setText("Trasporto");
            e1.setText(event.getLuogoPartenza());
            e2.setText(event.getLuogoArrivo());
            e3.setText(df.format(event.getDataSpostamento()));
            e4.setText(event.getTrasporto());
        }
        else if(tipo.equals("PERMANENZA")){
            t1.setText("Luogo di permanenza");
            t2.setText("Data inizio permanenza");
            t3.setText("Data fine permanenza");
            t4.setText("Hotel");
            e1.setText(event.getLuogoPermanenza());
            e2.setText(df.format(event.getDataArrivo()));
            e3.setText(df.format(event.getDataPartenza()));
            e4.setText(event.getHotel());
        }
    }


    public boolean checkErrori() throws ParseException, IOException {
        Toast toast = null;

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;

        String tipoTappa = event.getTipoTappa().toString();

        String edit1 = e1.getText().toString();
        String edit2 = e2.getText().toString();
        String edit3 = e3.getText().toString();
        String edit4 = e4.getText().toString();

        //usato per le textview data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        //controllo che i campi non siano vuoti
        if(edit1.length() == 0 || edit2.length() == 0 || edit3.length() == 0) {
            toast.makeText(this, "Inserisci tutti i campi!", toast.LENGTH_SHORT).show();
            return false;
        }
        //controllo che la data di partenza sia precedente a quella di arrivo
        else if(tipoTappa.equals("SPOSTAMENTO")){
            if(sdf.parse(edit3).before(sdf.parse(trip.getDataInizio())) || sdf.parse(edit3).after(sdf.parse(trip.getDataFine()))) {
                toast.makeText(this, "La data di spostamento deve essere compresa tra quelle del viaggio", toast.LENGTH_SHORT).show();
                return false;
            }
            //controllo che i luoghi inseriti esistano
            addresses = geocoder.getFromLocationName(edit1, 1);
            if(addresses.size() <= 0) {
                toast.makeText(this, "Luogo partenza non trovato!", toast.LENGTH_SHORT).show();
                return false;
            }
            addresses = geocoder.getFromLocationName(edit2, 1);
            if(addresses.size() <= 0) {
                toast.makeText(this, "Luogo destinazione non trovato!", toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(tipoTappa.equals("PERMANENZA")){
            if(sdf.parse(edit2).after(sdf.parse(edit3))) {
                toast.makeText(this, "La data di inizio deve essere precedente a quella di fine", toast.LENGTH_SHORT).show();
                return false;
            }
            else if(sdf.parse(edit2).before(sdf.parse(trip.getDataInizio())) ||
                    sdf.parse(edit2).after(sdf.parse(trip.getDataFine())) ||
                    sdf.parse(edit3).before(sdf.parse(trip.getDataInizio())) ||
                    sdf.parse(edit3).after(sdf.parse(trip.getDataFine()))) {
                toast.makeText(this, "Le date inserite devono essere comprese tra quelle del viaggio", toast.LENGTH_SHORT).show();
                return false;
            }
            //controllo che i luoghi inseriti esistano
            addresses = geocoder.getFromLocationName(edit1, 1);
            if(addresses.size() <= 0) {
                toast.makeText(this, "Luogo permanenza non trovato!", toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public void finish (){
        try {
            int cnt = 0;
            Tappe tp = trip.getTappe();
            Tappa nuovaTappa = null;

            //creo la nuova tappa in base al tipo
            if(event.getTipoTappa().toString().equals("SPOSTAMENTO")){
                nuovaTappa = new Tappa(e1.getText().toString(),
                                       e2.getText().toString(),
                                       df.parse(e3.getText().toString()),
                                       e4.getText().toString());
            }
            else if(event.getTipoTappa().toString().equals("PERMANENZA")){
                nuovaTappa = new Tappa(e1.getText().toString(),
                                       df.parse(e2.getText().toString()),
                                       df.parse(e3.getText().toString()),
                                       e4.getText().toString());
            }


            //cerco il viaggio da modificare nella lista
            ArrayList<Tappa> t = tp.getTappeList();
            for(Tappa x : tp.getTappeList())  {
                if(event.getTipoTappa().toString().equals("SPOSTAMENTO")){
                    if(x.getTipoTappa().equals(event.getTipoTappa()) &&
                            x.getLuogoPartenza().equals(event.getLuogoPartenza()) &&
                            x.getLuogoArrivo().equals(event.getLuogoArrivo()) &&
                            x.getDataSpostamento().equals(event.getDataSpostamento()) &&
                            x.getTrasporto().equals(event.getTrasporto())){
                        //recupero il suo indice

                        cnt = t.indexOf(x);
                    }
                }
                else if(event.getTipoTappa().toString().equals("PERMANENZA")){
                    if(x.getTipoTappa().equals(event.getTipoTappa()) &&
                            x.getLuogoPermanenza().equals(event.getLuogoPermanenza()) &&
                            x.getDataArrivo().equals(event.getDataArrivo()) &&
                            x.getDataPartenza().equals(event.getDataPartenza()) &&
                            x.getHotel().equals(event.getHotel())){
                        //recupero il suo indice
                        cnt = t.indexOf(x);
                    }
                }
            }

            //rimuovo la tappa e riaggiungo quella modificata
            trip.removeTappa(cnt);
            trip.addTappa(nuovaTappa);


            //creo l'oggetto fos per aprire il file
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            //scrivo nel file la lista di viaggi (serializzazione)
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(trip.getTappe());
            //aggiorno la lista di eventi
            trip.setEventList(trip.getTappe());

            //chiudo tutto
            fos.close();
            oos.close();

            //richiamo la main activity ripulendo lo stack delle activity
            Intent i=new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        } catch (Exception e) {
            Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }

    }
}