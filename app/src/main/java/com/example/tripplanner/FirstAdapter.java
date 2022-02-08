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

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class FirstAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Trips mDataset = null;
    private Context context;
    public static final String fileName = "trips";


    //creo il viewholder
    public class TripListViewHolder extends RecyclerView.ViewHolder{

        private ImageButton delTrip = null;
        private View v = null;
        public TripListViewHolder(View v) {
            super(v);
            context =v.getContext();
            this.v = v;


            //metto il ascolto sul click
            v.setOnClickListener(new View.OnClickListener() {
                //invocato quando clicco su un viaggio
                @Override
                public void onClick(View v) {
                    //recupero la posizione del viaggio cliccato
                    int position = getAdapterPosition();
                    //creo un Trip come quello appena cliccato
                    Trip t = mDataset.getTrip(position);
                    //creo l'intent per la classe di destinazione
                    Intent tripDetailsIntent = new Intent(context, DetailsActivity.class);
                    //aggiungo l'extra contenente l'oggetto serializzato
                    tripDetailsIntent.putExtra("tripObject", (Serializable) t);
                    //passo all'altra activity
                    context.startActivity(tripDetailsIntent);
                }
            });

            //gestisco l'onClick dell'ImageButton delete
            delTrip  = (ImageButton) v.findViewById(R.id.imageButtonDeleteTrip);
            delTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creo un alert dialog per l'eliminazione
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Sei sicuro di voler eliminare il viaggio?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //passo il nome prima che venga eliminato
                                    String nome = mDataset.getTrip(getAdapterPosition()).getNome();
                                    //rimuovo dal dataset il viaggio eliminato
                                    mDataset.removeTrip(getAdapterPosition());
                                    //chiamo la funzione di aggiornamento passandogli anche il nome del viaggio
                                    updateTrips(context, nome);
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
        }

        //setto il testo della textview
        public void setText(String text){
            TextView tView = (TextView)v.findViewById(R.id.TripTextView);
            tView.setText(text);
        }

    }

    //costruttore per l'adapter
    public FirstAdapter(Trips viaggi) {
        mDataset = viaggi;
    }


    //abilitiamo la creazione di nuovi viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.triplistview, parent, false);
        return new TripListViewHolder(v);
    }

    //imposto la posizione corretta e il testo
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TripListViewHolder tripListViewHolder = (TripListViewHolder)holder;
        tripListViewHolder.setText(mDataset.getTrip(position).getNome());
    }

    //conta quanto è grande il dataset
    @Override
    public int getItemCount() { return mDataset.getSize(); }

    //metodo per aggiornare i viaggi
    public void updateTrips(Context c, String nome) {
        try {
            //creo l'oggetto fos per aprire il file
            FileOutputStream fos = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            //scrivo nel file la lista di viaggi (serializzazione)
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mDataset);
            //recupero l'istanza del trips e aggiorno la sua lista di viaggi
            Trips.getInstance().setTripList(mDataset.getTripList());

            //elimino il file delle tappe del viaggio
            context.deleteFile(nome);

            //chiudo tutto
            fos.close();
            oos.close();
        } catch (Exception e) {
            Toast.makeText(context, "C'è stato un problema", Toast.LENGTH_SHORT).show();
            Log.e("", "exception", e);
        }
    }
}
