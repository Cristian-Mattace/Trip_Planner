package com.example.tripplanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private Button spostamento = null;
    private Button permanenza = null;
    private Button salva = null;
    private ImageButton data1 = null;
    private ImageButton data2 = null;

    private TextView t1 = null;
    private TextView t2 = null;
    private TextView t3 = null;
    private TextView t4 = null;

    private EditText e1 = null;
    private EditText e2 = null;
    private EditText e22 = null;
    private EditText e3 = null;
    private EditText e4 = null;

    private String tipoTappa = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setTitle("Aggiungi Tappa");


        //recupero tutti i widget
        spostamento  = (Button) findViewById(R.id.buttonSpostamento);
        permanenza = (Button) findViewById(R.id.buttonPermanenza);
        data1 = (ImageButton) findViewById(R.id.imageButtonAddData1);
        data2 = (ImageButton) findViewById(R.id.imageButtonAddData2);
        salva = (Button) findViewById(R.id.buttonAddEvent);

        t1 = (TextView) findViewById(R.id.textViewLuogo);
        t2 = (TextView) findViewById(R.id.textViewEvent2);
        t3 = (TextView) findViewById(R.id.textViewEvent3);
        t4 = (TextView) findViewById(R.id.textViewEvent4);

        e1 = (EditText) findViewById(R.id.editTextLuogo);
        //e2 = (EditText) findViewById(R.id.editTextEvent2);
        e3 = (EditText) findViewById(R.id.editTextEvent3);
        e4 = (EditText) findViewById(R.id.editTextEvent4);


        //gestisco l'onClick del button spostamento
        spostamento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //setto il colore del bottone cliccato a verde
                spostamento.setBackgroundColor(Color.rgb(45, 205, 52));
                permanenza.setBackgroundColor(Color.rgb(165, 165, 165));
                //setto il tipo di tappa
                tipoTappa = "SPOSTAMENTO";
                //setto il testo in base al tipo tappa
                setWidgetText(tipoTappa, t1, t2, t3, t4);
                e2 = (EditText) findViewById(R.id.editTextEvent2);
                e22 = (EditText)findViewById(R.id.editTextEvent);
                e22.setVisibility(View.INVISIBLE);
                //rendo visibili i widget
                setVisibility(tipoTappa, t1, t2, t3, t4, e1, e2, e3, e4, salva, data1, data2);
            }
        });

        //gestisco l'onClick del button permanenza
        permanenza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                permanenza.setBackgroundColor(Color.rgb(45, 205, 52));
                spostamento.setBackgroundColor(Color.rgb(165, 165, 165));
                tipoTappa = "PERMANENZA";
                setWidgetText(tipoTappa, t1, t2, t3, t4);
                e2 = (EditText) findViewById(R.id.editTextEvent);
                e22 = (EditText)findViewById(R.id.editTextEvent2);
                e22.setVisibility(View.INVISIBLE);
                setVisibility(tipoTappa, t1, t2, t3, t4, e1, e2, e3, e4, salva, data1, data2);
            }
        });

        //gestisco l'onClick della prima edit text
        data1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    //creo il nuovo fragment
                    DialogFragment newFragment = new DatePickerFragment();
                    //creo il bundle, inserisco l'id della edit view e lo passo come argomento al fragment
                    Bundle bundle = new Bundle();
                    bundle.putInt("EditText", R.id.editTextEvent);
                    newFragment.setArguments(bundle);
                    //chiamo il fragment
                    newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //gestisco l'onClick della prima edit text
        data2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //creo il nuovo fragment
                DialogFragment newFragment = new DatePickerFragment();
                //creo il bundle, inserisco l'id della edit view e lo passo come argomento al fragment
                Bundle bundle = new Bundle();
                bundle.putInt("EditText", R.id.editTextEvent3);
                newFragment.setArguments(bundle);
                //chiamo il fragment
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //gestisco l'onClick della prima edit text
        salva.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if(checkErrori())
                        finish(e1.getText().toString(),
                                e2.getText().toString(),
                                e3.getText().toString(),
                                e4.getText().toString());
                }
                catch(Exception e){}
            }
        });


    }

    //metto visibili i widget
    public void setVisibility(String t, TextView t1, TextView t2, TextView t3, TextView t4, EditText e1, EditText e2,
                              EditText e3, EditText e4, Button s, ImageButton d1, ImageButton d2){
        t1.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        t3.setVisibility(View.VISIBLE);
        t4.setVisibility(View.VISIBLE);
        e1.setVisibility(View.VISIBLE);
        e2.setVisibility(View.VISIBLE);
        e3.setVisibility(View.VISIBLE);
        e4.setVisibility(View.VISIBLE);
        s.setVisibility(View.VISIBLE);
        d2.setVisibility(View.VISIBLE);
        if(t.equals("PERMANENZA")) {
            d1.setVisibility(View.VISIBLE);
        }
        else {
            d1.setVisibility(View.INVISIBLE);
        }
    }

    //setto il testo delle textview in base al tipo di tappa
    public void setWidgetText(String tipo, TextView t1, TextView t2, TextView t3, TextView t4){
        if(tipo.equals("SPOSTAMENTO")) {
            t1.setText("Luogo di partenza");
            t2.setText("Luogo di destinazione");
            t3.setText("Data di spostamento");
            t4.setText("Trasporto");
        }
        else if(tipo.equals("PERMANENZA")){
            t1.setText("Luogo di permanenza");
            t2.setText("Data inizio permanenza");
            t3.setText("Data fine permanenza");
            t4.setText("Hotel");
        }
    }


    public boolean checkErrori() throws ParseException, IOException {
        Toast toast = null;

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;

        String edit1 = e1.getText().toString();
        String edit2 = e2.getText().toString();
        String edit3 = e3.getText().toString();
        String edit4 = e4.getText().toString();

        //recupero l'oggetto per il controllo sulle date
        Trip trip = (Trip) getIntent().getExtras().getSerializable("tripObject");

        //usato per le textview data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        //ciclo che controlla che le date inserite non siano gia occupate da spostamenti o permanenze
        /*for(int i=0; i<trip.getTappe().size(); i++){
            Tappa t = trip.getTappe().get(i);
            if(tipoTappa == "SPOSTAMENTO") {
                if (t.getTipoTappa().equals("SPOSTAMENTO")) {
                    if(sdf.parse(edit3).equals(t.getDataSpostamento())) {
                        toast.makeText(this, "Data spostamento non valida!", toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else if (t.getTipoTappa().equals("PERMANENZA")) {
                    if(sdf.parse(edit3).after(t.getDataArrivo()) && sdf.parse(edit3).before(t.getDataPartenza())) {
                        toast.makeText(this, "Data spostamento non valida!", toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            else if(tipoTappa == "PERMANENZA"){
                if (t.getTipoTappa().equals("SPOSTAMENTO")) {
                    if(sdf.parse(edit2).equals(t.getDataSpostamento()) || sdf.parse(edit3).equals(t.getDataSpostamento())) {
                        toast.makeText(this, "Date permanenza non valide!", toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else if (t.getTipoTappa().equals("PERMANENZA")){
                    if((sdf.parse(edit2).after(t.getDataArrivo()) && sdf.parse(edit2).before(t.getDataPartenza())) ||
                       (sdf.parse(edit3).after(t.getDataArrivo()) && sdf.parse(edit3).before(t.getDataPartenza()))){
                        toast.makeText(this, "Date permanenza non valide!", toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }*/


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
            try{
                addresses = geocoder.getFromLocationName(edit1, 1);
                if(addresses.size() <= 0) {
                    toast.makeText(this, "Luogo partenza non trovato!", toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            catch(Exception e){
                toast.makeText(this, "Luogo partenza non trovato!", toast.LENGTH_SHORT).show();
                return false;
            }

            try{
                addresses = geocoder.getFromLocationName(edit2, 1);
                if(addresses.size() <= 0) {
                    toast.makeText(this, "Luogo destinazione non trovato!", toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            catch(Exception e){
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
            try{
                addresses = geocoder.getFromLocationName(edit1, 1);
                if(addresses.size() <= 0) {
                    toast.makeText(this, "Luogo permanenza non trovato!", toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            catch(Exception e){
                toast.makeText(this, "Luogo permanenza non trovato!", toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;
    }


    public void finish(String e1, String e2, String e3, String e4){
        //creo l'intent da passare alla main activity
        Intent returnIntent = new Intent(getBaseContext(), DetailsActivity.class);

        if(tipoTappa == "SPOSTAMENTO"){
            //inserisco gli extra contenenti i dati del viaggio
            returnIntent.putExtra("luogoPartenza", e1);
            returnIntent.putExtra("luogoArrivo", e2);
            returnIntent.putExtra("data", e3);
            returnIntent.putExtra("trasporto", e4);
        }
        else if(tipoTappa == "PERMANENZA"){
            returnIntent.putExtra("luogo", e1);
            returnIntent.putExtra("dataInizio", e2);
            returnIntent.putExtra("dataFine", e3);
            returnIntent.putExtra("hotel", e4);
        }

        setResult(RESULT_OK, returnIntent);

        super.finish();
    }

}