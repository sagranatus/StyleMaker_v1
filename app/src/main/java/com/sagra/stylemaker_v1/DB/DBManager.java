package com.sagra.stylemaker_v1.DB;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.data.StyleData;
import com.sagra.stylemaker_v1.data.UserData;
import com.sagra.stylemaker_v1.data.WeatherData;

import java.util.ArrayList;

public class DBManager {
    private final String DB_NAME = "db_stylemaker.db";
    private final int DB_VERSION = 1;

    private Context mContext = null;
    private OpenHelper mOpener = null;
    private SQLiteDatabase mDbController = null;

    class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase aDb) {
            aDb.execSQL(UserDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(CodiDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(DailyDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(WeatherDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(ClothDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(StyleDBSqlData.SQL_DB_CREATE_TABLE);
        }



        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public DBManager(Context aContext){
        this.mContext = aContext;
        this.mOpener = new OpenHelper(mContext, DB_NAME, null, DB_VERSION);
    }

    public void dbOpen(){
        this.mDbController = mOpener.getWritableDatabase();
    }
    public void dbClose(){
        this.mDbController.close();
    }

    //user data

    public void insertUserData(String aSql, UserData aCData) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");
        //   mDbController.execSQL("delete from user");
        String[] sqlData = aCData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);


    }

    public void selectUserData(String aSql, String uid, ArrayList<UserData> aCDataList){
        String[] sqlData = {uid};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            UserData cData = new UserData(
                    results.getString(0),
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateUserProfile(String aSql, String path, String uid) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");
        //   mDbController.execSQL("delete from user");
        String[] sqlData = {path, uid};
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void updateUserData(String aSql, String[] info) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");
        //   mDbController.execSQL("delete from user");
        String[] sqlData = info;
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteUserData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }





    // ClothData 관련 코드
    public void insertClothData(String aSql, ClothData cData){
        String[] sqlData = cData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void updateClothData(String aSql, String[] sqlData){ // sqlData 는 imagepath info 이다.
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectClothData(String aSql, String[] sqlData, ArrayList<ClothData> cDataList){ // sqlData 는 imagepath info 이다.

        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            ClothData cData = new ClothData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            cDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectClothData(String aSql, ClothData clothData, ArrayList<ClothData> cDataList){
        //    mDbController.execSQL("delete from db_dailyweather"); 테이블 내용 비우기
        //   mDbController.execSQL(WeatherDBSqlData.SQL_DB_CREATE_TABLE);
        if(clothData.getDetail1().equals("-")){
            if(!clothData.getSeason().equals("all")){
                String[] sqlData = {clothData.getUid(), clothData.getType()};
                Cursor results = this.mDbController.rawQuery(ClothDBSqlData.SQL_DB_SELECT_CLOTH_SEASON_ALL, sqlData);
                results.moveToNext();
                while(!results.isAfterLast()){
                    ClothData cData = new ClothData(
                            results.getString(1),
                            results.getString(2),
                            results.getString(3),
                            results.getString(4),
                            results.getString(5),
                            results.getString(6));
                    cDataList.add(cData);
                    results.moveToNext();
                }
                results.close();
            }
            String[] sqlData = {clothData.getUid(), clothData.getSeason(), clothData.getType()};
            Cursor results = this.mDbController.rawQuery(ClothDBSqlData.SQL_DB_SELECT_CLOTH_SEASON_EACH, sqlData);
            results.moveToNext();
            while(!results.isAfterLast()){
                ClothData cData = new ClothData(
                        results.getString(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6));
                cDataList.add(cData);
                results.moveToNext();
            }
            results.close();

        }else{
            if(!clothData.getSeason().equals("all")) {
                String[] sqlData = {clothData.getUid(), clothData.getType(), clothData.getDetail1()};
                Cursor results = this.mDbController.rawQuery(ClothDBSqlData.SQL_DB_SELECT_CLOTH_SEASON_ALL_DETAIL, sqlData);
                results.moveToNext();
                while (!results.isAfterLast()) {
                    ClothData cData = new ClothData(
                            results.getString(1),
                            results.getString(2),
                            results.getString(3),
                            results.getString(4),
                            results.getString(5),
                            results.getString(6));
                    cDataList.add(cData);
                    results.moveToNext();
                }
                results.close();
            }
            String[] sqlData = {clothData.getUid(), clothData.getType(), clothData.getDetail1()};
            Cursor results = this.mDbController.rawQuery(ClothDBSqlData.SQL_DB_SELECT_CLOTH_SEASON_EACH_DETAIL, sqlData);
            results.moveToNext();
            while (!results.isAfterLast()) {
                ClothData cData = new ClothData(
                        results.getString(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6));
                cDataList.add(cData);
                results.moveToNext();
            }
            results.close();


        }

    }

    public void deleteClothData(String aSql, ClothData aCData){
        String[] sqlData = {aCData.getUid(), aCData.getInfo()};
        this.mDbController.execSQL(aSql, sqlData);
    }



    // 코디 DB관련
    public void insertCodiData(String aSql, CodiData aCData){
        String[] sqlData = aCData.getCDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void selectCodiData(String aSql, String uid, String season, String type, ArrayList<CodiData> aCDataList){
        String[] sqlData = {uid, season, type};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            CodiData cData = new CodiData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10),
                    results.getString(11));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectcodi(String aSql,String uid, String info, ArrayList<CodiData> aCDataList){
        String[] sqlData = {uid, info};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            CodiData cData = new CodiData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10),
                    results.getString(11));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void updateCodiData(String aSql, String[] sqlData){
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteCodiData(String aSql, String uid, String number){
        String[] sqlData = {uid, number};
        this.mDbController.execSQL(aSql, sqlData);
    }



    public void insertDailyData(String aSql, CodiData aCData){
        String[] sqlData = aCData.getCDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectDailyData(String aSql, String uid, String date, ArrayList<CodiData> aCDataList){
        String[] sqlData = {uid, date};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            CodiData cData = new CodiData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10),
                    results.getString(11));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateDailyData(String aSql, String[] sqlData){
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void deleteDailyData(String aSql, String uid, String number){
        String[] sqlData = {uid, number};
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteData(String aSql, CodiData aCData){
        String[] sqlData = {aCData.getNumber()};
        this.mDbController.execSQL(aSql, sqlData);
    }


    public void deleteData_Cloth(String aSql, String uid, String date){
        String[] sqlData = {uid, date};
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void updateData(String aSql, String[] sqlData){
        this.mDbController.execSQL(aSql, sqlData);
    }


 /*   public void selectAll(String aSql, ArrayList<CodiData> aCDataList){
        Cursor results = this.mDbController.rawQuery(aSql, null);
        results.moveToNext();
        while(!results.isAfterLast()){
            CodiData cData = new CodiData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10),
                    results.getString(11));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectAllbySeason(String aSql, String season, ArrayList<CodiData> aCDataList){
        String[] sqlData = {season};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            CodiData cData = new CodiData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10),
                    results.getString(11));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
*/
    public void selectAllbySeasonAndType(String aSql, String uid, String season, String type, ArrayList<CodiData> aCDataList){
        String[] sqlData = {uid, season, type};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            CodiData cData = new CodiData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10),
                    results.getString(11));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }


    public void selectOnebyDate(String aSql, String uid, String date, ArrayList<CodiData> aCDataList){
                String[] sqlData = {uid, date};
                Cursor results = this.mDbController.rawQuery(aSql, sqlData);
                results.moveToNext();
                while(!results.isAfterLast()){
                CodiData cData = new CodiData(
                        results.getString(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6),
                        results.getString(7),
                        results.getString(8),
                        results.getString(9),
                        results.getString(10),
                        results.getString(11));
                aCDataList.add(cData);
                results.moveToNext();
            }
            results.close();
        }

    // weather DB 관련
    public void insertWeatherData(String aSql, WeatherData wCData){

        String[] sqlData = wCData.getWDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void updateWeatherData(String aSql, String[] sqlData){
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectWeatherbyDate(String aSql, String date, ArrayList<WeatherData> aWDataList){
    //    mDbController.execSQL("delete from db_dailyweather"); 테이블 내용 비우기
     //   mDbController.execSQL(WeatherDBSqlData.SQL_DB_CREATE_TABLE);
        String[] sqlData = {date};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
           WeatherData wData = new WeatherData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4));
            aWDataList.add(wData);
            results.moveToNext();
        }
        results.close();
    }


  /*  public void selectAllWeather(String aSql, ArrayList<WeatherData> aWDataList){
        Cursor results = this.mDbController.rawQuery(aSql, null);
        results.moveToNext();
        while(!results.isAfterLast()){
           WeatherData wData = new WeatherData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4));
            aWDataList.add(wData);
            results.moveToNext();
        }
        results.close();
    } */


    public void selectCloth(String aSql, String uid, String info, ArrayList<ClothData> cDataList){ // info는 imagepath info 이다.
        //    mDbController.execSQL("delete from db_dailyweather"); 테이블 내용 비우기
        //   mDbController.execSQL(WeatherDBSqlData.SQL_DB_CREATE_TABLE);
        String[] sqlData = {uid, info};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            ClothData cData = new ClothData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            cDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }



    public void selectClothbySeason(String aSql, String uid, String season, String type, ArrayList<ClothData> cDataList){
        //    mDbController.execSQL("delete from db_dailyweather"); 테이블 내용 비우기
        //   mDbController.execSQL(WeatherDBSqlData.SQL_DB_CREATE_TABLE);
        String[] sqlData = {uid, season, type};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            ClothData cData = new ClothData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            cDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    // daily 관련 DB



    // 스타일북 관련
    public void insertStyleData(String aSql, StyleData sData){
   //     mDbController.execSQL("delete from db_style");
     //   mDbController.execSQL(StyleDBSqlData.SQL_DB_CREATE_TABLE); // 나중에 지워야해
        String[] sqlData = sData.getCDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectStyle(String aSql, String uid, String info, ArrayList<StyleData> aCDataList){
        String[] sqlData = {uid, info};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            StyleData sData = new  StyleData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            aCDataList.add(sData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectStylebySeason(String aSql, String uid, String season, ArrayList<StyleData> aCDataList){
        String[] sqlData = {uid, season};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            StyleData sData = new  StyleData(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            aCDataList.add(sData);
            results.moveToNext();
        }
        results.close();
    }



}
