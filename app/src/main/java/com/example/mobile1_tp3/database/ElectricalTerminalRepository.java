package com.example.mobile1_tp3.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile1_tp3.Model.electricalTerminals.ElectricalTerminal;
import com.example.mobile1_tp3.Model.electricalTerminals.ElectricalTerminalTable;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ElectricalTerminalRepository implements MarkerRepository<ElectricalTerminal> {

    private final SQLiteDatabase database;

    public ElectricalTerminalRepository(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void create(ElectricalTerminal electricalTerminal) {

        if (electricalTerminal == null) throw new IllegalArgumentException();

        database.beginTransaction();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(ElectricalTerminalTable.INSERT, new String[]{
                    electricalTerminal.getName(),
                    String.valueOf(electricalTerminal.getLatitude()),
                    String.valueOf(electricalTerminal.getLongitude())
            });

            cursor.moveToNext(); //Write into database.
            cursor.close();

            cursor = database.rawQuery("SELECT last_insert_rowid()", new String[]{});
            cursor.moveToNext();

            electricalTerminal.setId(cursor.getLong(0));

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
    public ElectricalTerminal readById(long id) {
        ElectricalTerminal electricalTerminal = null;

        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.SELECT_BY_ID, new String[]{String.valueOf(id)})) {
            if (cursor.moveToNext()) {
                String name = cursor.getString(0);
                Double latitude = cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);

                electricalTerminal = new ElectricalTerminal(id, name, latitude, longitude);
            }
        } catch (Exception e) {
            throw new SQLException("Unable to read Terminal by name.", e);
        }

        return electricalTerminal;
    }

    @Override
    public List<ElectricalTerminal> readAll() {
        List<ElectricalTerminal> electricalTerminals = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.SELECT_ALL, new String[]{})) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String name = cursor.getString(1);
                Double latitude = cursor.getDouble(2);
                Double longitude = cursor.getDouble(3);

                electricalTerminals.add(new ElectricalTerminal(id, name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return electricalTerminals;
    }

    @Override
    public List<ElectricalTerminal> readByPosition(LatLng currentPosition) {
        List<ElectricalTerminal> electricalTerminals = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.SELECT_BY_POSITION, new String[]{
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

                electricalTerminals.add(new ElectricalTerminal(id, name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return electricalTerminals;
    }

    @Override
    public void update(ElectricalTerminal electricalTerminal) {
        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.UPDATE, new String[]{
                electricalTerminal.getName(),
                String.valueOf(electricalTerminal.getLatitude()),
                String.valueOf(electricalTerminal.getLongitude()),
                String.valueOf(electricalTerminal.getId())
        })) {
            cursor.moveToNext(); //Update database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.DELETE, new String[]{String.valueOf(id)})) {
            cursor.moveToNext(); //Delete from database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
