package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdate;
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

    private static final int initialZoom = 8;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    private GoogleMap mMap;
    private ArrayList<Marker> markersTerminal;
    private CheckBox checkTerminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try
        {
            final InputStream FICHIER = this.getResources().openRawResource(R.raw.bornes);
            ParserElectrical.Instance.Parse(FICHIER);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        markersTerminal = new ArrayList<>();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        checkTerminal = findViewById(R.id.checkBox);
        checkTerminal.setChecked(true);


        checkTerminal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude), mMap.getCameraPosition().zoom));
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, initialZoom));
        for (int i = 1; i < ParserElectrical.Instance.electricalTerminals.size(); i++) {
            try {
                if (Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLatitude()) < 90 &&
                        Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLatitude()) > 40 &&
                        Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLongitude()) < -60 &&
                        Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLongitude()) > -80)
                markersTerminal.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLongitude())))
                        .title(ParserElectrical.Instance.electricalTerminals.get(i).getNameElectricalTerminal())));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (checkTerminal.isChecked()) {
                    for (int i = 0; i < markersTerminal.size(); i++) {
                        if (isMarkerClose(i))
                            markersTerminal.get(i).setVisible(true);
                        else
                            markersTerminal.get(i).setVisible(false);
                    }
                }
                else {
                    for (int i = 0; i < markersTerminal.size(); i++) {
                            markersTerminal.get(i).setVisible(false);
                    }
                }
            }
        });
    }

    public boolean isMarkerClose(int index)
    {
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target,
                new LatLng(markersTerminal.get(index).getPosition().latitude, markersTerminal.get(index).getPosition().longitude)) < 30000);
    }
}
