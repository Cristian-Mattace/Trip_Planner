package com.example.tripplanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tappe implements Serializable {

    private ArrayList<Tappa> tappeList;

    public Tappe() {tappeList = new ArrayList<Tappa>(); }
    public void setTappeList(ArrayList<Tappa> list) {
        tappeList = list;
    }
    public ArrayList<Tappa> getTappeList() { return tappeList; }
    public Tappa getTappa(int i) { return tappeList.get(i); }
    public void removeTappa(int i) { tappeList.remove(i); }
    public void addTrip(Tappa t) { tappeList.add(t); }
    public int getSize() { return tappeList.size(); }
}
