package com.example.mobile1_tp3.Model.pointsOfInterest;

public class PointOfInterestTable {

    public static final String CREATE = "" +
            "CREATE TABLE IF NOT EXISTS pointsOfInterest (\n" +
            "    id         INTEGER     PRIMARY KEY     AUTOINCREMENT,\n" +
            "    name       VARCHAR(30),\n" +
            "    latitude   REAL,\n" +
            "    longitude  REAL\n" +
            ");";

    public static final String INSERT = "" +
            "INSERT INTO pointsOfInterest (\n" +
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
            "FROM pointsOfInterest;";

    public static final String SELECT_BY_ID = "" +
            "SELECT name, latitude, longitude\n" +
            "FROM pointsOfInterest\n" +
            "WHERE id = ?;";

    public static final String SELECT_BY_POSITION = "" +
            "SELECT id, name, latitude, longitude\n" +
            "FROM pointsOfInterest\n" +
            "WHERE (latitude BETWEEN ? AND ?) AND (longitude BETWEEN ? AND ?);";

    public static final String UPDATE = "" +
            "UPDATE pointsOfInterest\n" +
            "SET \n" +
            "    name = ?,\n" +
            "    latitude = ?,\n" +
            "    longitude = ?\n" +
            "WHERE\n" +
            "    id = ?;";

    public static final String DELETE = "" +
            "DELETE FROM pointsOfInterest\n" +
            "WHERE id = ?;";

    public static final String DROP = "" +
            "DROP TABLE IF EXISTS pointsOfInterest";

    private PointOfInterestTable() {
        //Private for static class
    }
}
