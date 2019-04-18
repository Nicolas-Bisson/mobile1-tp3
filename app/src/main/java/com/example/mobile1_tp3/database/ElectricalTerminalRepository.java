package com.example.mobile1_tp3.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile1_tp3.electricalTerminals.ElectricalTerminal;
import com.example.mobile1_tp3.electricalTerminals.ElectricalTerminalTable;

import java.util.ArrayList;
import java.util.List;

public class ElectricalTerminalRepository implements Repository<ElectricalTerminal> {

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
    public ElectricalTerminal readById(Long id) {
        ElectricalTerminal electricalTerminal = null;

        try(Cursor cursor = database.rawQuery(ElectricalTerminalTable.SELECT_BY_ID, new String[]{String.valueOf(id)})) {
            if (cursor.moveToNext()) {
                String name = cursor.getString(1);
                float latitude = cursor.getFloat(2);
                float longitude = cursor.getFloat(3);

                electricalTerminal = new ElectricalTerminal(name, latitude, longitude);
            }
        } catch (Exception e) {
            throw new SQLException("Unable to read Country by id.", e);
        }

        return electricalTerminal;
    }

    @Override
    public List<ElectricalTerminal> readAll() {
        List<ElectricalTerminal> electricalTerminals = new ArrayList<>();

        try(Cursor cursor = database.rawQuery(ElectricalTerminalTable.SELECT_ALL, new String[]{})) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String name = cursor.getString(1);
                float latitude = cursor.getFloat(2);
                float longitude = cursor.getFloat(3);

                electricalTerminals.add(new ElectricalTerminal(name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return electricalTerminals;
    }

    @Override
    public void update(ElectricalTerminal country) {
        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.UPDATE, new String[]{
                String.valueOf(country.getName()),
                String.valueOf(country.getLatitude()),
                String.valueOf(country.getLongitude()),
                String.valueOf(country.getId())})) {
            cursor.moveToNext(); //Update database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ElectricalTerminal country) {
        try (Cursor cursor = database.rawQuery(ElectricalTerminalTable.DELETE, new String[]{String.valueOf(country.getId())})) {
            cursor.moveToNext(); //Delete from database.

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
