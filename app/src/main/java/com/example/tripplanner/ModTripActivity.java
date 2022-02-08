package com.example.tripplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModTripActivity extends AppCompatActivity {

    private Trip tripToDetail;

    private EditText nomeV = null;
    private EditText descrizioneV = null;
    private TextView partenzaV = null;
    private TextView ritornoV = null;
    private ImageButton dataPartenza = null;
    private ImageButton dataRitorno = null;
    private Button modifica = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_trip);

        //recupero l'oggetto passato tramite intent ed extra
        tripToDetail = (Trip) getIntent().getExtras().getSerializable("tripObject");

        setTitle("Modifica " + tripToDetail.getNome());

        nomeV = (EditText)findViewById(R.id.editTextModificaNomeViaggio);
        descrizioneV = (EditText)findViewById(R.id.editTextModificaDescrizioneViaggio);
        partenzaV = (TextView)findViewById(R.id.textViewModificaDataPartenzaViaggio);
        ritornoV = (TextView)findViewById(R.id.textViewModificaDataRitornoViaggio);

        dataPartenza = (ImageButton)findViewById(R.id.imageButtonModificaDataPartenzaViaggio);
        dataRitorno = (ImageButton)findViewById((R.id.imageButtonModificaDataRitornoViaggio));

        modifica = (Button)findViewById(R.id.buttonModificaViaggio);

        nomeV.setText(tripToDetail.getNome());
        descrizioneV.setText(tripToDetail.getDescrizione());
        partenzaV.setText(tripToDetail.getDataInizio());
        ritornoV.setText(tripToDetail.getDataFine());

        //gestisco l'onClick dell'image button partenza
        dataPartenza  = (ImageButton) findViewById(R.id.imageButtonModificaDataPartenzaViaggio);
        dataPartenza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //creo il nuovo fragment
                DialogFragment newFragment = new DatePickerFragment();
                //creo il bundle, inserisco l'id della text view e lo passo come argomento al fragment
                Bundle bundle = new Bundle();
                bundle.putInt("Text", R.id.textViewModificaDataPartenzaViaggio);
                newFragment.setArguments(bundle);
                //chiamo il fragment
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        //gestisco l'onClick dell'image button ritorno
        dataRitorno  = (ImageButton) findViewById(R.id.imageButtonModificaDataRitornoViaggio);
        dataRitorno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //creo il nuovo fragment
                DialogFragment newFragment = new DatePickerFragment();
                //creo il bundle, inserisco l'id della text view e lo passo come argomento al fragment
                Bundle bundle = new Bundle();
                bundle.putInt("Text", R.id.textViewModificaDataRitornoViaggio);
                newFragment.setArguments(bundle);
                //chiamo il fragment
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        //gestisco l'onClick del button Salva
        modifica  = (Button) findViewById(R.id.buttonModificaViaggio);
        modifica.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if(checkErrori())
                        finish(nomeV.getText().toString(),
                                descrizioneV.getText().toString(),
                                partenzaV.getText().toString(),
                                ritornoV.getText().toString());
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "C'è stato un problema", Toast.LENGTH_SHORT).show();
                    Log.e("", "exception", e);
                }
            }
        });
    }



    public boolean checkErrori() throws ParseException {
        Toast toast = null;

        Integer cnt = tripToDetail.getTappe().getSize();

        String nome = nomeV.getText().toString();
        String descrizione = descrizioneV.getText().toString();
        String partenza = partenzaV.getText().toString();
        String ritorno = ritornoV.getText().toString();

        //recupero la data odierna
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        //usato per le textview data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if(nome.length() == 0) { //controllo che il nome non sia vuoto
            toast.makeText(this, "Inserisci il nome del viaggio!", toast.LENGTH_SHORT).show();
            return false;
        }
        else if(descrizione.length() == 0) { //controllo che la descrizione non sia vuota
            toast.makeText(this, "Inserisci la descrizione del viaggio!", toast.LENGTH_SHORT).show();
            return false;
        }
        else if(partenza.length() == 0){ //controllo che la data di partenza non sia vuota
            toast.makeText(this, "Inserisci la data di partenza del viaggio!", toast.LENGTH_SHORT).show();
            return false;
        }
        else if(ritorno.length() == 0){ //controllo che la data di arrivo non sia vuota
            toast.makeText(this, "Inserisci la data di ritorno del viaggio!", toast.LENGTH_SHORT).show();
            return false;
        }//controllo che la data di partenza sia precedente a quella di arrivo
        else if(sdf.parse(partenza).after(sdf.parse(ritorno))){
            toast.makeText(this, "La data di partenza deve essere precedente a quella di arrivo", toast.LENGTH_SHORT).show();
            return false;
        }//controllo che la data di partenza sia successiva alla data odierna
        else if(sdf.parse(partenza).before(sdf.parse(formattedDate))){
            toast.makeText(this, "La data di partenza deve essere successiva a quella odierna", toast.LENGTH_SHORT).show();
            return false;
        }//controllo che la data di partenza non sia successiva alla prima tappa e ritorno non sia precedente all'ultima
        else if(cnt>0) {
            Tappa t1 = tripToDetail.getTappe().getTappa(0);
            Tappa t2 = tripToDetail.getTappe().getTappa(cnt - 1);
            if (sdf.parse(partenza).after(t1.getDataModificaViaggio()) || sdf.parse(ritorno).before(t2.getDataModificaViaggio())) {
                toast.makeText(this, "Le date del viaggio devono poter contenere quelle delle tappe", toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void finish(String nome, String descrizione, String partenza, String ritorno){
        //creo l'intent da passare alla main activity
        Intent returnIntent = new Intent(getBaseContext(), MainActivity.class);

        //inserisco gli extra contenenti i dati del viaggio
        returnIntent.putExtra("nome", nome);
        returnIntent.putExtra("descrizione", descrizione);
        returnIntent.putExtra("partenza", partenza);
        returnIntent.putExtra("ritorno", ritorno);

        setResult(RESULT_OK, returnIntent);

        super.finish();
    }
}