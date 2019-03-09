package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int initialZoom = 10;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, initialZoom));
        for (int i = 1; i < ParserCSV.Instance.electricalTerminals.size(); i++) {
            try {
                Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLatitude());
                tabMarker.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLongitude())))
                        .title(ParserCSV.Instance.electricalTerminals.get(i).getNameElectricalTerminal())));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                for (int i = 0; i < tabMarker.size(); i++) {
                    if (isMarkerClose(i))
                        tabMarker.get(i).setVisible(true);
                    else
                        tabMarker.get(i).setVisible(false);
                }
            }
        });
    }

    public boolean isMarkerClose(int index)
    {
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target,
                new LatLng(tabMarker.get(index).getPosition().latitude, tabMarker.get(index).getPosition().longitude)) < 30000);
    }
}
