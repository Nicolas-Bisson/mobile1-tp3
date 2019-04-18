package com.example.mobile1_tp3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.mobile1_tp3.electricalTerminals.ElectricalTerminalTable;

public class DbConnectionFactory extends SQLiteOpenHelper {

    public static final String DB_NAME = "Database";
    public static final int DB_VERSION = 1;


    public DbConnectionFactory(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ElectricalTerminalTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ElectricalTerminalTable.DROP);
        onCreate(db);
    }
}
