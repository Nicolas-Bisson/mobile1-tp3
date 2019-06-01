package com.example.mobile1_tp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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

//BEN_REVIEW : Quand on a plusieurs "implements" comme ceci, je vous suggère de le formatter ainsi :

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                               AsyncParseElectricalTerminal.Listener,
                                                               AsyncParsePointOfInterest.Listener,
                                                               AsyncGetTerminalToShowFromRepository.Listener,
                                                               GoogleMap.OnMarkerClickListener {
//             Voici à quoi cela ressemblait avant :
//public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AsyncParseElectricalTerminal.Listener,
//        AsyncParsePointOfInterest.Listener, AsyncGetTerminalToShowFromRepository.Listener, GoogleMap.OnMarkerClickListener {

    //BEN_REVIEW : Avoir à le refaire, je placerais ces constantes dans un fichier de configuration.
    private static final int INITIAL_ZOOM = 12;
    private static final LatLng QUEBEC_POSITION = new LatLng(46.829853, -71.254028);
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private ElectricalTerminalRepository terminalRepository;
    private PointOfInterestRepository pointOfInterestRepository;
    private FavoriteTerminalRepository favoriteTerminalRepository;
    private FusedLocationProviderClient providerClient;
    //BEN_CORRECTION : Tout à coup, un tout autre standard de nommage...Copier-Coller de code ?
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
    //BEN_CORRECTION : Private manquant.
    ElectricalTerminalMarker electricalTerminalMarker;
    //BEN_CORRECTION : Private manquant.
    PointOfInterestMarker pointOfInterestMarker;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private View rootView;
    private ToggleButton favoriteButton;

    //BEN_REVIEW : Espace de trop entre "private" et "boolean".
    private  boolean permissionGranted;

    //BEN_CORRECTION : Private manquant.
    LatLng cameraPositionBeforeRot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        favoriteButton = findViewById(R.id.favoriteButton);
        favoriteButton.setEnabled(false);
        favoriteButton.setOnClickListener(this::onFavoriteButtonClick);

        listFavoriteTerminals = new ArrayList<>();
        listCurrentTerminals = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        rootView = findViewById(R.id.rootView);
        DbConnectionFactory connectionFactory = new DbConnectionFactory(this);

        terminalRepository = new ElectricalTerminalRepository(connectionFactory.getWritableDatabase());
        pointOfInterestRepository = new PointOfInterestRepository(connectionFactory.getWritableDatabase());
        favoriteTerminalRepository = new FavoriteTerminalRepository(connectionFactory.getWritableDatabase());

        //BEN_CORRECTION : S'il y a une erreur là, c'est nécessairement une erreur de programation.
        //                 Le try/catch a donc pas d'affaire là. De toute façon, vous ne faites
        //                 rien avec cette erreur. Logique fautive.
        try {
            //BEN_CORRECTION : Un "count" aurait été 3 000 000 de fois plus rapide.
            //                 De plus, cela aurait du être fait en tâche de fond.
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

        searchText = findViewById(R.id.searchText);

        isTerminalSelected = false;
        indexSelectedTerminal = 0;

        cameraPositionBeforeRot = new LatLng(0, 0);

        permissionGranted = false;

        providerClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //BEN_CORRECTION : Constantes.
        outState.putString("latBeforeRot", String.valueOf(getCurrentPosition().latitude));
        outState.putString("longBeforeRot", String.valueOf(getCurrentPosition().longitude));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //BEN_CORRECTION : Constantes.
        //BEN_CORRECTION : Warnings importants ignorés.
        //BEN_CORRECTION : Le zoom est pas conservé.
        //                 Voir "mMap.getCameraPosition().zoom".
        double latBeforeRot = Double.valueOf(savedInstanceState.getString("latBeforeRot"));
        double longBeforeRot = Double.valueOf(savedInstanceState.getString("longBeforeRot"));
        cameraPositionBeforeRot = new LatLng(latBeforeRot, longBeforeRot);
    }

    private void askForDeviceLocationPermission() {
        //BEN_CORRECTION : La seconde partie de cette condition (après le &&) est redondante.
        //                 Si la première est vrai, la second l'est aussi et vice versa.
        //                 Erreur de logique.
        if (!checkDeviceLocationPermission() && !permissionGranted) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Snackbar.make(rootView, getString(R.string.refused_location_permission_message), Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(rootView, getString(R.string.accepted_location_permission_message), Snackbar.LENGTH_LONG).show();
                    permissionGranted = true;
                } else {
                    permissionGranted = false;
                    //BEN_REVIEW : Hey! Vous venez de mentir à votre usager !!!
                    //             Voir la fonction "moveCameraToDevicePosition".
                    //
                    //             J'ai vraiment failli vous enlever des points pour ça.
                    Snackbar.make(rootView, getString(R.string.refused_location_permission_message), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void moveCameraToDevicePosition() {
        if (checkDeviceLocationPermission()) {

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
        } else
            moveCamera(QUEBEC_POSITION);
    }

    //BEN_REVIEW : Les méthodes répondant à des questions booléennes débutent généralement par "has"
    //             ou "is". Il est très rare de voir "check". À faire attention.
    private boolean checkDeviceLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return false;
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

    //BEN_CORRECTION : Pourquoi vous ne faites pas cela au "onCreate" ? J'ai vraiment l'impression
    //                 que vous avez fait cela pour éviter un "null ptr exception". Logique patchée.
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

    private void locateSearchedCity() {
        isTerminalSelected = false;
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        //BEN_CORRECTION : Ceci devrait être fait en tâche de fond. La doc dit que cette fonction
        //                 est bloquante, car elle fait un appel réseau pour obtenir le nom de la
        //                 ville.
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            //BEN_CORRECTION ; Erreur ignorée. La doc dit que "getFromLocationName" peut effectivement
            //                 lancer une exception si elle na pas accès à internet.
            e.printStackTrace();
        }

        //BEN_CORRECTION : Et si la ville demandée n'existe pas ? Actuellement, vous ne faites rien.
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

        //BEN_REVIEW : Aurait pu être demandé dès "onStart".
        askForDeviceLocationPermission();
        //Check if onMapReady call due to device rotation
        if (cameraPositionBeforeRot.latitude == 0 && cameraPositionBeforeRot.longitude == 0)
            moveCameraToDevicePosition();
        else
            moveCamera(cameraPositionBeforeRot);

        setListenerOnCitySearch();
        pointOfInterestMarker.setPointInterestMarkerInvisible(markersInterest);

        setMapListener();

        setInitialFavoriteMarker();
    }

    private void setInitialFavoriteMarker() {
        listFavoriteTerminals = favoriteTerminalRepository.readAll();
        for (int i = 0; i < listFavoriteTerminals.size(); i++) {
            addFavoriteMarkerToMap(listFavoriteTerminals.get(i).getName(),
                    new LatLng(listFavoriteTerminals.get(i).getLatitude(), listFavoriteTerminals.get(i).getLongitude()));
        }
        electricalTerminalMarker = new ElectricalTerminalMarker();
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
            public void onCameraIdle() {
                if (!isTerminalSelected) {
                    electricalTerminalMarker.deleteAllTerminalMarker(markersTerminal);
                    listCurrentTerminals.clear();

                    progressBar.setVisibility(View.VISIBLE);

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
        favoriteButton.setChecked(false);
        favoriteButton.setEnabled(false);
    }

    private void moveCamera(LatLng newPosition) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, INITIAL_ZOOM));
    }

    private LatLng getCurrentPosition() {
        return new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
    }

    @Override
    public void onParseElectricalTerminalComplete() {
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
        } else {
            terminalName = markersFavorite.get(indexSelectedTerminal).getTitle();
            removeFavoriteMarker(terminalName);
        }
    }

    private void createFavoriteMarker(String terminalName) {
        LatLng favoriteTerminalPosition = markersTerminal.get(indexSelectedTerminal).getPosition();
        ElectricalTerminal newFavoriteTerminal = new ElectricalTerminal(terminalName,
                favoriteTerminalPosition.latitude, favoriteTerminalPosition.longitude);

        favoriteTerminalRepository.create(newFavoriteTerminal);
        listFavoriteTerminals.add(newFavoriteTerminal);

        addFavoriteMarkerToMap(terminalName, favoriteTerminalPosition);

        markersTerminal.get(indexSelectedTerminal).remove();
        markersTerminal.remove(indexSelectedTerminal);
        terminalRepository.delete(listCurrentTerminals.get(indexSelectedTerminal).getId());
        listCurrentTerminals.remove(indexSelectedTerminal);

        onMarkerClick(markersFavorite.get(markersFavorite.size() - 1));
    }

    private void removeFavoriteMarker(String terminalName) {
        LatLng electricalTerminalPosition = markersFavorite.get(indexSelectedTerminal).getPosition();
        ElectricalTerminal newElectricalTerminal = new ElectricalTerminal(terminalName,
                electricalTerminalPosition.latitude, electricalTerminalPosition.longitude);

        favoriteTerminalRepository.create(newElectricalTerminal);
        listCurrentTerminals.add(newElectricalTerminal);

        CreateMarkerTerminal(electricalTerminalPosition, terminalName);

        markersFavorite.get(indexSelectedTerminal).remove();
        markersFavorite.remove(indexSelectedTerminal);
        favoriteTerminalRepository.delete(listFavoriteTerminals.get(indexSelectedTerminal).getId());
        listFavoriteTerminals.remove(indexSelectedTerminal);

        onMarkerClick(markersTerminal.get(markersTerminal.size() - 1));
    }

    private void addFavoriteMarkerToMap(String terminalName, LatLng favoriteTerminalPosition) {
        markersFavorite.add(mMap.addMarker(new MarkerOptions()
                .position(favoriteTerminalPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_favorite_terminal))
                .title(terminalName)));
    }

    public boolean onMarkerClick(final Marker marker) {

        if (isElectricalTerminalClicked(marker))
            return true;
        else
            return (isFavoriteTerminalClicked(marker));
    }

    private boolean isFavoriteTerminalClicked(Marker marker) {
        for (int i = 0; i < markersFavorite.size(); i++) {
            if (marker.equals(markersFavorite.get(i))) {
                return onTerminalClickedOperation(i, true);
            }
        }
        return false;
    }

    private boolean isElectricalTerminalClicked(Marker marker) {
        for (int i = 0; i < markersTerminal.size(); i++) {
            if (marker.equals(markersTerminal.get(i))) {
                return onTerminalClickedOperation(i, false);
            }
        }
        return false;
    }

    private boolean onTerminalClickedOperation(int index, boolean isFavorite) {
        favoriteButton.setEnabled(true);
        favoriteButton.setChecked(isFavorite);
        isTerminalSelected = true;
        indexSelectedTerminal = index;

        pointOfInterestMarker.setPointOfInterestNodes(mMap, markersInterest, pointOfInterestRepository);
        return true;
    }

    @Override
    public void onGetTerminalToShowFromRepositoryComplete(List<ElectricalTerminal> electricalTerminals) {
        listCurrentTerminals = electricalTerminals;
        electricalTerminalMarker.setElectricalTerminalNodes(electricalTerminals, this);

        progressBar.setVisibility(View.GONE);
    }

    public void CreateMarkerTerminal(LatLng markerPosition, String title) {
        markersTerminal.add(mMap.addMarker(new MarkerOptions()
                .position(markerPosition)
                .icon(BitmapDescriptorFactory.fromResource(com.example.mobile1_tp3.R.mipmap.ic_electrical_terminal))
                .title(title)));
    }

    //BEN_CORRECTION : Ça m'a pris un bon momment pour comprendre ce que cela faisait.
    //                 Cela recherche les terminaux les plus proches de la position actuelle n'est-ce pas ?
    //
    //                 Alors pourquoi est-ce que le nom de cette fonction ne le dit pas ?
    public void initiateSetTerminalNodes() {
        AsyncGetTerminalToShowFromRepository task = new AsyncGetTerminalToShowFromRepository(this, getCurrentPosition());
        task.execute(terminalRepository);
    }
}
