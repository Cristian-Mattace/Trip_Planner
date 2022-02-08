package com.example.tripplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class SecondAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Tappe mDataset = new Tappe();
    private Trip trip = null;
    private static String fileName = null;
    private Context context;

    public class TripDetailsViewHolder extends RecyclerView.ViewHolder{

        private ImageButton delEvent = null;
        private ImageButton modEvent = null;
        private View v = null;
        public TripDetailsViewHolder(View v) {

            super(v);
            context =v.getContext();
            this.v = v;


            //gestisco l'onClick dell'ImageButton delete
            delEvent  = (ImageButton) v.findViewById(R.id.imageButtonDeleteEvent);
            delEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creo un alert dialog per l'eliminazione
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Sei sicuro di voler eliminare la tappa?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //rimuovo dal dataset il viaggio eliminato
                                    mDataset.removeTappa(getAdapterPosition());
                                    //chiamo la funzione di aggiornamento passandogli anche il nome del viaggio
                                    updateTrips(context);
                                    //notifico il cambiamento del dataset
                                    notifyDataSetChanged();
                                    //elimino il dialog
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) { dialog.cancel(); } });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });


            //gestisco l'onClick dell'ImageButton modifica
            modEvent  = (ImageButton) v.findViewById(R.id.imageButtonModifyEvent);
            modEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(context, ModEventActivity.class);
                    newIntent.putExtra("eventObject", (Serializable) mDataset.getTappa(getAdapterPosition()));
                    newIntent.putExtra("tripObject", (Serializable) trip);
                    newIntent.putExtra("nomeFile", fileName);
                    context.startActivity(newIntent);
                }
            });

        }


        //setto il testo della textview
        public void setText(String text){
            TextView tView = (TextView)v.findViewById(R.id.textViewEvents);
            tView.setText(text);
        }

        //setto il testo delle altre textview
        public void setOtherText(String tipo, Tappa t){

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            if (tipo.equals("SPOSTAMENTO")){
                TextView partenza = (TextView)v.findViewById(R.id.textView1);
                TextView arrivo = (TextView)v.findViewById(R.id.textView2);
                TextView data = (TextView)v.findViewById(R.id.textView3);
                TextView trasporto = (TextView)v.findViewById(R.id.textView4);
                trasporto.setText("");

                partenza.setText("Da: " + t.getLuogoPartenza());
                arrivo.setText("A: " + t.getLuogoArrivo());
                data.setText("Il: " + df.format(t.getDataSpostamento()));
                if(t.getTrasporto() != "") trasporto.setText("Con: " + t.getTrasporto());

            }
            else if(tipo.equals("PERMANENZA")){
                TextView inizio = (TextView)v.findViewById(R.id.textView1);
                TextView fine = (TextView)v.findViewById(R.id.textView2);
                TextView luogo = (TextView)v.findViewById(R.id.textView3);
                TextView hotel = (TextView)v.findViewById(R.id.textView4);
                hotel.setText("");

                inizio.setText("Dal: " + df.format(t.getDataArrivo()));
                fine.setText("Al: " + df.format(t.getDataPartenza()));
                luogo.setText("A: " + t.getLuogoPermanenza());
                if(t.getHotel() != "") hotel.setText("Presso: " + t.getHotel());

            }
        }

    }


    //costruttore per l'adapter
    public SecondAdapter(Tappe tappe, Trip t) {
        mDataset = tappe;
        fileName = t.getNome();
        trip = t;
    }

    //abilitiamo la creazione di nuovi viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailstripview, parent, false);
        return new TripDetailsViewHolder(v);
    }

    //imposto la posizione corretta e il testo
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TripDetailsViewHolder tripDetailsViewHolder = (TripDetailsViewHolder)holder;
        //imposto il titolo
        tripDetailsViewHolder.setText(mDataset.getTappa(position).getTipoTappa());
        //imposto il resto delle textview
        tripDetailsViewHolder.setOtherText(mDataset.getTappa(position).getTipoTappa(), mDataset.getTappa(position));
    }

    //conta quanto è grande il dataset
    @Override
    public int getItemCount() { return mDataset.getSize(); }


    //metodo per aggiornare le tappe
    public void updateTrips(Context c) {
        try {
            //creo l'oggetto fos per aprire il file
            FileOutputStream fos = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            //scrivo nel file la lista di viaggi (serializzazione)
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mDataset);

            //chiudo tutto
            fos.close();
            oos.close();
        } catch (Exception e) {
            Toast.makeText(context, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }
    }

}
