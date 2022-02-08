package com.example.tripplanner;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.tripplanner.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Trip trip = null;
    private Tappe tappe= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trip = (Trip)getIntent().getExtras().getSerializable("tripObject");
        tappe = trip.getTappe();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;
        LatLng x = null;

        //scorro tutte le tappe del viaggio
        for(int i=0; i<tappe.getSize(); i++) {
            //spostamento
            if(tappe.getTappa(i).getTipoTappa().toString().equals("SPOSTAMENTO")){
                try {
                    //aggiungo il marker al luogo di partenza
                    addresses = geocoder.getFromLocationName(tappe.getTappa(i).getLuogoPartenza().toString(), 1);
                    if(addresses.size() > 0) {
                        x = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(x).title(i+1 + " " + tappe.getTappa(i).getTipoTappa()));
                    }
                    //aggiungo il marker al luogo d'arrivo
                    addresses = geocoder.getFromLocationName(tappe.getTappa(i).getLuogoArrivo().toString(), 1);
                    if(addresses.size() > 0) {
                        x = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(x).title(i+1 + " " + tappe.getTappa(i).getTipoTappa()));
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
                    Log.e("", "exception", e);
                }
            }
            //permanenza
            else if(tappe.getTappa(i).getTipoTappa().toString().equals("PERMANENZA")){
                try {
                    //aggiungo il marker al luogo di permanenza
                    addresses = geocoder.getFromLocationName(tappe.getTappa(i).getLuogoPermanenza().toString(), 1);
                    if(addresses.size() > 0) {
                        x = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(x).title(i+1 + " " + tappe.getTappa(i).getTipoTappa()));
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "C'è stato un problema", Toast.LENGTH_SHORT).show();
                    Log.e("", "exception", e);
                }

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(x));
        }
    }
}