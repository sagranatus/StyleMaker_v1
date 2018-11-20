package com.sagra.stylemaker_v1.server;
//exp : 서버로 연결하는 부분이다

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "https://ssagranatus.cafe24.com/userData.php";
    // public static String URL_LOGIN = "http://192.168.116.1/android_login_api/login.php";
    // Server user register url
    public static String URL_REGISTER = "https://ssagranatus.cafe24.com/userData.php";
    public static String URL_USERUPDATE = "https://ssagranatus.cafe24.com/userData.php";
    public static String URL_UPLOADIMAGE = "https://ssagranatus.cafe24.com/upload_image.php";

    public static String URL_CLOTHDATA = "https://ssagranatus.cafe24.com/clothData.php";
    public static String URL_CODIDATA = "https://ssagranatus.cafe24.com/codiData.php";
    public static String URL_DAILYDATA = "https://ssagranatus.cafe24.com/dailyData.php";
    public static String URL_STYLEDATA = "https://ssagranatus.cafe24.com/styleData.php";
    // public static String URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";
    public static String URL_GETCOMPANY = "https://ssagranatus.cafe24.com/getcompany.php";
}