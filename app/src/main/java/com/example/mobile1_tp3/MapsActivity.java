package com.example.mobile1_tp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.mobile1_tp3.database.DbConnectionFactory;
import com.example.mobile1_tp3.database.ElectricalTerminalRepository;
import com.example.mobile1_tp3.database.FavoriteTerminalRepository;
import com.example.mobile1_tp3.database.PointOfInterestRepository;
import com.example.mobile1_tp3.electricalTerminals.AsyncParseElectricalTerminal;
import com.example.mobile1_tp3.electricalTerminals.ElectricalTerminal;
import com.example.mobile1_tp3.pointsOfInterest.AsyncParsePointOfInterest;
import com.example.mobile1_tp3.pointsOfInterest.PointOfInterest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AsyncParseElectricalTerminal.Listener,
        AsyncParsePointOfInterest.Listener, GoogleMap.OnMarkerClickListener {

    private static final int INITIAL_ZOOM = 12;
    private static final LatLng QUEBEC = new LatLng(46.829853, -71.254028);
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private SQLiteDatabase Database;
    private ElectricalTerminalRepository terminalRepository;
    private PointOfInterestRepository pointOfInterestRepository;
    private FavoriteTerminalRepository favoriteTerminalRepository;
    private FusedLocationProviderClient providerClient;
    private GoogleMap mMap;
    private boolean isTerminalSelected;
    private int indexSelectedTerminal;

    //widgets
    private EditText searchText;
    private ArrayList<Marker> markersTerminal;
    private ArrayList<Marker> markersInterest;
    private ArrayList<Marker> markersFavorite;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private View rootView;
    private ToggleButton favoriteButton;

    private boolean isPermissionGranted = false;


    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        favoriteButton = findViewById(R.id.favoriteButton);
        favoriteButton.setEnabled(false);
        favoriteButton.setOnClickListener(this::onFavoriteButtonClick);

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        rootView = findViewById(R.id.rootView);
        DbConnectionFactory connectionFactory = new DbConnectionFactory(this);
        Database = connectionFactory.getWritableDatabase();

        terminalRepository = new ElectricalTerminalRepository(Database);
        pointOfInterestRepository = new PointOfInterestRepository(Database);
        favoriteTerminalRepository = new FavoriteTerminalRepository(Database);

        try {
            final InputStream FILE_ELECTRICAL_TERMINAL = this.getResources().openRawResource(R.raw.bornes);
            AsyncParseElectricalTerminal asyncParserElectricalTerminal = new AsyncParseElectricalTerminal(this, terminalRepository);
            asyncParserElectricalTerminal.execute(FILE_ELECTRICAL_TERMINAL);
            final InputStream[] FILE_POINT_OF_INTEREST = new InputStream[]{this.getResources().openRawResource(R.raw.attraitsinfo), this.getResources().openRawResource(R.raw.attraitsadresse)};
            AsyncParsePointOfInterest asyncParsePointOfInterest = new AsyncParsePointOfInterest(this, pointOfInterestRepository);
            asyncParsePointOfInterest.execute(FILE_POINT_OF_INTEREST);
        } catch (Exception e) {
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
        indexSelectedTerminal = 0;

        providerClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            location.getLatitude(),
                            location.getLongitude()), INITIAL_ZOOM));
                }
            }
        };
    }

    private void askForDeviceLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(rootView, getString(R.string.refused_Location_Permission_Message), Snackbar.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
        Snackbar.make(rootView, getString(R.string.Accepted_Location_Permission_Message), Snackbar.LENGTH_LONG).show();
        isPermissionGranted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        isPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isPermissionGranted = true;
                } else {
                    //Permission refusée. Expliquer à l'utilisateur pourquoi c'est important.
                }
                return;
            }
        }
    }


    private void moveCameraToDevicePosition() {

        if (isPermissionGranted) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, INITIAL_ZOOM));
                return;
            }

        providerClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()), INITIAL_ZOOM));
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC, INITIAL_ZOOM));
                        }
                    }
                });
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

    //Set the camera on the searched city
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

        askForDeviceLocationPermission();

        moveCameraToDevicePosition();

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
                favoriteButton.setEnabled(false);
                favoriteButton.setChecked(false);
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
                if(!isTerminalSelected)
                {
                    setElectricalTerminalNodes();
                    if (markersInterest.size() > 0) {
                        deleteAllInterestMarker();
                    }
                }
            }
        });
    }

    private void deleteAllInterestMarker() {
        for (int i = markersInterest.size()-1; i >= 0; i--) {
            markersInterest.get(i).remove();
        }
    }

    public boolean onMarkerClick(final Marker marker)
    {

        for (int i = 0; i < markersTerminal.size(); i++)
        {
            if (marker.equals(markersTerminal.get(i)))
            {
                favoriteButton.setEnabled(true);
                isTerminalSelected = true;
                indexSelectedTerminal = i;

                setPointOfInterestNodes();
                return true;
            }
        }
        return false;
    }

    private void setPointOfInterestNodes()
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentPosition(), mMap.getCameraPosition().zoom));
        deleteAllInterestMarker();
        List<PointOfInterest> pointOfInterests = pointOfInterestRepository.readByPosition(getCurrentPosition());
        for (int i = 0; i < pointOfInterests.size(); i++) {
            try {
                Double latitude = pointOfInterests.get(i).getLatitude();
                Double longitude = pointOfInterests.get(i).getLongitude();
                String title = pointOfInterests.get(i).getName();
                markersInterest.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point_of_interest))
                        .title(title)));
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.toString());
            }
        }
    }

    private LatLng getCurrentPosition() {
        return new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
    }

    private void setElectricalTerminalNodes()
    {
        for (int i = markersTerminal.size()-1; i >= 0; i--) {
            markersTerminal.get(i).remove();
        }
        List<ElectricalTerminal> electricalTerminals = terminalRepository.readByPosition(getCurrentPosition());
        for (int i = 0; i < electricalTerminals.size(); i++) {
            try {
                Double latitude = electricalTerminals.get(i).getLatitude();
                Double longitude = electricalTerminals.get(i).getLongitude();
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

    public void onFavoriteButtonClick(View view) {
        if (favoriteButton.isChecked()) {
            Double latitude = markersTerminal.get(indexSelectedTerminal).getPosition().latitude;
            Double longitude = markersTerminal.get(indexSelectedTerminal).getPosition().longitude;
            favoriteTerminalRepository.create(new ElectricalTerminal("Favorite", latitude, longitude));
            markersFavorite.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point_of_interest))));
        }
    }
}
