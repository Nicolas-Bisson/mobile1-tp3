package com.example.mobile1_tp3.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile1_tp3.Model.pointsOfInterest.PointOfInterest;
import com.example.mobile1_tp3.Model.pointsOfInterest.PointOfInterestTable;
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

            cursor = database.rawQuery("SELECT last_insert_rowid()", new String[]{});
            cursor.moveToNext();

            pointOfInterest.setId(cursor.getLong(0));

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
    public PointOfInterest readById(long id) {
        PointOfInterest pointOfInterest = null;

        try (Cursor cursor = database.rawQuery(PointOfInterestTable.SELECT_BY_ID, new String[]{String.valueOf(id)})) {
            if (cursor.moveToNext()) {
                String name = cursor.getString(0);
                Double latitude = cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);

                pointOfInterest = new PointOfInterest(id, name, latitude, longitude);
            }
        } catch (Exception e) {
            throw new SQLException("Unable to read Point of Interest by name.", e);
        }

        return pointOfInterest;
    }

    @Override
    public List<PointOfInterest> readAll() {
        List<PointOfInterest> pointOfInterests = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(PointOfInterestTable.SELECT_ALL, new String[]{})) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String name = cursor.getString(1);
                Double latitude = cursor.getDouble(2);
                Double longitude = cursor.getDouble(3);

                pointOfInterests.add(new PointOfInterest(id, name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pointOfInterests;
    }

    @Override
    public List<PointOfInterest> readByPosition(LatLng currentPosition) {
        List<PointOfInterest> pointOfInterests = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(PointOfInterestTable.SELECT_BY_POSITION, new String[]{
                String.valueOf(currentPosition.latitude - DETECTION_RANGE),
                String.valueOf(currentPosition.latitude + DETECTION_RANGE),
                String.valueOf(currentPosition.longitude - DETECTION_RANGE),
                String.valueOf(currentPosition.longitude + DETECTION_RANGE)
        })) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String name = cursor.getString(1);
                Double latitude = cursor.getDouble(2);
                Double longitude = cursor.getDouble(3);

                pointOfInterests.add(new PointOfInterest(id, name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pointOfInterests;
    }

    @Override
    public void update(PointOfInterest pointOfInterest) {
        try (Cursor cursor = database.rawQuery(PointOfInterestTable.UPDATE, new String[]{
                pointOfInterest.getName(),
                String.valueOf(pointOfInterest.getLatitude()),
                String.valueOf(pointOfInterest.getLongitude()),
                String.valueOf(pointOfInterest.getId())
        })) {
            cursor.moveToNext(); //Update database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long id) {
        try (Cursor cursor = database.rawQuery(PointOfInterestTable.DELETE, new String[]{String.valueOf(id)})) {
            cursor.moveToNext(); //Delete from database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
