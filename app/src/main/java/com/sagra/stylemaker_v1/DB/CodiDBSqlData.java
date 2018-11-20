package com.sagra.stylemaker_v1.DB;

public class CodiDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE db_codi "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"uid TEXT NOT NULL, "
            +"number TEXT NOT NULL, "
            +"season TEXT NOT NULL, "
            +"type TEXT NOT NULL, "
            +"top TEXT NOT NULL, "
            +"bottom TEXT NOT NULL, "
            +"shoes TEXT NOT NULL, "
            +"outdoor TEXT NOT NULL, "
            +"bag TEXT NOT NULL, "
            +"acc TEXT NOT NULL,"
            + "tag TEXT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO db_codi "
            +"(uid, number, season, type, top, bottom, shoes, outdoor, bag, acc, tag)"
            +"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE db_codi " + "SET season=?, type=?, top=?, bottom=?, shoes=?, outdoor=?, bag=?, acc=?, tag=?"+" WHERE uid=? AND number=?";
    public static final String SQL_DB_UPDATE_DATA_TAG = "UPDATE db_codi " + "SET tag=?"+" WHERE uid=? AND number=?";
    public static final String SQL_DB_SELECT_ALL = "SELECT * " + "FROM db_codi ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_CODI = "SELECT * " + "FROM db_codi"+ " WHERE uid=? AND number= ?";
    public static final String SQL_DB_SELECT_SEASON = "SELECT * " + "FROM db_codi"+" WHERE uid=? AND season=? ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_SEASON_TYPE = "SELECT * " + "FROM db_codi"+" WHERE uid=? AND season= ? AND type= ? ORDER BY reg_id DESC";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM db_codi "+"WHERE uid=? AND number=?";
}
