package com.example.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    public static final int CREATE_EVENT_REQUEST = 1;
    public static final int MOD_TRIP_REQUEST = 2;
    private String fileName = null;

    private RecyclerView mRecyclerView = null;
    private LinearLayoutManager mLayoutManager = null;
    private SecondAdapter sAdapter = null;
    private ImageButton addEventButton = null;
    private ImageButton mapButton = null;
    private Context context = null;

    private Trips trips;
    private Trip tripToDetail;
    //private ArrayList<Tappa> tappe = new ArrayList<>();
    private Tappe tappe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //recupero l'oggetto passato tramite intent ed extra
        tripToDetail = (Trip) getIntent().getExtras().getSerializable("tripObject");
        fileName = tripToDetail.getNome();

        try {
            //creo il file col nome del viaggio selezionato
            File file = new File(this.getFilesDir(), fileName);
            //se non esiste il file
            if (file == null || !file.exists()) {
                //creo l'oggetto fos per aprire il file
                FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                //scrivo nel file la lista di viaggi (serializzazione)
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(tripToDetail.getTappe());
                //chiudo tutto
                fos.close();
                oos.close();
            }
            //se esiste il file
            else {
                //creo l'oggetto fis per aprire il file
                FileInputStream fis = openFileInput(fileName);
                //leggo nel file la lista di viaggi (deserializzazione)
                ObjectInputStream is = new ObjectInputStream(fis);
                //setto la event list del teip selezionato
                tripToDetail.setEventList((Tappe) is.readObject());
                //chiudo tutto
                fis.close();
                is.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }



        //set up della toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbarDetails);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        setTitle(tripToDetail.getNome());

        //recupero le textview tramite l'id
        TextView datainizio = (TextView)findViewById(R.id.textViewInizio);
        TextView datafine = (TextView)findViewById(R.id.textViewFine);
        TextView descrizione = (TextView)findViewById(R.id.textViewDescrizione);

        //setto il testo delle textviev
        datainizio.setText("Da: " + tripToDetail.getDataInizio());
        datafine.setText("A: " + tripToDetail.getDataFine());
        descrizione.setText("Info: " + tripToDetail.getDescrizione());

        //recupero l'oggetto recyclerview
        mRecyclerView  = (RecyclerView)findViewById(R.id.recyclerViewTripDetails);

        //la lista ha grandezza variabile
        mRecyclerView.setHasFixedSize(false);

        //utilizzo un linear layout
        mLayoutManager  = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //specifico il mio adapter
        sAdapter  = new SecondAdapter(tripToDetail.getTappe(), tripToDetail);
        mRecyclerView.setAdapter(sAdapter);

        //gestisco l'onClick dell'ImageButton +
        addEventButton  = (ImageButton) findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context = v.getContext();
                Intent newIntent = new Intent(context, AddEventActivity.class);
                newIntent.putExtra("tripObject", (Serializable) tripToDetail);
                startActivityForResult(newIntent, CREATE_EVENT_REQUEST);
            }
        });

        //gestisco l'onClick dell'ImageButton +
        mapButton  = (ImageButton) findViewById(R.id.mapsButton);
        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context = v.getContext();
                Intent newIntent = new Intent(context, MapsActivity.class);
                newIntent.putExtra("tripObject", (Serializable) tripToDetail);
                startActivity(newIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //controllo la correttezza
        if (requestCode == CREATE_EVENT_REQUEST && resultCode == RESULT_OK) {
            //spostamento
            //controllo che il data abbia gli attributi seguenti nell'extra
            if (data.hasExtra("luogoPartenza") && data.hasExtra("luogoArrivo")
                    && data.hasExtra("data") && data.hasExtra("trasporto")) {

                tripToDetail.addTappa(new Tappa(
                        data.getStringExtra("luogoPartenza"),
                        data.getStringExtra("luogoArrivo"),
                        stringToDate(data.getStringExtra("data")),
                        data.getStringExtra("trasporto")));
                sAdapter.notifyDataSetChanged();
                updateEvent();

            }
            //permanenza
            //controllo che il data abbia gli attributi seguenti nell'extra
            else if (data.hasExtra("luogo") && data.hasExtra("dataInizio")
                    && data.hasExtra("dataFine") && data.hasExtra("hotel")) {

                tripToDetail.addTappa(new Tappa(
                        data.getStringExtra("luogo"),
                        stringToDate(data.getStringExtra("dataInizio")),
                        stringToDate(data.getStringExtra("dataFine")),
                        data.getStringExtra("hotel")));
                sAdapter.notifyDataSetChanged();
                updateEvent();
            }
        }
        else if (requestCode == MOD_TRIP_REQUEST && resultCode == RESULT_OK) {
            //controllo che il data abbia i seguenti parametri nell'extra
            if (data.hasExtra("nome") && data.hasExtra("descrizione")
                    && data.hasExtra("partenza") && data.hasExtra("ritorno")) {
                //creo un nuovo oggetto come il viaggio da modificare
                Trip vecchioTrip = tripToDetail;
                //creo il nuovo viaggio con i nuovi parametri
                Trip newTrip = new Trip(data.getStringExtra("nome"),
                        data.getStringExtra("descrizione"),
                        data.getStringExtra("partenza"),
                        data.getStringExtra("ritorno"));

                //chiamo il metodo aggiornare il file contenente i viaggi
                updateTrips(vecchioTrip, newTrip);
            }
        }
    }

    //creazione del menu nella toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trip_action_menu, menu);
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip_action_menu, menu);
    }


    //modifica del viaggio tramite click del menu nella toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_modify) {
            Intent newIntent = new Intent(this, ModTripActivity.class);
            newIntent.putExtra("tripObject", (Serializable) tripToDetail);
            startActivityForResult(newIntent, MOD_TRIP_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo per trasformare string in date
    public Date stringToDate(String d){
        Date mydate = null;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
        try {
            mydate = dateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mydate;
    }

    //metodo per l'aggiornamento
    public void updateEvent() {
        try {
            //creo l'oggetto fos per aprire il file
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            //scrivo nel file la lista di viaggi (serializzazione)
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tripToDetail.getTappe());
            //aggiorno la lista di eventi
            tripToDetail.setEventList(tripToDetail.getTappe());

            //chiudo tutto
            fos.close();
            oos.close();
        } catch (Exception e) {
            Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }
    }

    //metodo per aggiornare i viaggi
    public void updateTrips(Trip vecchioTrip, Trip nuovoTrip) {
        try {
            Trips tripList = new Trips();
            ArrayList<Trip> tripL = new ArrayList<Trip>();

            //creo l'oggetto fis per aprire il file
            FileInputStream fis = openFileInput("trips");
            //leggo nel file la lista di viaggi (deserializzazione)
            ObjectInputStream is = new ObjectInputStream(fis);
            //setto la lista di viaggi come quella appena letta
            tripList = (Trips) is.readObject();

            tripL = tripList.getTripList();
            //cerco il viaggio da modificare nella lista
            for(Trip x : tripL)  {
                if(x.getNome().equals(vecchioTrip.getNome())){
                    //recupero il suo indice
                    int cnt = tripL.indexOf(x);
                    //modifico il vecchio viaggio con quello nuovo
                    tripL.set(cnt, nuovoTrip);
                }
            }

            //creo l'oggetto fos per aprire il file
            FileOutputStream fos = openFileOutput("trips", Context.MODE_PRIVATE);
            //scrivo nel file la lista di viaggi (serializzazione)
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //scrivo la nuova lista
            tripList.setTripList(tripL);
            oos.writeObject(tripList);

            //chiudo tutto
            fos.close();
            oos.close();

            //modifico il nome del file delle tappe
            File nuovofileTappe = new File(this.getFilesDir(), nuovoTrip.getNome());
            File vecchiofileTappe = this.getFileStreamPath(tripToDetail.getNome());
            vecchiofileTappe.renameTo(nuovofileTappe);
            //torno alla main activity
            super.onNavigateUp();

        } catch (Exception e) {
            Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }
    }

}
