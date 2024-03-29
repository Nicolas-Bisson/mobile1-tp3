package com.example.mobile1_tp3.Model.electricalTerminals;

public class FavoriteTerminalTable {

    public static final String CREATE = "" +
            "CREATE TABLE IF NOT EXISTS favoriteTerminals (\n" +
            "    id         INTEGER     PRIMARY KEY     AUTOINCREMENT,\n" +
            "    name       VARCHAR(10),\n" +
            "    latitude   REAL,\n" +
            "    longitude  REAL\n" +
            ");";

    public static final String INSERT = "" +
            "INSERT INTO favoriteTerminals (\n" +
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
            "FROM favoriteTerminals;";

    public static final String SELECT_BY_ID = "" +
            "SELECT name, latitude, longitude\n" +
            "FROM favoriteTerminals\n" +
            "WHERE id = ?;";

    public static final String SELECT_BY_POSITION = "" +
            "SELECT id, name, latitude, longitude\n" +
            "FROM favoriteTerminals\n" +
            "WHERE (latitude BETWEEN ? AND ?) AND (longitude BETWEEN ? AND ?);";

    public static final String UPDATE = "" +
            "UPDATE favoriteTerminals\n" +
            "SET \n" +
            "    name = ?,\n" +
            "    latitude = ?,\n" +
            "    longitude = ?\n" +
            "WHERE\n" +
            "    id = ?;";

    public static final String DELETE = "" +
            "DELETE FROM favoriteTerminals\n" +
            "WHERE name = ?;";

    public static final String DROP = "" +
            "DROP TABLE IF EXISTS favoriteTerminals";

    private FavoriteTerminalTable() {
        //Private for static class
    }
}
