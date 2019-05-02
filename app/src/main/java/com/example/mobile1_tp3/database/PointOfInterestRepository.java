package com.example.mobile1_tp3.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile1_tp3.pointsOfInterest.PointOfInterest;
import com.example.mobile1_tp3.pointsOfInterest.PointOfInterestTable;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PointOfInterestRepository implements MarkerRepository<PointOfInterest> {

    private final SQLiteDatabase database;

    public PointOfInterestRepository(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void create(PointOfInterest pointOfInterest) {

        if (pointOfInterest == null) throw new IllegalArgumentException();

        database.beginTransaction();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(PointOfInterestTable.INSERT, new String[]{
                    pointOfInterest.getName(),
                    String.valueOf(pointOfInterest.getLatitude()),
                    String.valueOf(pointOfInterest.getLongitude())
            });

            cursor.moveToNext(); //Write into database.
            cursor.close();

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        database.endTransaction();

    }

    @Override
    public PointOfInterest readByName(String name) {
        PointOfInterest pointOfInterest = null;

        try(Cursor cursor = database.rawQuery(PointOfInterestTable.SELECT_BY_NAME, new String[]{name})) {
            if (cursor.moveToNext()) {
                Double latitude = cursor.getDouble(0);
                Double longitude = cursor.getDouble(1);

                pointOfInterest = new PointOfInterest(name, latitude, longitude);
            }
        } catch (Exception e) {
            throw new SQLException("Unable to read Country by id.", e);
        }

        return pointOfInterest;
    }

    @Override
    public List<PointOfInterest> readAll() {
        List<PointOfInterest> pointOfInterests = new ArrayList<>();

        try(Cursor cursor = database.rawQuery(PointOfInterestTable.SELECT_ALL, new String[]{})) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                Double latitude = cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);

                pointOfInterests.add(new PointOfInterest(name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pointOfInterests;
    }

    @Override
    public List<PointOfInterest> readByPosition(LatLng currentPosition) {
        List<PointOfInterest> pointOfInterests = new ArrayList<>();

        try(Cursor cursor = database.rawQuery(PointOfInterestTable.SELECT_BY_POSITION, new String[]{
                String.valueOf(currentPosition.latitude - detectionRange),
                String.valueOf(currentPosition.latitude + detectionRange),
                String.valueOf(currentPosition.longitude - detectionRange),
                String.valueOf(currentPosition.longitude + detectionRange)
        })) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                Double latitude = cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);

                pointOfInterests.add(new PointOfInterest(name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pointOfInterests;
    }

    @Override
    public void update(PointOfInterest pointOfInterest) {
        try (Cursor cursor = database.rawQuery(PointOfInterestTable.UPDATE, new String[]{
                String.valueOf(pointOfInterest.getLatitude()),
                String.valueOf(pointOfInterest.getLongitude()),
                pointOfInterest.getName()
        })) {
            cursor.moveToNext(); //Update database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        try (Cursor cursor = database.rawQuery(PointOfInterestTable.DELETE, new String[]{name})) {
            cursor.moveToNext(); //Delete from database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
