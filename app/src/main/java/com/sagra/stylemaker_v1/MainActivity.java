package com.sagra.stylemaker_v1;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.DB.ClothDBSqlData;
import com.sagra.stylemaker_v1.DB.CodiDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.DailyDBSqlData;
import com.sagra.stylemaker_v1.DB.UserDBSqlData;
import com.sagra.stylemaker_v1.DB.WeatherDBSqlData;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.data.UserData;
import com.sagra.stylemaker_v1.data.WeatherData;
import com.sagra.stylemaker_v1.dialog.AddtagDialog;
import com.sagra.stylemaker_v1.dialog.ListSelectorDialog;
import com.sagra.stylemaker_v1.etc.BottomNavigationViewHelper;
import com.sagra.stylemaker_v1.etc.Fonttype;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.DownloadImageTask_bitmap;
import com.sagra.stylemaker_v1.server.Server_CodiData;
import com.sagra.stylemaker_v1.weather.GoogleMapsGeocodingService;
import com.sagra.stylemaker_v1.weather.GpsInfo;
import com.sagra.stylemaker_v1.weather.YahooWeatherService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleMapsGeocodingService.GeocodingServiceListener, YahooWeatherService.WeatherServiceListener, View.OnClickListener{
    // 당연히도 맨 처음 화면이다!

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;

    String uid="1";
    // GPSTracker class
    private GpsInfo gps;
    GoogleMapsGeocodingService geocodingService;
    private YahooWeatherService weatherService;

    ImageButton select1, select2;
    String date;

    BottomNavigationView bottomNavigationView;

    private boolean isPermission = false;
    static final int PERMISSION_REQUEST_CODE = 1; //this
    String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};//this

    ImageView weatherIconImageView;
    TextView temperatureTextView;
    TextView locationTextView;

    ArrayList<CodiData> aCDataList;
    private SessionManager session;

    String number;
    ImageView img, img2, img3, img4, img5, img6;
    ListSelectorDialog dlg;
    String[] listk, listv;

    ListSelectorDialog dlg_left;
    String[] listk_left, listv_left;
    CodiData info;

    Menu thismenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        //actionbar setting
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_left);
        TextView mytext = (TextView) findViewById(R.id.mytext);
        Fonttype.setFont( "Billabong",MainActivity.this, mytext);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.list);

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);

        // 날씨정보는 우선 안보이게하고 값이 불러와 지면 보이게 한다.
        weatherIconImageView.setVisibility(View.GONE);
        temperatureTextView.setVisibility(View.GONE);
        locationTextView.setVisibility(View.GONE);


        // 계절 정보 세팅하기
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        SharedPreferences.Editor wEditPreference = wPreference.edit();
        Calendar cal_ = Calendar.getInstance();
        int month =  cal_.get(Calendar.MONTH) + 1;
        if((month >=3 && month<6) || (month>=10 && month <12) ){
            wEditPreference.putString("season", "봄,가을");
            wEditPreference.commit();
        }else if(month>=6 && month <10){
            wEditPreference.putString("season", "여름");
            wEditPreference.commit();
        }else{
            wEditPreference.putString("season", "겨울");
            wEditPreference.commit();
        }
        //  String season = wPreference.getString("season", "");
        // Toast.makeText(this, season, Toast.LENGTH_SHORT).show();



        // 권한 요청을 해야 함
        SharedPreferences mPreference = getSharedPreferences("Camera", MODE_PRIVATE);
        String camerapermmission = mPreference.getString("camerarequest", "default");
        if(!camerapermmission.equals("ok")){
            callPermission();
        }else{
        //    getGeoAndWeather();
            getWeatherThread g = new getWeatherThread("thread");
            g.run();					//start() 호출
        }

        // 다이얼로그 세팅
        // custom dialog setting
        dlg  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정
        listk = new String[] {"a", "b", "c","d", "e"};
        listv = new String[] {"스타일북만들기","태그달기", "코디함에 저장" ,"수정", "삭제"};


        // custom dialog setting
        dlg_left  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정
        listk_left = new String[] {"a", "b"};
        listv_left = new String[] {"프로필 수정","옷 가져오기"};


        RelativeLayout rl = (RelativeLayout)findViewById(R.id.codiimage);
        rl.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        uid = session.getUid();
        Log.d("saea", uid);

        ImageView profileimg = (ImageView) findViewById(R.id.profileimg);
        ContextWrapper cw = new ContextWrapper(MainActivity.this);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
        ArrayList<UserData> userdata = new ArrayList<UserData>();


        //Server_UserData.getUser(MainActivity.this, "MainActivity", profileimg, uid);


        if(directory.list().length == 0){
            Log.d("saea", "D not exist");
            // 여기에서 데이터베이스랑 이미지 모두 가져와야 한다.
            ArrayList<ClothData> mAppItem = null; 
            selectClothAll(MainActivity.this, uid, mAppItem);
            selectCodiAll(MainActivity.this, uid);
            selectDailyAll(MainActivity.this, uid);
            getUser(MainActivity.this, uid);
        }else{
            Log.d("saea", "D exist");
            // 프로필 이미지 가져오기
            DBManager dbMgr = new DBManager(MainActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectUserData(UserDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
            dbMgr.dbClose();
            if(userdata.get(0) != null){
                UserData udata = userdata.get(0);
                try {
                    File f=new File(directory, udata.getProfile());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                  //  profileimg.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(profileimg);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
       // getUser(MainActivity.this, uid);

        // bottomnavigation 뷰 등록
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        BottomNavigationViewHelper.disableShiftMode2(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem_1 = menu.getItem(0);
        MenuItem menuItem_2 = menu.getItem(1);
        MenuItem menuItem_3 = menu.getItem(2);
        MenuItem menuItem_4 = menu.getItem(3);
        MenuItem menuItem_5 = menu.getItem(4);
        menuItem_1.setChecked(false);
        menuItem_2.setChecked(false);
        menuItem_3.setChecked(false);
        menuItem_4.setChecked(false);
        menuItem_5.setChecked(false);

        SharedPreferences setPreference1 = getSharedPreferences("Setting", MODE_PRIVATE);
        String gender1 = setPreference1.getString("gender", "");
        if(gender1.equals("남자")){
            menuItem_4.setIcon(R.drawable.style_male);
        }else{
            menuItem_4.setIcon(R.drawable.style_female);
        }

        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_one:
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(MainActivity.this, ThirdActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(MainActivity.this, FourthActivity.class);
                        startActivity(i4);
                        break;
                    case R.id.action_fifth:
                        Intent i5 = new Intent(MainActivity.this, FifthActivity.class);
                        startActivity(i5);
                        break;
                }
                return false;
            }

        });

        img = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);
        img4 = (ImageView) findViewById(R.id.imageView4);
        img5 = (ImageView) findViewById(R.id.imageView5);
        img6 = (ImageView) findViewById(R.id.imageView6);

        img.setImageResource(0);
        img2.setImageResource(0);
        img3.setImageResource(0);
        img4.setImageResource(0);
        img5.setImageResource(0);
        img6.setImageResource(0);

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd");
        date = dayTime.format(new Date(time)); // number에는 코디저장한 시간으로 저장한다!
        aCDataList = new ArrayList<CodiData>();
        Log.d("saea","date: "+date);
       // selectDailybyInfo(MainActivity.this, uid, date);
        DBManager dbMgr = new DBManager(MainActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date, aCDataList);
        dbMgr.dbClose();
        if(!aCDataList.isEmpty()){
            CodiData codiData = aCDataList.get(0);
            info = codiData;
            afterSelect(codiData);
        }


   /*   이미지 가져올때
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f=new File(directory, "saea.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imageView1);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }*/
    }

    public void afterAddTag(CodiData codiData){
      //  selectDailybyInfo(MainActivity.this, uid, date);

            info = codiData;
            afterSelect(codiData);


    }

    public void afterSelect(CodiData info){
        if(info != null) {

            if(thismenu != null){
                MenuItem menuitem = thismenu.findItem(R.id.action_zero);
                menuitem.setVisible(true);
            }
            number = info.getNumber();

            if(info.getTop().contains("dress")){
                final float scale = getResources().getDisplayMetrics().density;
                int height  = (int) (300 * scale);
                img.getLayoutParams().height = height;
                img2.getLayoutParams().height = 0;
            }else{
                final float scale = getResources().getDisplayMetrics().density;
                int height  = (int) (150 * scale);
                img.getLayoutParams().height = height;
                img2.getLayoutParams().height = height;
            }
            //  Bitmap top_bitmap = BitmapFactory.decodeFile(top);
            // img.setImageBitmap(top_bitmap);

            ContextWrapper cw = new ContextWrapper(MainActivity.this);

            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
            if(!info.getTop().isEmpty()){

                // new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getTop());
                try {
                    File f=new File(directory, info.getTop());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                 //   img.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(img);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            if(!info.getBottom().isEmpty()) {
                // new DownloadImageTask(viewHolder.imageView2).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getBottom());
                try {
                    File f=new File(directory, info.getBottom());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    //img2.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(img2);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            if(!info.getShoes().isEmpty()) {
                //   new DownloadImageTask(viewHolder.imageView3).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getShoes());
                try {
                    File f=new File(directory, info.getShoes());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                 //   img3.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(img3);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            if(!info.getOuter().isEmpty()) {
                //   new DownloadImageTask(viewHolder.imageView4).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getOuter());
                try {
                    File f=new File(directory, info.getOuter());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                  //  img4.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(img4);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            if(!info.getBag().isEmpty()) {
                //   new DownloadImageTask(viewHolder.imageView5).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getBag());
                try {
                    File f=new File(directory, info.getBag());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                  //  img5.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(img5);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            if(!info.getAccessories().isEmpty()) {
                //   new DownloadImageTask(viewHolder.imageView6).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getAccessories());
                try {
                    File f = new File(directory, info.getAccessories());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                  //  img6.setImageBitmap(b);
                    Glide.with(MainActivity.this).load(f).into(img6);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            img.setOnClickListener(this);
            img2.setOnClickListener(this);
            img3.setOnClickListener(this);
            img4.setOnClickListener(this);
            img5.setOnClickListener(this);
            img6.setOnClickListener(this);
        }else{
            if(thismenu != null){
                MenuItem menuitem = thismenu.findItem(R.id.action_zero);
                menuitem.setVisible(false);
            }
        }
    }


    // 오늘의 코디가 있는 경우에는 수정할 수 있게 한다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_main, menu);
     //   DBManager dbMgr2 = new DBManager(this);
     //   dbMgr2.dbOpen();
        thismenu = menu;
        if(!aCDataList.isEmpty()){
            menu.getItem(0).setVisible(true);
        }else{
            menu.getItem(0).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!aCDataList.isEmpty()){
            menu.getItem(0).setVisible(true);
        }else{
            menu.getItem(0).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // 커스텀 다이얼로그 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_zero:
                // show the list dialog.
                dlg.show(listv, listk, new ListSelectorDialog.listSelectorInterface() {

                    // procedure if user cancels the dialog.
                    public void selectorCanceled() {
                    }
                    // procedure for when a user selects an item in the dialog.
                    public void selectedItem(String key, String item) {
                        if(item.equals("스타일북만들기")){
                            Intent i = new Intent(MainActivity.this, AddstyleActivity.class);
                            i.putExtra("info", info);
                            i.putExtra("date", date);
                            i.putExtra("frommain", "yes");
                            startActivity(i);
                        }else if(item.equals("코디함에 저장")){

                            Server_CodiData.insertCodi(MainActivity.this, uid, info);
                            Toast.makeText(MainActivity.this, "코디함에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        }else if(item.equals("삭제")){
                            DialogSimple();
                            MenuItem menuitem = thismenu
                                    .findItem(R.id.action_zero);
                            menuitem.setVisible(false);
                        }else if(item.equals("수정")){
                            Intent i = new Intent(MainActivity.this, AddcodiActivity.class);
                            i.putExtra("codiupdate", "yes");
                            i.putExtra("info", info);
                            i.putExtra("date", date);
                            i.putExtra("frommain", "main");
                            startActivity(i);
                        }else if(item.equals("태그달기")) {
                            AddtagDialog dialog = new AddtagDialog(MainActivity.this, uid, info, "main", date);
                            dialog.show();
                        }
                    }
                });
                return true;
            case R.id.action_one:
            Intent i = new Intent(MainActivity.this, AddcodiActivity.class);
            i.putExtra("date", date);
            i.putExtra("frommain", "main");
            startActivity(i);
            return true;
            case R.id.action_two:
                Intent i2 = new Intent(MainActivity.this, ThirdActivity.class);
                i2.putExtra("date", date);
                i2.putExtra("frommain", "main");
                startActivity(i2);
                return true;
            default:
                // show the list dialog.
                dlg_left.show(listv_left, listk_left, new ListSelectorDialog.listSelectorInterface() {

                    // procedure if user cancels the dialog.
                    public void selectorCanceled() {
                    }
                    // procedure for when a user selects an item in the dialog.
                    public void selectedItem(String key, String item) {
                        if(item.equals("옷 가져오기")){
                        /*    String path = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker";
                            Log.d("Files", "Path: " + path);
                            File directory = new File(path);
                            File[] files = directory.listFiles();
                            Log.d("Files", "Size: "+ files.length);
                            for (int j = 0; j < files.length; j++)
                            {
                                Log.d("saea", "FileName:" + files[j].getName());
                                if(files[j].getName().contains("봄,가을") || files[j].getName().contains("여름") || files[j].getName().contains("겨울") || files[j].getName().contains("계절없음")) {
                                    Log.d("saea", "jajaja");
                                    int first = files[j].getName().indexOf('-');
                                    int second = files[j].getName().indexOf('-', 5);
                                    Log.d("saea", first + "val" + second + "value");
                                    String set1 = files[j].getName().substring(0, first);
                                    String set2 = files[j].getName().substring(first + 1, second);
                                    String set3 = null;
                                    Log.d("saea", set1 + set2);
                                    // 파일 위치가져와서 DB에 저장하기
                                    String filePlace = Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker" + File.separator + files[j].getName();
                                    Log.d("saea", filePlace);
                                    ClothData cData = new ClothData(uid, set1, set2, filePlace, set3, null);

                                    ArrayList<ClothData> cDataList = new ArrayList<ClothData>();
                                    DBManager dbMgr = new DBManager(MainActivity.this);
                                    dbMgr.dbOpen();
                                    dbMgr.selectCloth(ClothDBSqlData.SQL_DB_SELECT_CLOTH, uid, filePlace, cDataList);
                                    dbMgr.dbClose();

                                    if (cDataList.isEmpty()) {
                                        dbMgr.dbOpen();
                                        dbMgr.insertClothData(ClothDBSqlData.SQL_DB_INSERT_DATA, cData);
                                        dbMgr.dbClose();
                                    }
                                }
                            }*/
                        }else if(item.equals("프로필 수정")){
                            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(i);
                        }
                    }
                });
                return true;
        }
    }



    // 삭제 이벤트
    public void DialogSimple(){
        android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(this);
        alt_bld.setMessage("이 옷을 삭제하시겠어요?").setCancelable(
                false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteDaily(MainActivity.this, uid, date);
                        DBManager dbMgr = new DBManager(MainActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.deleteDailyData(DailyDBSqlData.SQL_DB_DELETE_DATA, uid, date);
                        dbMgr.dbClose();
                        onResume();
                    }
                }).setNegativeButton("삭제안함",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alt_bld.create();
        alert.setTitle("삭제 확인");
        // Icon for AlertDialog
        //   alert.setIcon(R.drawable.icon);
        alert.show();

    }



    // 퍼미션 관련 코드
    private void callPermission() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            // Log.d(TAG, "퍼미션 요청");
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            // Log.d(TAG, "퍼미션 허가");
            // Set the content view and get references to our views

        }

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
         //   getGeoAndWeather();
            getWeatherThread g = new getWeatherThread("thread");
            g.run();					//start() 호출

        }
    }

    // 퍼미션 관련 코드
    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)) {
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //모든 퍼미션이 허가된 경우

        return true;
    }

    // 퍼미션 관련 코드
    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    // 퍼미션 관련 코드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!writeAccepted) {
                        showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else {

                    }
                }
            }
        } else if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }

        // camerarequest를 ok로 저장하고 AddclothActivity에서 정보를 가져와서 ok인 경우에만 사진을 찍는다
        SharedPreferences mPreference = getSharedPreferences("Camera", MODE_PRIVATE);
        SharedPreferences.Editor mEditPreference = mPreference.edit();
        mEditPreference.putString("camerarequest", "ok");
        mEditPreference.commit();

      //  getGeoAndWeather();
        getWeatherThread g = new getWeatherThread("thread");
        g.run();					//start() 호출

    }

    // 퍼미션 관련 코드
    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    // 각 옷 클릭 이벤트
    @Override
    public void onClick(View v)
    {

        Intent i = new Intent(MainActivity.this, ClothdetailActivity.class);
        switch (v.getId()) {
            case R.id.imageView1:
                if(img.getDrawable() != null){
                    i.putExtra("info_fromcodi", info.getTop());
                    startActivity(i);
                }

                break;
            case R.id.imageView2:
                if(img2.getDrawable() != null){
                    i.putExtra("info_fromcodi", info.getBottom());
                    startActivity(i);
                }
                break;
            case R.id.imageView3:
                if(img3.getDrawable() != null){
                    i.putExtra("info_fromcodi", info.getShoes());
                    startActivity(i);
                }
                break;
            case R.id.imageView4:
                if(img4.getDrawable() != null){
                    i.putExtra("info_fromcodi", info.getOuter());
                    startActivity(i);
                }
                break;
            case R.id.imageView5:
                if(img5.getDrawable() != null){
                    i.putExtra("info_fromcodi", info.getBag());
                    startActivity(i);
                }
                break;
            case R.id.imageView6:
                if(img6.getDrawable() != null) {
                    i.putExtra("info_fromcodi", info.getAccessories());
                    startActivity(i);
                }
                break;
        }

    }


    // 지역 및 날씨 정보 가져오기
    @Override
    public void geocodeSuccess(GoogleMapsGeocodingService.LocationResult location) {
        // completed geocoding successfully
        weatherService.refreshWeather(location.getAddress());

    }

    @Override
    public void geocodeFailure(Exception exception) {

    }

    // 날씨정보 가져오기, 오늘 정보, 오늘 이후로 계속 업데이트하기
    @Override
    public void serviceSuccess(YahooWeatherService.Channel channel) {


      //  YahooWeatherService.Condition condition = channel.getItem().getCondition();
     //   YahooWeatherService.Units units = channel.getUnits();
        YahooWeatherService.Condition[] forecast = channel.getItem().getForecast();


        weatherIconImageView.setVisibility(View.VISIBLE);
        temperatureTextView.setVisibility(View.VISIBLE);
      //  conditionTextView.setVisibility(View.VISIBLE);
        locationTextView.setVisibility(View.VISIBLE);
        int weatherIconImageResource = getResources().getIdentifier("icon_" + forecast[0].getCode(), "drawable", getPackageName());
        weatherIconImageView.setImageResource(weatherIconImageResource);
        temperatureTextView.setText(forecast[0].getLowTemperature()+"/"+forecast[0].getHighTemperature()+"℃");
     //   conditionTextView.setText(forecast[0].getDescription());
        locationTextView.setText(channel.getLocation());

        Calendar cal_ = Calendar.getInstance( );  // 현재 날짜/시간 등의 각종 정보 얻기
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        // 이번주 날씨 가져오고 sharedpreferences에 저장하기
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        SharedPreferences.Editor wEditPreference = wPreference.edit();
        wEditPreference.putString("todaydate", format1.format(cal_.getTime()));
        wEditPreference.putString("temp", forecast[0].getHighTemperature()+"/"+forecast[0].getLowTemperature()+"℃");
        wEditPreference.putString("code", "icon_" + forecast[0].getCode());
        wEditPreference.putString("description", forecast[0].getDescription());
        wEditPreference.putString("location",channel.getLocation());
        wEditPreference.commit();


        Calendar cal = Calendar.getInstance();  // 현재 날짜/시간 등의 각종 정보 얻기

        System.out.println(cal.get(Calendar.DAY_OF_WEEK));
       // int today = cal.get(Calendar.DAY_OF_WEEK);
        //오늘 날짜 formatted 안에 있음
        String formatted = format1.format(cal.getTime());
        System.out.println(formatted);

        String result = selectWeatherDB(formatted);
        //만일 오늘 날짜 데이터가 없으면 모든 데이터 가져와서 삽입
        if(result.equals("none")){

            for (int day = 0; day < forecast.length; day++) {

                YahooWeatherService.Condition currentCondition = forecast[day];

                weatherIconImageView.setImageResource(weatherIconImageResource);
                Log.d("saea", "실제날짜"+formatted+"날짜:"+forecast[day].getDay() + "최고기온:"+forecast[day].getHighTemperature()+"최저기온:"+forecast[day].getLowTemperature()+"날씨코드:"+forecast[day].getCode());

                WeatherData wData = new WeatherData(formatted, String.valueOf(forecast[day].getHighTemperature()), String.valueOf(forecast[day].getLowTemperature()), String.valueOf(forecast[day].getCode()));

                DBManager dbMgr = new DBManager(this);
                dbMgr.dbOpen();
                dbMgr.insertWeatherData(WeatherDBSqlData.SQL_DB_INSERT_DATA, wData);
                dbMgr.dbClose();

                cal.add(Calendar.DATE, 1);
                formatted = format1.format(cal.getTime());
            }
        // 오늘 데이터가 있는 경우에는 다시 이번주 나머지 데이터를 업데이트 한다.
        }else{
            //do nothing
            Log.d("saea", "this week data exists");
            // 이번주 날씨 가져오고 저장하기

            for (int day = 0; day < forecast.length; day++) {

                YahooWeatherService.Condition currentCondition = forecast[day];

                weatherIconImageView.setImageResource(weatherIconImageResource);
                Log.d("saea", "실제날짜"+formatted+"날짜:"+forecast[day].getDay() + "최고기온:"+forecast[day].getHighTemperature()+"최저기온:"+forecast[day].getLowTemperature()+"날씨코드:"+forecast[day].getCode());
                // 날짜가 없는 경우만 insert하고 나머지는 업데이트하기
                String result2 = selectWeatherDB(formatted);
                if(!result2.equals("none")){
                    Log.d("saea", formatted+" update");
                    String[] update = {String.valueOf(forecast[day].getHighTemperature()), String.valueOf(forecast[day].getLowTemperature()), String.valueOf(forecast[day].getCode()), formatted};
                    DBManager dbMgr = new DBManager(this);
                    dbMgr.dbOpen();
                    dbMgr.updateWeatherData(WeatherDBSqlData.SQL_DB_UPDATE_DATE, update);
                    dbMgr.dbClose();
                }else{
                    Log.d("saea", formatted+" insert");
                    WeatherData wData2 = new WeatherData(formatted, String.valueOf(forecast[day].getHighTemperature()), String.valueOf(forecast[day].getLowTemperature()), String.valueOf(forecast[day].getCode()));
                    DBManager dbMgr = new DBManager(this);
                    dbMgr.dbOpen();
                    dbMgr.insertWeatherData(WeatherDBSqlData.SQL_DB_INSERT_DATA, wData2);
                    dbMgr.dbClose();
                }

                cal.add(Calendar.DATE, 1);
                formatted = format1.format(cal.getTime());
            }

        }

    }

    // 날짜에 맞는 날씨 데이터가 있는지 확인
    private String selectWeatherDB(String date) {
        ArrayList<WeatherData> aWDataList =  new ArrayList<WeatherData>();
        DBManager dbMgr = new DBManager(this);
        dbMgr.dbOpen();
        dbMgr.selectWeatherbyDate(WeatherDBSqlData.SQL_DB_SELECT_DATE, date, aWDataList);
        dbMgr.dbClose();
        if(!aWDataList.isEmpty()){
            return aWDataList.get(0).getDate();
        }else{
            return "none";
        }
    }

    @Override
    public void serviceFailure(Exception exception) {

    }
