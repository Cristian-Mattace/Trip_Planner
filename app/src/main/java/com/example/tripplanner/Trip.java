package com.example.tripplanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class Trip implements Serializable {

    //impostato per problema serializzazione
    private static final long serialVersionUID = 7990042516409577925L;

    private static Trip instance;
    private String nome;
    private String descrizione;
    private String dataInizio;
    private String dataFine;
    private Tappe tappe;



    public Trip(String tripName, String tripDescrizione, String inizio, String fine){
        nome = tripName;
        descrizione = tripDescrizione;
        dataInizio = inizio;
        dataFine = fine;
        tappe = new Tappe();
    }

    public String getNome(){ return nome; }
    public  String getDescrizione(){ return descrizione; }
    public String getDataInizio(){ return dataInizio; }
    public String getDataFine(){ return dataFine; }

    public void setNome(String n) { nome = n; }
    public void setDescrizione(String d) { descrizione = d; }
    public void setDataInizio(String d1) { dataInizio = d1; }
    public void setDataFine(String d2) { dataFine = d2; }


    public void addTappa(Tappa t){
        tappe.addTrip(t);
        ordinamento(t);
    }
    public void removeTappa(int i){ tappe.removeTappa(i); }
    //public void removeTappa2(Tappa t){ tappe.removeTappa(t); }
    public Tappe getTappe() { return tappe; }
    public Tappa getTappaAtIndex(int i) { return tappe.getTappa(i); }
    public void setEventList(Tappe list) {
        tappe = list;
    }
    public static Trip getInstance() {
        return instance;
    }

    private void ordinamento(Tappa t){
        Collections.sort(tappe.getTappeList(), new Comparator<Tappa>(){
            @Override
            public int compare(Tappa o1, Tappa o2) {
                return o1.getDataOrdinamento().compareTo(o2.getDataOrdinamento());
            }
        });
    }




}
