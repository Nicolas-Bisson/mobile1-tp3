package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int initialZoom = 8;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    private GoogleMap mMap;
    private TextWatcher textWatcher;

    //widgets
    private EditText searchText;
    private ArrayList<Marker> markersTerminal;
    private CheckBox checkTerminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try
        {
            final InputStream FILE_ELECTRICAL_TERMINAL = this.getResources().openRawResource(R.raw.bornes);
            ParserElectricalTerminal.Instance.Parse(FILE_ELECTRICAL_TERMINAL);
            final InputStream FILE_POINT_OF_INTEREST_INFO = this.getResources().openRawResource(R.raw.attraitsinfo);
            ParserElectricalTerminal.Instance.Parse(FILE_POINT_OF_INTEREST_INFO);
            final InputStream FILE_POINT_OF_INTEREST_ADDRESS = this.getResources().openRawResource(R.raw.attraitsadresse);
            ParserElectricalTerminal.Instance.Parse(FILE_POINT_OF_INTEREST_ADDRESS);
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


        searchText = (EditText) findViewById(R.id.searchText);
    }

    private void initSearch()
    {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE)
                {
                    geoLocate();
                }
                return false;
            }
        });
    }



    private void geoLocate()
    {
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        try
        {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e)
        {
        }

        if (list.size() > 0)
        {
            Address address = list.get(0);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), initialZoom));
        }


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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, initialZoom));
        for (int i = 1; i < ParserElectricalTerminal.Instance.electricalTerminals.size(); i++) {
            try {
                if (Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude()) < 90 &&
                        Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude()) > 40 &&
                        Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude()) < -60 &&
                        Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude()) > -80)
                        tabMarker.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude())))
                        .title(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getNameElectricalTerminal())));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
        }

        initSearch();

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
