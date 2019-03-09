package com.example.hack;

import androidx.fragment.app.FragmentActivity;

import android.net.sip.SipAudioCall;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextWatcher textWatcher;

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

        LatLng quebec = new LatLng(46.829853, -71.254028);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(quebec));
        mMap.setMinZoomPreference(8);
        for (int i = 1; i < ParserCSV.Instance.bornes.size(); i++) {
            try {
                Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude());
                if (SphericalUtil.computeDistanceBetween(new LatLng(mMap.getProjection().getVisibleRegion().nearLeft.latitude, mMap.getProjection().getVisibleRegion().nearLeft.longitude),
                        new LatLng(Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLongitude()))) > 10)
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLatitude()), Double.parseDouble(ParserCSV.Instance.bornes.get(i).getLongitude())))
                        .title(ParserCSV.Instance.bornes.get(i).getNomBorne()));
            }
            catch (NumberFormatException ex)
            {
            }
            }
    }

    public interface CheckboxListener {
        void onCheck();
    }

}