/*
    double RE = 6371.00877; // 지구 반경(km)
    double GRID = 5.0; // 격자 간격(km)
    double SLAT1 = 30.0; // 투영 위도1(degree)
    double SLAT2 = 60.0; // 투영 위도2(degree)
    double OLON = 126.0; // 기준점 경도(degree)
    double OLAT = 38.0; // 기준점 위도(degree)
    double XO = 43; // 기준점 X좌표(GRID)
    double YO = 136; // 기1준점 Y좌표(GRID)

*/

    // 지역정보 검색하기, 만약 오늘 날씨 정보가 아직 없는경우 가져온다
    private void getGeoAndWeather() {
        Calendar cal = Calendar.getInstance( );  // 현재 날짜/시간 등의 각종 정보 얻기
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);

        String todaydate_pref = wPreference.getString("todaydate", "date");

        // 오늘 날짜랑 같은 정보가 담겨있으면 정보를 가져와서 날씨를 보여준다.
        if(todaydate_pref.equals(format1.format(cal.getTime()))){

            String temp_pref = wPreference.getString("temp", "");
            String code_pref = wPreference.getString("code", "");
            String description_pref = wPreference.getString("description", "");
            String location_pref = wPreference.getString("location", "");

            weatherIconImageView.setVisibility(View.VISIBLE);
            temperatureTextView.setVisibility(View.VISIBLE);
            locationTextView.setVisibility(View.VISIBLE);

            int weatherIconImageResource = getResources().getIdentifier(code_pref, "drawable", getPackageName());
            weatherIconImageView.setImageResource(weatherIconImageResource);
            temperatureTextView.setText(temp_pref);
            locationTextView.setText(location_pref);

            // 처음 오늘 날씨를 가져오는 경우에
        }else{
            // 아래 삽입
            gps = new GpsInfo(MainActivity.this);
            // GPS 사용유무 가져오기
            if (gps.isGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                weatherService = new YahooWeatherService(MainActivity.this);
                geocodingService = new GoogleMapsGeocodingService(MainActivity.this);
                geocodingService.refreshLocation(latitude, longitude);
            } else {
                // GPS 를 사용할수 없으므로
                gps.showSettingsAlert();
            }
        }

    }

    public class getWeatherThread implements Runnable {
        String str;
        public getWeatherThread(String str){
            this.str = str;
        }

        public void run(){
                    getGeoAndWeather();
            }
        }

    // 로그아웃할때
    private void logoutUser() {

        session.setLogin(false, "");

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void selectDailybyInfo(final Context context, final String uid, final String date) {

        Log.i("saea", "Starting Upload...");
        selectDailybyInfo_Connect(context, uid, date);

    }




    public void selectDailybyInfo_Connect(final Context context, final String uid, final String date) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_DAILYDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");

                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject codi = jObj.getJSONObject("daily");

                        CodiData codiData = new CodiData(codi.getString("uid"), codi.getString("dateval"), codi.getString("season"),
                                codi.getString("type"), codi.getString("top"), codi.getString("bottom"),
                                codi.getString("shoes"), codi.getString("outdoor"),codi.getString("bag"),codi.getString("acc"), codi.getString("tag"));

                        info = codiData;
                        afterSelect(codiData);

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.e("saea", "gettoday daily look: " + uid+date);
                params.put("status", "selectbyInfo");
                params.put("uid", uid);
                params.put("date", date);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void deleteDaily(final Context context, final String uid, final String date) {


        Log.i("saea", "Starting Upload...");
        deleteDaily_Connect(context, uid, date);


    }

    public void deleteDaily_Connect(Context context, final String uid, final String date) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_DAILYDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"<- error status");
                    if (!error) {


                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        Log.d("saea", error+"<- error status");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "delete");
                params.put("uid", uid);
                params.put("date", date);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void selectClothAll(final Context context, final String uid, final ArrayList<ClothData> mAppItem) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

        Log.i("saea", "Starting Upload...");
        selectCloth_Connect(context, uid, mAppItem);
            }
        });
        t.start();
    }


    public void selectCloth_Connect(final Context context, final String uid, final ArrayList<ClothData> mAppItem) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CLOTHDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                ArrayList<String[]> results = new ArrayList<String[]>();
                ArrayList<ClothData> clothItems = new ArrayList<ClothData>();
                Log.d("saea2", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea2", "error status : "+error);
                    if (!error) {
                        if(jObj.has("stack")){
                            JSONArray stack = jObj.getJSONArray("stack");
                            for(int i=0; i<stack.length(); i++) {
                                JSONArray eachstack = stack.getJSONArray(i);
                                String[] arr=new String[eachstack.length()];
                                for(int j=0; j<arr.length; j++) {
                                    arr[j]=eachstack.optString(j);
                                }


                             ClothData clothdata = new ClothData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
                                DBManager dbMgr = new DBManager(context);
                                dbMgr.dbOpen();
                                dbMgr.insertClothData(ClothDBSqlData.SQL_DB_INSERT_DATA, clothdata);
                                dbMgr.dbClose();


                                String urldisplay ="https://ssagranatus.cafe24.com/files/user" + uid + "/"+clothdata.getInfo();
                                Log.d("saea2", "urld"+urldisplay);
                                Bitmap mIcon11 = null;
                                new DownloadImageTask_bitmap(MainActivity.this, uid, clothdata, mIcon11).execute(urldisplay);



                                String[] result = {arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]};
                                results.add(result);
                                clothItems.add(clothdata);
                                Log.d("saea2", String.valueOf(clothItems.size()));
                            }

                        }else{

                        }


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "selectall");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    public void getUser(final Context context,  final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        final ArrayList<UserData>  userData =  new ArrayList<UserData>();

        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String gender = user.getString("gender");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다
                        String profile_pic = user.getString("profile_pic");
                        Log.d("saea", uid);

                        Log.d("saea", uid);
                        UserData cData = new UserData(uid, user_id, name, email, gender, created_at, profile_pic);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.insertUserData(UserDBSqlData.SQL_DB_INSERT_DATA, cData);
                        dbMgr.dbClose();

                        String urldisplay ="https://ssagranatus.cafe24.com/files/user" + uid + "/"+profile_pic;
                        Log.d("saea2", "urld"+urldisplay);
                        Bitmap mIcon11 = null;
                        new DownloadImageTask_bitmap(MainActivity.this, uid, profile_pic, mIcon11).execute(urldisplay);

                        // Displaying the user details on the screen


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg+"1111", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage()+"2222", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "get");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    public void selectCodiAll(final Context context, final String uid) {

        Log.i("saea", "Starting Upload...");

        selectCodi_Connect(context, uid);

    }


    public void selectCodi_Connect(final Context context, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CODIDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                ArrayList<String[]> results = new ArrayList<String[]>();
                ArrayList<CodiData> codiItems = new ArrayList<CodiData>();
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", "error status : "+error);
                    if (!error) {
                        if(jObj.has("stack")){
                            JSONArray stack = jObj.getJSONArray("stack");
                            for(int i=0; i<stack.length(); i++) {
                                JSONArray eachstack = stack.getJSONArray(i);
                                String[] arr=new String[eachstack.length()];
                                for(int j=0; j<arr.length; j++) {
                                    arr[j]=eachstack.optString(j);
                                }
                                CodiData codidata = new CodiData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9], arr[10]);
                                //   String[] result = {arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]};
                                //   results.add(result);
                                DBManager dbMgr = new DBManager(context);
                                dbMgr.dbOpen();
                                dbMgr.insertCodiData(CodiDBSqlData.SQL_DB_INSERT_DATA, codidata);
                                dbMgr.dbClose();
                                codiItems.add(codidata);
                                Log.d("saea", String.valueOf(codiItems.size()));
                            }



                        }else{

                        }


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "selectall");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void selectDailyAll(final Context context, final String uid) {

        Log.i("saea", "Starting Upload...");

        selectDailybyInfo_Connect(context, uid);

    }




    public void selectDailybyInfo_Connect(final Context context, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_DAILYDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");


                    if (!error) {
                        if(jObj.has("stack")){
                            JSONArray stack = jObj.getJSONArray("stack");
                            for(int i=0; i<stack.length(); i++) {
                                JSONArray eachstack = stack.getJSONArray(i);
                                String[] arr=new String[eachstack.length()];
                                for(int j=0; j<arr.length; j++) {
                                    arr[j]=eachstack.optString(j);
                                }
                                CodiData codidata = new CodiData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9], arr[10]);
                                //   String[] result = {arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]};
                                //   results.add(result);
                                DBManager dbMgr = new DBManager(context);
                                dbMgr.dbOpen();
                                dbMgr.insertDailyData(DailyDBSqlData.SQL_DB_INSERT_DATA, codidata);
                                dbMgr.dbClose();
                            }



                        }else{

                        }
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.e("saea", "select: " + uid+date);
                params.put("status", "selectall");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}


