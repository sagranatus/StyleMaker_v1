package com.sagra.stylemaker_v1.DB;

public class UserDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE user "+
            "(uid INTEGER NOT NULL, "
            +"user_id TEXT NOT NULL, "
            +"name TEXT NOT NULL, "
            +"email TEXT NOT NULL UNIQUE, "
            +"gender TEXT NULL, "
            +"created_at TEXT NULL," +
            "profile TEXT NULL)";

    public static final String SQL_DB_INSERT_DATA = "INSERT INTO user "
            +"(uid, user_id, name, email, gender, created_at, profile)"
            +"VALUES(?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_PROFILE = "UPDATE user " + "SET profile=?"+" WHERE uid=?";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE user " + "SET user_id=?, name=?, email=?, gender=?, profile=?"+" WHERE uid=?";
    public static final String SQL_DB_SELECT_DATA = "SELECT * " + "FROM user" +" WHERE uid=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM user "+"WHERE uid=?";
}
