package com.example.mobile1_tp3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ToggleButton;
import com.example.mobile1_tp3.database.DbConnectionFactory;
import com.example.mobile1_tp3.database.ElectricalTerminalRepository;
import com.example.mobile1_tp3.database.FavoriteTerminalRepository;
import com.example.mobile1_tp3.database.PointOfInterestRepository;
import com.example.mobile1_tp3.Model.electricalTerminals.AsyncGetTerminalToShowFromRepository;
import com.example.mobile1_tp3.Model.electricalTerminals.AsyncParseElectricalTerminal;
import com.example.mobile1_tp3.Model.electricalTerminals.ElectricalTerminal;
import com.example.mobile1_tp3.Model.electricalTerminals.ElectricalTerminalMarker;
import com.example.mobile1_tp3.Model.pointsOfInterest.AsyncParsePointOfInterest;
import com.example.mobile1_tp3.Model.pointsOfInterest.PointOfInterestMarker;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AsyncParseElectricalTerminal.Listener,
        AsyncParsePointOfInterest.Listener, AsyncGetTerminalToShowFromRepository.Listener, GoogleMap.OnMarkerClickListener {

    private static final int INITIAL_ZOOM = 12;
    private static final LatLng QUEBEC_POSITION = new LatLng(46.829853, -71.254028);
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private SQLiteDatabase database;
    private ElectricalTerminalRepository terminalRepository;
    private PointOfInterestRepository pointOfInterestRepository;
    private FavoriteTerminalRepository favoriteTerminalRepository;
    private FusedLocationProviderClient providerClient;
    private GoogleMap mMap;
    private boolean isTerminalSelected;
    private int indexSelectedTerminal;

    //widgets
    private EditText searchText;
    private List<ElectricalTerminal> listCurrentTerminals;
    private List<ElectricalTerminal> listFavoriteTerminals;
    private ArrayList<Marker> markersTerminal;
    private ArrayList<Marker> markersInterest;
    private ArrayList<Marker> markersFavorite;
    ElectricalTerminalMarker electricalTerminalMarker;
    PointOfInterestMarker pointOfInterestMarker;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private View rootView;
    private ToggleButton favoriteButton;

    private boolean isPermissionGranted = false;

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
        database = connectionFactory.getWritableDatabase();

        terminalRepository = new ElectricalTerminalRepository(database);
        pointOfInterestRepository = new PointOfInterestRepository(database);
        favoriteTerminalRepository = new FavoriteTerminalRepository(database);

        try {
            if (terminalRepository.readAll().size() == 0) {
                final InputStream FILE_ELECTRICAL_TERMINAL = this.getResources().openRawResource(R.raw.bornes);
                AsyncParseElectricalTerminal asyncParserElectricalTerminal = new AsyncParseElectricalTerminal(this, terminalRepository);
                asyncParserElectricalTerminal.execute(FILE_ELECTRICAL_TERMINAL);
            }
            if (pointOfInterestRepository.readAll().size() == 0) {
                final InputStream[] FILE_POINT_OF_INTEREST = new InputStream[]{this.getResources().openRawResource(R.raw.attraitsinfo), this.getResources().openRawResource(R.raw.attraitsadresse)};
                AsyncParsePointOfInterest asyncParsePointOfInterest = new AsyncParsePointOfInterest(this, pointOfInterestRepository);
                asyncParsePointOfInterest.execute(FILE_POINT_OF_INTEREST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        markersTerminal = new ArrayList<>();
        markersInterest = new ArrayList<>();
        markersFavorite = new ArrayList<>();
        electricalTerminalMarker = new ElectricalTerminalMarker();
        pointOfInterestMarker = new PointOfInterestMarker();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        searchText = (EditText) findViewById(R.id.searchText);
        isTerminalSelected = false;
        indexSelectedTerminal = 0;

        providerClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void askForDeviceLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Snackbar.make(rootView, getString(R.string.refused_Location_Permission_Message), Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
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
                    Snackbar.make(rootView, getString(R.string.refused_Location_Permission_Message), Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void moveCameraToDevicePosition() {

        //Permission verification
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                isPermissionGranted) {
            moveCamera(QUEBEC_POSITION);
            return;
        }

        providerClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location deviceLocation) {

                        if (deviceLocation != null) {
                            moveCamera(new LatLng(
                                    deviceLocation.getLatitude(),
                                    deviceLocation.getLongitude()));
                        } else {
                            moveCamera(QUEBEC_POSITION);
                        }
                    }
                });
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

    private void setListenerOnCitySearch() {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    locateSearchedCity();
                }
                return false;
            }
        });
    }

    private void locateSearchedCity()
    {
        isTerminalSelected = false;
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address addressSearched = list.get(0);
            moveCamera(new LatLng(addressSearched.getLatitude(), addressSearched.getLongitude()));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        askForDeviceLocationPermission();
        moveCameraToDevicePosition();

        setListenerOnCitySearch();
        pointOfInterestMarker.setPointInterestMarkerInvisible(markersInterest);

        setMapListener();
        progressBar.setVisibility(View.GONE);
    }

    private void setMapListener() {

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                disableMarkerSelection();

                pointOfInterestMarker.setPointInterestMarkerInvisible(markersInterest);
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle()
            {
                if(!isTerminalSelected)
                {
                    electricalTerminalMarker.deleteAllTerminalMarker(markersTerminal);
                    initiateSetTerminalNodes();
                    if (markersInterest.size() > 0) {
                        pointOfInterestMarker.deleteAllInterestMarker(markersInterest);
                    }
                }
            }
        });
    }

    private void disableMarkerSelection() {
        isTerminalSelected = false;
        favoriteButton.setEnabled(false);
        favoriteButton.setChecked(false);
    }

    private void moveCamera(LatLng newPosition){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, INITIAL_ZOOM));
    }

    private LatLng getCurrentPosition() {
        return new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
    }

    @Override
    public void onParseElectricalTerminalComplete()
    {
        initiateSetTerminalNodes();
    }

    @Override
    public void onParsePointOfInterestComplete() {
        pointOfInterestMarker.setPointOfInterestNodes(mMap, markersInterest, pointOfInterestRepository);
    }

    public void onFavoriteButtonClick(View view) {
        String terminalName;
        if (favoriteButton.isChecked()) {
            terminalName = markersTerminal.get(indexSelectedTerminal).getTitle();
            createFavoriteMarker(terminalName);
        }
        else {
            terminalName = markersFavorite.get(indexSelectedTerminal).getTitle();
            removeFavoriteMarker(terminalName);
        }
    }

    private void removeFavoriteMarker(String terminalName) {

        LatLng electricalTerminalPosition = markersFavorite.get(indexSelectedTerminal).getPosition();

        favoriteTerminalRepository.create(new ElectricalTerminal(terminalName, electricalTerminalPosition.latitude,
                electricalTerminalPosition.longitude));

        CreateMarkerTerminal(electricalTerminalPosition, terminalName);

        markersFavorite.get(indexSelectedTerminal).remove();
        favoriteTerminalRepository.delete(listFavoriteTerminals.get(indexSelectedTerminal).getId());
        listFavoriteTerminals.remove(indexSelectedTerminal);

        disableMarkerSelection();
    }

    private void createFavoriteMarker(String terminalName) {
        LatLng favoriteTerminalPosition = markersTerminal.get(indexSelectedTerminal).getPosition();

        favoriteTerminalRepository.create(new ElectricalTerminal(terminalName, favoriteTerminalPosition.latitude,
                favoriteTerminalPosition.longitude));

        addFavoriteMarkerToMap(terminalName, favoriteTerminalPosition);

        markersTerminal.get(indexSelectedTerminal).remove();
        terminalRepository.delete(listCurrentTerminals.get(indexSelectedTerminal).getId());
        listCurrentTerminals.remove(indexSelectedTerminal);

        disableMarkerSelection();
    }

    private void addFavoriteMarkerToMap(String terminalName, LatLng favoriteTerminalPosition) {
        markersFavorite.add(mMap.addMarker(new MarkerOptions()
                .position(favoriteTerminalPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_favorite_terminal))
                .title(terminalName)));
    }

    public boolean onMarkerClick(final Marker marker) {

        if (isElectricalTerminalClicked(marker)) return true;
        else if (isFavoriteTerminalClicked(marker)) return true;
        else return false;
    }

    private boolean isFavoriteTerminalClicked(Marker marker) {
        for (int i = 0; i < markersFavorite.size(); i++)
        {
            if (marker.equals(markersFavorite.get(i)))
            {
                favoriteButton.setEnabled(true);
                favoriteButton.setChecked(true);
                isTerminalSelected = true;
                indexSelectedTerminal = i;

                pointOfInterestMarker.setPointOfInterestNodes(mMap, markersInterest, pointOfInterestRepository);
                return true;
            }
        }
        return false;
    }

    private boolean isElectricalTerminalClicked(Marker marker) {
        for (int i = 0; i < markersTerminal.size(); i++)
        {
            if (marker.equals(markersTerminal.get(i)))
            {
                favoriteButton.setEnabled(true);
                favoriteButton.setChecked(false);
                isTerminalSelected = true;
                indexSelectedTerminal = i;

                pointOfInterestMarker.setPointOfInterestNodes(mMap, markersInterest, pointOfInterestRepository);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onGetTerminalToShowFromRepositoryComplete(List<ElectricalTerminal> electricalTerminals) {
        listCurrentTerminals = electricalTerminals;
        electricalTerminalMarker.setElectricalTerminalNodes(electricalTerminals, this);
        listFavoriteTerminals = favoriteTerminalRepository.readAll();
        for (int i = 0; i < listFavoriteTerminals.size(); i++) {
            addFavoriteMarkerToMap(listFavoriteTerminals.get(i).getName(),
                    new LatLng(listFavoriteTerminals.get(i).getLatitude(), listFavoriteTerminals.get(i).getLongitude()));
        }
    }

    public void CreateMarkerTerminal(LatLng markerPosition, String title) {
        markersTerminal.add(mMap.addMarker(new MarkerOptions()
                .position(markerPosition)
                .icon(BitmapDescriptorFactory.fromResource(com.example.mobile1_tp3.R.mipmap.ic_electrical_terminal))
                .title(title)));
    }

    public void initiateSetTerminalNodes() {
        AsyncGetTerminalToShowFromRepository task = new AsyncGetTerminalToShowFromRepository(this, getCurrentPosition());
        task.execute(terminalRepository);
    }
}
