package com.example.mobile1_tp3.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface MarkerRepository<T> {

    double detectionRange = 0.05;

    void create(T item);

    T readByName(String name);

    List<T> readAll();

    List<T> readByPosition(LatLng currentPosition);

    void update(T item);

    void delete(String name);

}
