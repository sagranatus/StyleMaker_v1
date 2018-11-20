package com.sagra.stylemaker_v1.DB;

public class DailyDBSqlData {
        public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE db_dailycodi "+
                "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"uid TEXT NOT NULL, "
                +"date TEXT NOT NULL unique, "
                +"season TEXT NOT NULL, "
                +"type TEXT NOT NULL, "
                +"top TEXT NOT NULL, "
                +"bottom TEXT NOT NULL, "
                +"shoes TEXT NOT NULL, "
                +"outdoor TEXT NOT NULL, "
                +"bag TEXT NOT NULL, "
                +"acc TEXT NOT NULL,"
                + "tag TEXT NULL)";
        public static final String SQL_DB_INSERT_DATA = "INSERT INTO db_dailycodi "
                +"(uid, date, season, type, top, bottom, shoes, outdoor, bag, acc, tag)"
                +"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        public static final String SQL_DB_UPDATE_DATA_TAG = "UPDATE db_dailycodi " + "SET tag=?"+" WHERE uid=? AND date=?";
        public static final String SQL_DB_SELECT_ALL = "SELECT * " + "FROM db_dailycodi ORDER BY reg_id DESC";
        public static final String SQL_DB_SELECT_DATE = "SELECT * " + "FROM db_dailycodi"+" WHERE uid=? AND date=? ORDER BY reg_id DESC";
        public static final String SQL_DB_UPDATE_DATE = "UPDATE db_dailycodi " + "SET season=?, type=?, top=?, bottom=?, shoes=?, outdoor=?, bag=?, acc=?, tag=?"+" WHERE uid=? AND date=?";
        public static final String SQL_DB_DELETE_DATA = "DELETE FROM db_dailycodi "+"WHERE uid=? AND date=?";

    }
