package com.example.mobile1_tp3.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile1_tp3.electricalTerminals.ElectricalTerminal;
import com.example.mobile1_tp3.electricalTerminals.FavoriteTerminalTable;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FavoriteTerminalRepository implements MarkerRepository<ElectricalTerminal> {
    private final SQLiteDatabase database;

    public FavoriteTerminalRepository(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void create(ElectricalTerminal electricalTerminal) {

        if (electricalTerminal == null) throw new IllegalArgumentException();

        database.beginTransaction();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(FavoriteTerminalTable.INSERT, new String[]{
                    electricalTerminal.getName(),
                    String.valueOf(electricalTerminal.getLatitude()),
                    String.valueOf(electricalTerminal.getLongitude())
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
    public ElectricalTerminal readByName(String name) {
        ElectricalTerminal electricalTerminal = null;

        try(Cursor cursor = database.rawQuery(FavoriteTerminalTable.SELECT_BY_NAME, new String[]{name})) {
            if (cursor.moveToNext()) {
                Double latitude = cursor.getDouble(0);
                Double longitude = cursor.getDouble(1);

                electricalTerminal = new ElectricalTerminal(name, latitude, longitude);
            }
        } catch (Exception e) {
            throw new SQLException("Unable to read Favorite Terminal by name.", e);
        }

        return electricalTerminal;
    }

    @Override
    public List<ElectricalTerminal> readAll() {
        List<ElectricalTerminal> electricalTerminals = new ArrayList<>();

        try(Cursor cursor = database.rawQuery(FavoriteTerminalTable.SELECT_ALL, new String[]{})) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                Double latitude = cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);

                electricalTerminals.add(new ElectricalTerminal(name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return electricalTerminals;
    }

    @Override
    public List<ElectricalTerminal> readByPosition(LatLng currentPosition) {
        List<ElectricalTerminal> electricalTerminals = new ArrayList<>();

        try(Cursor cursor = database.rawQuery(FavoriteTerminalTable.SELECT_BY_POSITION, new String[]{
                String.valueOf(currentPosition.latitude - DETECTION_RANGE),
                String.valueOf(currentPosition.latitude + DETECTION_RANGE),
                String.valueOf(currentPosition.longitude - DETECTION_RANGE),
                String.valueOf(currentPosition.longitude + DETECTION_RANGE)
        })) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                Double latitude = cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);

                electricalTerminals.add(new ElectricalTerminal(name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return electricalTerminals;
    }

    @Override
    public void update(ElectricalTerminal electricalTerminal) {
        try (Cursor cursor = database.rawQuery(FavoriteTerminalTable.UPDATE, new String[]{
                String.valueOf(electricalTerminal.getLatitude()),
                String.valueOf(electricalTerminal.getLongitude()),
                electricalTerminal.getName()
        })) {
            cursor.moveToNext(); //Update database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        try (Cursor cursor = database.rawQuery(FavoriteTerminalTable.DELETE, new String[]{name})) {
            cursor.moveToNext(); //Delete from database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
