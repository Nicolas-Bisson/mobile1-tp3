package com.example.mobile1_tp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AsyncParserElectricalTerminal.Listener,
        AsyncParserPointOfInterest.Listener, GoogleMap.OnMarkerClickListener {

    public static final int MAX_TERMINAL_RANGE = 15000;
    private static final int INITIAL_ZOOM = 12;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    public static final int MAX_INTEREST_RANGE = 5000;

    private FusedLocationProviderClient providerClient;
    private GoogleMap mMap;
    private boolean isTerminalSelected;
    private int indexTerminal;

    //widgets
    private EditText searchText;
    private ArrayList<Marker> markersTerminal;
    private ArrayList<Marker> markersInterest;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);



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
        isTerminalSelected = false;
        indexTerminal = 0;
    }

    private void moveCameraToDevicePosition(){

        providerClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            final Task location = providerClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        Location currentLocation = (Location) task.getResult();

//                        LatLng currentLatLng = new LatLng(
//                                currentLocation.getLatitude(),
//                                currentLocation.getLongitude());

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, INITIAL_ZOOM));
                    }
                    else {
                        //indiquer à l'utilisateur que la position n'a pas pu être trouvé
                    }
                }
            });

        }catch (SecurityException e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        isTerminalSelected = false;
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

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), INITIAL_ZOOM));
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, INITIAL_ZOOM));

        initSearch();
        for (int i = 0; i < markersInterest.size(); i++)
        {
            markersInterest.get(i).setVisible(false);
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isTerminalSelected = false;
                for (int i = 0; i < markersInterest.size(); i++)
                {
                    markersInterest.get(i).setVisible(false);
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle()
            {
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

                if(isTerminalSelected)
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
        for (TreeMap.Entry<String, PointOfInterest> entry : ParsePointOfInterest.Instance.pointOfInterests.entrySet())
        {
            try {
                double latitude = Double.parseDouble(entry.getValue().getLatitude());
                double longitude = Double.parseDouble(entry.getValue().getLongitude());
                if (isInQuebec(latitude, longitude))
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude), mMap.getCameraPosition().zoom));
        progressBar.setVisibility(View.GONE);
    }

    private void setElectricalTerminalNodes()
    {
        for (int i = 1; i < ParseElectricalTerminal.Instance.electricalTerminals.size(); i++) {
            try {
                double latitude = Double.parseDouble(ParseElectricalTerminal.Instance.electricalTerminals.get(i).getLatitude());
                double longitude = Double.parseDouble(ParseElectricalTerminal.Instance.electricalTerminals.get(i).getLongitude());
                if (isInQuebec(latitude, longitude))
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

    private boolean isInQuebec(double latitude, double longitude) {
        return latitude < 90 && latitude > 40 && longitude < -60 && longitude > -80;
    }

    public boolean isMarkerTerminalClose(int index)
    {
        return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target, markersTerminal.get(index).getPosition()) < MAX_TERMINAL_RANGE);
    }

    public boolean isMarkerInterestClose(int index, int indexTerminal)
    {
        if(indexTerminal <= -1)
        {
            return (SphericalUtil.computeDistanceBetween(mMap.getCameraPosition().target,
                    new LatLng(markersInterest.get(index).getPosition().latitude, markersInterest.get(index).getPosition().longitude)) < MAX_INTEREST_RANGE);
        }
        else
        {
            return (SphericalUtil.computeDistanceBetween(markersTerminal.get(indexTerminal).getPosition(),
                    new LatLng(markersInterest.get(index).getPosition().latitude, markersInterest.get(index).getPosition().longitude)) < MAX_INTEREST_RANGE);
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
}
