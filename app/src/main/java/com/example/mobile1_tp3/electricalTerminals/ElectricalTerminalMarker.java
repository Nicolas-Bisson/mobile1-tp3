package com.example.mobile1_tp3.electricalTerminals;

import com.example.mobile1_tp3.MapsActivity;
import com.example.mobile1_tp3.database.ElectricalTerminalRepository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ElectricalTerminalMarker{

    List<Marker> markersTerminal;


    public void deleteAllTerminalMarker(List<Marker> markersTerminal) {
        for (int i = markersTerminal.size()-1; i >= 0; i--) {
            markersTerminal.get(i).remove();
        }
        markersTerminal.clear();
    }

//    public void CreateMarkerTerminal(LatLng markerPosition, String title, GoogleMap mMap) {
//        markersTerminal.add(mMap.addMarker(new MarkerOptions()
//                .position(markerPosition)
//                .icon(BitmapDescriptorFactory.fromResource(com.example.mobile1_tp3.R.mipmap.ic_electrical_terminal))
//                .title(title)));
//    }

    public void setElectricalTerminalNodes(List<ElectricalTerminal> electricalTerminals, MapsActivity activity)
    {
        for (int i = 0; i < electricalTerminals.size(); i++) {
            try {
                LatLng electricalTerminalPosition = new LatLng(electricalTerminals.get(i).getLatitude(),
                        electricalTerminals.get(i).getLongitude());

                activity.CreateMarkerTerminal(electricalTerminalPosition, electricalTerminals.get(i).getName());
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }
}
