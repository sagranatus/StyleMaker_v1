package com.sagra.stylemaker_v1.DB;

public class ClothDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE db_cloth "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"uid TEXT NOT NULL, "
            +"season TEXT NOT NULL, "
            +"clothtype TEXT NOT NULL, "
            +"info TEXT NOT NULL, "
            +"detail1 TEXT NULL, "
            +"detail2 TEXT NULL)"; // detail2는 태그
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO db_cloth "
            +"(uid, season, clothtype, info, detail1, detail2)"
            +"VALUES(?, ?, ?, ?, ?, ?)";
    public static final String SQL_DB_SELECT_ALL = "SELECT * " + "FROM db_cloth ORDER BY reg_id DESC WHERE uid=?";
    public static final String SQL_DB_SELECT_CLOTH = "SELECT * " + "FROM db_cloth"+" WHERE uid=? AND info=?";
    public static final String SQL_DB_SELECT_CLOTH_SEASON_ALL = "SELECT * " + "FROM db_cloth"+" WHERE season='all' AND uid=? AND clothtype=? ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_CLOTH_SEASON_EACH = "SELECT * " + "FROM db_cloth"+" WHERE uid=? AND season=? AND clothtype=? ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_CLOTH_SEASON_ALL_DETAIL = "SELECT * " + "FROM db_cloth"+" WHERE uid=? AND season=all AND clothtype=? AND detail1=? ORDER BY reg_id DESC ";
    public static final String SQL_DB_SELECT_CLOTH_SEASON_EACH_DETAIL = "SELECT * " + "FROM db_cloth"+" WHERE uid=? AND season=? AND clothtype=? AND detail1=? ORDER BY reg_id DESC ";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE db_cloth " + "SET season=?, clothtype=?, detail1=?, detail2=?"+" WHERE uid=? AND info=?";
    public static final String SQL_DB_UPDATE_DATA_DETAIL2 = "UPDATE db_cloth " + "SET detail2=?"+" WHERE uid=? AND info=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM db_cloth "+"WHERE uid=? AND info=?";

}
