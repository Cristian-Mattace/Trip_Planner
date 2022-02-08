package com.example.tripplanner;

import java.io.Serializable;
import java.util.ArrayList;

public class Trips implements Serializable {
    private static Trips instance;
    private ArrayList<Trip> tripList;


    public Trips() {
        tripList = new ArrayList<Trip>();
    }
    public void setTripList(ArrayList<Trip> list) {
        tripList = list;
    }
    public ArrayList<Trip> getTripList() {
        return tripList;
    }
    public Trip getTrip(int i) { return tripList.get(i); }
    public void removeTrip(int i) { tripList.remove(i); }
    public void addTrip(Trip t) { tripList.add(t); }
    public int getSize() { return tripList.size(); }

    public static Trips getInstance() {
        if (instance == null) {
            instance = new Trips();
        }
        return instance;
    }

}
