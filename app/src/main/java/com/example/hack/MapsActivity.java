package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        for (int i = 1; i < ParserCSV.Instance.bornes.size(); i++) {
            try {
                Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude());
                if (SphericalUtil.computeDistanceBetween(new LatLng(mMap.getProjection().getVisibleRegion().nearLeft.latitude, mMap.getProjection().getVisibleRegion().nearLeft.longitude),
                        new LatLng(Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLongitude()))) > 10)
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLongitude())))
                        .title(ParserCSV.Instance.bornes.get(i).getNomBorne()));
            }
            catch (NumberFormatException ex)
            {
            }
            }
    }
}
