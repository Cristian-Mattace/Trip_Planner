package com.example.tripplanner;

import java.io.Serializable;
import java.util.Date;

public class Tappa implements Serializable {

    //impostato per problema serializzazione
    private static final long serialVersionUID = -7850498827797231730L;

    private String tipoTappa;

    private String luogoPartenza;
    private String luogoArrivo;
    private Date dataSpostamento;
    private String trasporto;

    private String luogoPermanenza;
    private Date dataArrivo;
    private Date dataPartenza;
    private String hotel;

    public Tappa(String partenza, String arrivo, Date spostamento){
        tipoTappa = "SPOSTAMENTO";
        luogoPartenza = partenza;
        luogoArrivo = arrivo;
        dataSpostamento = spostamento;
    }

    public Tappa(String partenza, String arrivo, Date spostamento, String descrizione){
        tipoTappa = "SPOSTAMENTO";
        luogoPartenza = partenza;
        luogoArrivo = arrivo;
        dataSpostamento = spostamento;
        trasporto = descrizione;
    }

    public Tappa(String luogo, Date arrivo, Date partenza){
        tipoTappa = "PERMANENZA";
        luogoPermanenza = luogo;
        dataArrivo = arrivo;
        dataPartenza = partenza;
    }

    public Tappa(String luogo, Date arrivo, Date partenza, String descrizione){
        tipoTappa = "PERMANENZA";
        luogoPermanenza = luogo;
        dataArrivo = arrivo;
        dataPartenza = partenza;
        hotel = descrizione;
    }

    public String getTipoTappa(){return tipoTappa;}

    public Date getDataOrdinamento(){
        if(tipoTappa.equals("SPOSTAMENTO")) return dataSpostamento;
        else if(tipoTappa.equals("PERMANENZA")) return dataArrivo;
        return null;
    }

    public Date getDataModificaViaggio(){
        if(tipoTappa.equals("SPOSTAMENTO")) return dataSpostamento;
        else if(tipoTappa.equals("PERMANENZA")) return dataPartenza;
        return null;
    }

    public String getLuogoPartenza(){return luogoPartenza;}
    public String getLuogoArrivo(){return luogoArrivo;}
    public Date getDataSpostamento(){return dataSpostamento;}
    public String getTrasporto(){return trasporto;}

    public String getLuogoPermanenza(){return luogoPermanenza;}
    public Date getDataArrivo(){return dataArrivo;}
    public Date getDataPartenza(){return dataPartenza;}
    public String getHotel(){return hotel;}
}