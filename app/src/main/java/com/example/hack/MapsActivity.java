package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.net.sip.SipAudioCall;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
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
    private ArrayList<Marker> tabMarker;

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

        tabMarker = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //searchText = (EditText) findViewById(R.id.searchText);
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
        for (int i = 1; i < ParserElectrical.Instance.electricalTerminals.size(); i++) {
            try {
                if (Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLatitude()) < 90 &&
                        Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLatitude()) > 40 &&
                        Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLongitude()) < -60 &&
                        Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLongitude()) > -80)
                tabMarker.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLatitude()), Double.parseDouble(ParserElectrical.Instance.electricalTerminals.get(i).getLongitude())))
                        .title(ParserElectrical.Instance.electricalTerminals.get(i).getNameElectricalTerminal())));
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

    public interface CheckboxListener {
        void onCheck();
    }

}
