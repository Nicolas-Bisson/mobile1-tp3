package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> tabMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try
        {
            final InputStream FICHIER = this.getResources().openRawResource(R.raw.bornes);
            ParserCSV.Instance.Parse(FICHIER);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        tabMarker = new ArrayList<>();

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
        mMap.setMinZoomPreference(5);
        for (int i = 1; i < ParserCSV.Instance.bornes.size(); i++) {
            try {
                Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude());
                if (isMarkerClose(i))
                    tabMarker.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLongitude())))
                            .title(ParserCSV.Instance.bornes.get(i).getNomBorne())));
            }
            catch (NumberFormatException ex)
            {
            }
            }
    }

    @Override
    public void onMapLoaded() {
        for (int i = 0; i < tabMarker.size(); i++) {
            if (isMarkerClose(i))
                tabMarker.get(i).setVisible(true);
            else
                tabMarker.get(i).setVisible(false);
        }
    }

    public boolean isMarkerClose(int index)
    {
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target,
                new LatLng(Double.parseDouble(ParserCSV.Instance.bornes.get(index).getLatitude()), Double.parseDouble(ParserCSV.Instance.bornes.get(index).getLongitude()))) < 30000);

    }
}
