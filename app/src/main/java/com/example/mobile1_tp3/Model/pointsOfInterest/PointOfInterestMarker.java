package com.example.mobile1_tp3.Model.pointsOfInterest;

import com.example.mobile1_tp3.database.PointOfInterestRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class PointOfInterestMarker {


    public void setPointInterestMarkerInvisible(List<Marker> markersInterest) {
        for (int i = 0; i < markersInterest.size(); i++) {
            markersInterest.get(i).setVisible(false);
        }
    }

    public void deleteAllInterestMarker(List<Marker> markersInterest) {
        for (int i = markersInterest.size()-1; i >= 0; i--) {
            markersInterest.get(i).remove();
        }
        markersInterest.clear();
    }

    public void setPointOfInterestNodes(GoogleMap mMap, List<Marker> markersInterest, PointOfInterestRepository pointOfInterestRepository)
    {
        LatLng currentPosition = new LatLng(mMap.getCameraPosition().target.latitude,
                mMap.getCameraPosition().target.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, mMap.getCameraPosition().zoom));
        deleteAllInterestMarker(markersInterest);
        List<PointOfInterest> pointOfInterests = pointOfInterestRepository.readByPosition(currentPosition);

        for (int i = 0; i < pointOfInterests.size(); i++) {
            try {
                LatLng pointInterestPosition = new LatLng(pointOfInterests.get(i).getLatitude(),
                        pointOfInterests.get(i).getLongitude());

                String title = pointOfInterests.get(i).getName();
                markersInterest.add(mMap.addMarker(new MarkerOptions()
                        .position(pointInterestPosition)
                        .icon(BitmapDescriptorFactory.fromResource(com.example.mobile1_tp3.R.mipmap.ic_point_of_interest))
                        .title(title)));
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }
}
