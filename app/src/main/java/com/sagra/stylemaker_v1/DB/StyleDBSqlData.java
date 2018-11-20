package com.sagra.stylemaker_v1.DB;

public class StyleDBSqlData { //stylebook은 그냥 이미지로 저장하는게 나을 거 같다. 안그러면 너무 복잡도가 늘어남.
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE db_style "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"uid TEXT NOT NULL, "
            +"number TEXT NOT NULL, " // imagepath number랑 갖게만들자
            +"codinumber TEXT NULL, " // codidata number로 옷 가져오기
            +"season TEXT NULL, " // codidata number로 옷 가져오기
            +"coditag TEXT NULL, "
            + "clothtag TEXT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO db_style "
            +"(uid, number, codinumber, season, coditag, clothtag)"
            +"VALUES(?, ?, ?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE db_style " + "SET season=?, type=?, top=?, pants=?, shoes=?, outer=?, bag=?, accessories=?, tag=?"+" WHERE number=?";
    public static final String SQL_DB_UPDATE_DATA_TAG = "UPDATE db_style " + "SET tag=?"+" WHERE number=?";
    public static final String SQL_DB_SELECT_ALL = "SELECT * " + "FROM db_style ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_STYLE = "SELECT * " + "FROM db_style"+ " WHERE uid=? AND number= ?";
    public static final String SQL_DB_SELECT_SEASON = "SELECT * " + "FROM db_style"+" WHERE uid=? AND season=? ORDER BY reg_id DESC";
    public static final String SQL_DB_SELECT_SEASON_TYPE = "SELECT * " + "FROM db_style"+" WHERE season= ? AND type= ?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM db_style "+"WHERE uid=? AND number=?";
}

