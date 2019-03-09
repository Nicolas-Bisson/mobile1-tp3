package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.InputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int initialZoom = 10;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    private GoogleMap mMap;

    //widgets
    private EditText searchText;

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

        searchText = (EditText) findViewById(R.id.searchText);
    }

    private void initSearch()
    {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
               /* if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE)
                {

                }*/
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, initialZoom));
        for (int i = 1; i < ParserCSV.Instance.electricalTerminals.size(); i++) {
            try {
                Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLatitude());
                if (SphericalUtil.computeDistanceBetween(new LatLng(mMap.getProjection().getVisibleRegion().nearLeft.latitude, mMap.getProjection().getVisibleRegion().nearLeft.longitude),
                        new LatLng(Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLongitude()))) > 10)
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.electricalTerminals.get(i).getLongitude())))
                        .title(ParserCSV.Instance.electricalTerminals.get(i).getNameElectricalTerminal()));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
        }
    }
}
