package com.example.mobile1_tp3.Model.electricalTerminals;

import com.example.mobile1_tp3.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class ElectricalTerminalMarker{

    public void deleteAllTerminalMarker(List<Marker> markersTerminal) {
        for (int i = markersTerminal.size()-1; i >= 0; i--) {
            markersTerminal.get(i).remove();
        }
        markersTerminal.clear();
    }

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
