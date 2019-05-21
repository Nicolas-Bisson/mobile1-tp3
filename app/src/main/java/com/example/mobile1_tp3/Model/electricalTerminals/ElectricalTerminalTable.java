package com.example.mobile1_tp3.Model.electricalTerminals;

public class ElectricalTerminalTable {

    public static final String CREATE = "" +
            "CREATE TABLE IF NOT EXISTS electricalTerminals (\n" +
            "    id         INTEGER     PRIMARY KEY     AUTOINCREMENT,\n" +
            "    name       VARCHAR(10),\n" +
            "    latitude   REAL,\n" +
            "    longitude  REAL,\n" +
            "    UNIQUE(latitude, longitude)\n" +
            ");";

    public static final String INSERT = "" +
            "INSERT INTO electricalTerminals (\n" +
            "    name,\n" +
            "    latitude,\n" +
            "    longitude\n" +
            ") VALUES (\n" +
            "    ?,\n" +
            "    ?,\n" +
            "    ?\n" +
            ");";

    public static final String SELECT_ALL = "" +
            "SELECT id, name, latitude, longitude\n" +
            "FROM electricalTerminals;";

    public static final String SELECT_BY_ID = "" +
            "SELECT name, latitude, longitude\n" +
            "FROM electricalTerminals\n" +
            "WHERE id = ?;";

    public static final String SELECT_BY_POSITION = "" +
            "SELECT id, name, latitude, longitude\n" +
            "FROM electricalTerminals\n" +
            "WHERE (latitude BETWEEN ? AND ?) AND (longitude BETWEEN ? AND ?);";

    public static final String UPDATE = "" +
            "UPDATE electricalTerminals\n" +
            "SET \n" +
            "    name = ?,\n" +
            "    latitude = ?,\n" +
            "    longitude = ?\n" +
            "WHERE\n" +
            "    id = ?;";

    public static final String DELETE = "" +
            "DELETE FROM electricalTerminals\n" +
            "WHERE id = ?;";

    public static final String DROP = "" +
            "DROP TABLE IF EXISTS electricalTerminals";

    private ElectricalTerminalTable() {
        //Private for static class
    }
}
