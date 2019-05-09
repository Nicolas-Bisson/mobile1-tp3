package com.example.mobile1_tp3.electricalTerminals;

public class FavoriteTerminalTable {
    public static final String CREATE = "" +
            "CREATE TABLE IF NOT EXISTS favoriteTerminals (\n" +
            "    id         INTEGER     PRIMARY KEY     AUTOINCREMENT,\n" +
            "    name       VARCHAR(10) UNIQUE,\n" +
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
            "SELECT name, latitude, longitude\n" +
            "FROM favoriteTerminals;";

    public static final String SELECT_BY_NAME = "" +
            "SELECT latitude, longitude\n" +
            "FROM favoriteTerminals\n" +
            "WHERE name = ?;";

    public static final String SELECT_BY_POSITION = "" +
            "SELECT name, latitude, longitude\n" +
            "FROM favoriteTerminals\n" +
            "WHERE (latitude BETWEEN ? AND ?) AND (longitude BETWEEN ? AND ?);";

    public static final  String UPDATE = "" +
            "UPDATE favoriteTerminals\n" +
            "SET \n" +
            "    name = ?,\n" +
            "    latitude = ?,\n" +
            "    longitude = ?\n" +
            "WHERE\n" +
            "    name = ?;";

    public static final String DELETE = "" +
            "DELETE FROM favoriteTerminals\n" +
            "WHERE name = ?;";

    public static final String DROP = "" +
            "DROP TABLE IF EXISTS favoriteTerminals";

    private FavoriteTerminalTable() {
        //Private for static class
    }
}
