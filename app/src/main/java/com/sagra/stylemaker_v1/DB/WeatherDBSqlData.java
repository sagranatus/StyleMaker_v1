package com.sagra.stylemaker_v1.DB;

public class WeatherDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE db_dailyweather "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"date TEXT NOT NULL unique, "
            +"max TEXT NOT NULL, "
            +"min TEXT NOT NULL, "
            +"code TEXT NOT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO db_dailyweather "
            +"(date, max, min, code)"
            +"VALUES(?, ?, ?, ?)";
    public static final String SQL_DB_SELECT_ALL = "SELECT * " + "FROM db_dailyweather ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_DATE = "SELECT * " + "FROM db_dailyweather"+" WHERE date=? ORDER BY reg_id DESC";
    public static final String SQL_DB_UPDATE_DATE = "UPDATE db_dailyweather " + "SET max=?, min=?, code=?"+" WHERE date=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM db_dailyweather "+"WHERE date=?";
}
