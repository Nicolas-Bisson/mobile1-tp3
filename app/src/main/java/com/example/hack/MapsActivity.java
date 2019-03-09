package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Parser.Instance.Parse();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng quebec = new LatLng(46.829853, -71.254028);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(quebec));
        mMap.setMinZoomPreference(8);
        int e = 0;
        for (int i = 0; i < Parser.Instance.bornes.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(Parser.Instance.bornes.get(i).getLatitude()), Double.parseDouble(Parser.Instance.bornes.get(i).getLongitude())))
                    .title(Parser.Instance.bornes.get(i).getNomBorne()));
        }
    }
}
