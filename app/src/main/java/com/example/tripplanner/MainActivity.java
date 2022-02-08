package com.example.tripplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final int CREATE_TRIP_REQUEST = 1;
    public static final String fileName = "trips";

    private RecyclerView mRecyclerView = null;
    private LinearLayoutManager mLayoutManager = null;
    private FirstAdapter fAdapter = null;
    private ImageButton addTrip = null;

    //private Trips viaggi;
    //private ArrayList<Trip> trip = new ArrayList<>();
    private Trips trip = null;
    private Context context = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trip = new Trips();

        try {
            //creo un file con la stringa filename
            File file = new File(this.getFilesDir(), fileName);
            //se non esiste il file oppure è null
            if (file == null || !file.exists()) {

                //creo l'oggetto fos per aprire il file
                FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                //scrivo nel file la lista di viaggi (serializzazione)
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(trip);

                //chiudo tutto
                fos.close();
                oos.close();
            } else {
                //creo l'oggetto fis per aprire il file
                FileInputStream fis = openFileInput(fileName);
                //leggo nel file la lista di viaggi (deserializzazione)
                ObjectInputStream is = new ObjectInputStream(fis);
                //setto la lista di viaggi come quella appena letta
                trip = ((Trips) is.readObject());

                //chiudo tutto
                fis.close();
                is.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }

        //setto il titolo dell'activity
        setTitle("I miei viaggi");


        //recupero l'oggetto recyclerview
        mRecyclerView  = (RecyclerView)findViewById(R.id.RecyclerViewTripList);

        //la lista ha grandezza variabile
        mRecyclerView.setHasFixedSize(false);

        //utilizzo un linear layout
        mLayoutManager  = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //specifico il mio adapter
        fAdapter  = new FirstAdapter(trip);
        mRecyclerView.setAdapter(fAdapter);


        //gestisco l'onClick dell'ImageButton +
        addTrip  = (ImageButton) findViewById(R.id.AddTripButton);
        addTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //recupero il context
                context = v.getContext();
                //creo l'intent per la nuova classe
                Intent newIntent = new Intent(context, AddTripActivity.class);
                //avvio l'activity aspettandomi un risultato
                startActivityForResult(newIntent, CREATE_TRIP_REQUEST);
            }
        });

    }


    //usato quando aspetto i risultati da un'altra activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //controllo che sia tutto ok
        if (requestCode == CREATE_TRIP_REQUEST && resultCode == RESULT_OK) {
            //controllo che il data abbia i seguenti parametri nell'extra
            if (data.hasExtra("nome") && data.hasExtra("descrizione")
                    && data.hasExtra("partenza") && data.hasExtra("ritorno")) {
                //creo e aggiungo un nuovo Trip alla lista presente
                trip.addTrip(new Trip(data.getStringExtra("nome"), data.getStringExtra("descrizione"),
                        data.getStringExtra("partenza"), data.getStringExtra("ritorno")));
                //avviso l'adapter che il data set è cambiato
                fAdapter.notifyDataSetChanged();
                //chiamo il metodo aggiornare il file contenente i viaggi
                updateTrips();
            }
        }
    }


    //metodo per aggiornare i viaggi
    public void updateTrips() {
        try {
            //creo l'oggetto fos per aprire il file
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            //scrivo nel file la lista di viaggi (serializzazione)
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(trip);
            //recupero l'istanza del trips e aggiorno la sua lista di viaggi
            Trips.getInstance().setTripList(trip.getTripList());

            //chiudo tutto
            fos.close();
            oos.close();
        } catch (Exception e) {
            Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }
    }

}