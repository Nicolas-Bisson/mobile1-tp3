package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncParserElectricalTerminal.Listener, AsyncParserPointOfInterest.Listener {

    private static final int initialZoom = 12;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    private GoogleMap mMap;
    private TextWatcher textWatcher;
    private LatLng screenCenter;

    //widgets
    private EditText searchText;
    private ArrayList<Marker> markersTerminal;
    private ArrayList<Marker> markersInterest;
    private CheckBox checkTerminal;
    private CheckBox checkInterest;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        try
        {
            final InputStream FILE_ELECTRICAL_TERMINAL = this.getResources().openRawResource(R.raw.bornes);
            AsyncParserElectricalTerminal asyncParserElectricalTerminal = new AsyncParserElectricalTerminal(this);
            asyncParserElectricalTerminal.execute(FILE_ELECTRICAL_TERMINAL);
            final InputStream[] FILE_POINT_OF_INTEREST = new InputStream[]{this.getResources().openRawResource(R.raw.attraitsinfo),this.getResources().openRawResource(R.raw.attraitsadresse)};
            AsyncParserPointOfInterest asyncParserPointOfInterest = new AsyncParserPointOfInterest(this);
            asyncParserPointOfInterest.execute(FILE_POINT_OF_INTEREST);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }


        markersTerminal = new ArrayList<>();
        markersInterest = new ArrayList<>();

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(screenCenter, mMap.getCameraPosition().zoom));
            }
        });

        checkInterest = findViewById(R.id.checkBoxI);
        checkInterest.setChecked(false);

        checkInterest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(screenCenter, mMap.getCameraPosition().zoom));
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

        initSearch();

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                screenCenter = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
                if (checkTerminal.isChecked()) {
                    for (int i = 0; i < markersTerminal.size(); i++) {
                        if (isMarkerTerminalClose(i))
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
                if (checkInterest.isChecked()) {
                    for (int i = 0; i < markersInterest.size(); i++) {
                        if (isMarkerInterestClose(i))
                            markersInterest.get(i).setVisible(true);
                        else
                            markersInterest.get(i).setVisible(false);
                    }
                }
                else {
                    for (int i = 0; i < markersInterest.size(); i++) {
                        markersInterest.get(i).setVisible(false);
                    }
                }
            }
        });
    }

    private void setPointOfInterestNodes()
    {
        for (TreeMap.Entry<String, PointOfInterest> entry : ParserPointOfInterest.Instance.pointOfInterests.entrySet())
        {
            try {
                double latitude = Double.parseDouble(entry.getValue().getLatitude());
                double longitude = Double.parseDouble(entry.getValue().getLongitude());
                if (latitude < 90 && latitude > 40 && longitude < -60 && longitude > -80)
                    markersInterest.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(entry.getValue().getNomAttrait())
                            .visible(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point_of_interest))));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
            catch (NullPointerException e)
            {
                System.out.println(e.toString());
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(screenCenter, mMap.getCameraPosition().zoom));
        progressBar.setVisibility(View.GONE);
    }

    private void setElectricalTerminalNodes()
    {
        for (int i = 1; i < ParserElectricalTerminal.Instance.electricalTerminals.size(); i++) {
            try {
                double latitude = Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude());
                double longitude = Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude());
                if (latitude < 90 && latitude > 40 && longitude < -60 && longitude > -80)
                        markersTerminal.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_electrical_terminal))));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
        }
    }

    public boolean isMarkerTerminalClose(int index)
    {
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target, markersTerminal.get(index).getPosition()) < 5000);
    }

    public boolean isMarkerInterestClose(int index)
    {
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target, markersInterest.get(index).getPosition()) < 5000);
    }

    @Override
    public void onParseElectricalTerminalComplete()
    {
        setElectricalTerminalNodes();
    }

    @Override
    public void onParsePointOfInterestComplete()
    {
        setPointOfInterestNodes();
    }
}
