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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncParserElectricalTerminal.Listener,
        AsyncParserPointOfInterest.Listener, GoogleMap.OnMarkerClickListener {

    private static final int initialZoom = 12;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    private GoogleMap mMap;

    private int distanceCheckTerminal;
    private int distanceCheckInterest;
    private boolean isTerminalSelected;
    private int indexTerminal;

    //widgets
    private EditText searchText;
    private ArrayList<Marker> markersTerminal;
    private ArrayList<Marker> markersInterest;
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

        searchText = (EditText) findViewById(R.id.searchText);

        distanceCheckTerminal = 5000;
        distanceCheckInterest = 5000;
        isTerminalSelected = false;
        indexTerminal = 0;
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

        mMap.setOnMarkerClickListener(this);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(!isTerminalSelected)
                {
                    distanceCheckInterest = 5000;
                    for (int i = 0; i < markersInterest.size(); i++)
                    {
                        markersInterest.get(i).setVisible(false);
                    }

                    for (int i = 0; i < markersTerminal.size(); i++)
                    {
                        if (!isMarkerTerminalClose(i))
                        {
                            markersTerminal.get(i).setVisible(false);
                        }
                        else
                        {
                            markersTerminal.get(i).setVisible(true);
                        }

                    }
                }
                else
                {
                    for (int i = 0; i < markersTerminal.size(); i++)
                    {
                        if(i == indexTerminal)
                        {
                            for (int j = 0; j < markersInterest.size(); j++)
                            {
                                if(isMarkerInterestClose(j, indexTerminal))
                                {
                                    markersInterest.get(j).setVisible(true);
                                }
                            }
                        }
                    }

                }
            }
        });
    }

    public boolean onMarkerClick(final Marker marker)
    {

        for (int i = 0; i < markersTerminal.size(); i++)
        {
            if (marker.equals(markersTerminal.get(i)))
            {
                isTerminalSelected = true;
                indexTerminal = i;
                distanceCheckInterest = 2000;

                for (int j = 0; j < markersInterest.size(); j++)
                {
                    if (isMarkerInterestClose(j, indexTerminal))
                    {
                        markersInterest.get(j).setVisible(true);
                    }
                    else
                    {
                        markersInterest.get(j).setVisible(false);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void setPointOfInterestNodes()
    {
        for (TreeMap.Entry<String, PointOfInterest> entry : ParserPointOfInterest.Instance.pointOfInterests.entrySet())
        {
            try {
                if (Double.parseDouble(entry.getValue().getLatitude()) < 90 &&
                        Double.parseDouble(entry.getValue().getLatitude()) > 40 &&
                        Double.parseDouble(entry.getValue().getLongitude()) < -60 &&
                        Double.parseDouble(entry.getValue().getLongitude()) > -80)
                    markersInterest.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(entry.getValue().getLatitude()), Double.parseDouble(entry.getValue().getLongitude())))
                            .title(entry.getValue().getNomAttrait())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point_of_interest))
                            ));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude), mMap.getCameraPosition().zoom));
        progressBar.setVisibility(View.GONE);
    }

    private void setElectricalTerminalNodes()
    {
        for (int i = 1; i < ParserElectricalTerminal.Instance.electricalTerminals.size(); i++) {
            try {
                if (Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude()) < 90 &&
                        Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude()) > 40 &&
                        Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude()) < -60 &&
                        Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude()) > -80)
                        markersTerminal.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude())))
                        .title(ParserElectricalTerminal.Instance.electricalTerminals.get(i).getNameElectricalTerminal())
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
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target,
                new LatLng(markersTerminal.get(index).getPosition().latitude, markersTerminal.get(index).getPosition().longitude)) < distanceCheckTerminal);
    }

    public boolean isMarkerInterestClose(int index, int indexTerminal)
    {
        if(indexTerminal <= -1)
        {
            return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target,
                    new LatLng(markersInterest.get(index).getPosition().latitude, markersInterest.get(index).getPosition().longitude)) < distanceCheckInterest);
        }
        else
        {
            return (SphericalUtil.computeDistanceBetween(markersTerminal.get(indexTerminal).getPosition(),
                    new LatLng(markersInterest.get(index).getPosition().latitude, markersInterest.get(index).getPosition().longitude)) < distanceCheckInterest);
        }
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

    private boolean isTerminalSelected()
    {
        return isTerminalSelected;
    }
}
