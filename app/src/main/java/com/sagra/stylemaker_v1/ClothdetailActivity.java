package com.sagra.stylemaker_v1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.DB.ClothDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.dialog.AddtagDialog;
import com.sagra.stylemaker_v1.dialog.CustomDialog;
import com.sagra.stylemaker_v1.dialog.ListSelectorDialog;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClothdetailActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    ListSelectorDialog dlg;
    String[] listk, listv;
    ClothData info;
    String imagepath;
    TextView info1, info2, info3, info4;

    String season = null;
    String type= null;
    String detail1= null;
    String detail2= null;
    String uid;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector gestureScanner;
    private ArrayList<ClothData> mAppItem = null;
    String set1,set2,set3;
    ClothData clothdata;
    int thisOrder;
    String info_fromcodi;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothdetail);
        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();
        // actionbar setting
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("뒤로가기");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // intent에서 값 가져오기 및 세팅
        Intent intent = getIntent();
       set1 = intent.getStringExtra("season");
       set2 = intent.getStringExtra("type");
       set3 = intent.getStringExtra("detail1");
        info = (ClothData) intent.getSerializableExtra("info");
        info_fromcodi = intent.getStringExtra("info_fromcodi");
          img = (ImageView) findViewById(R.id.clothimg);
        ClothData data = null;
        if(info_fromcodi != null){
          //  selectClothbyInfo(ClothdetailActivity.this, uid, info_fromcodi);
            ArrayList<ClothData> clothdata = new ArrayList<ClothData>();
            DBManager dbMgr = new DBManager(ClothdetailActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData(ClothDBSqlData.SQL_DB_SELECT_CLOTH, new String[]{uid, info_fromcodi}, clothdata);
            dbMgr.dbClose();
            if(clothdata.get(0) != null) {
                ClothData cdata = clothdata.get(0);
                info = cdata;
                if(info.getInfo().contains("dress")){
                    final float scale = getResources().getDisplayMetrics().density;
                    int width  = (int) (200 * scale);
                    img.getLayoutParams().width = width;
                }
                //   Glide.with(this).load(imagepath).into(img);
           //     new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user"+info.getUid()+"/"+info.getInfo());
                ContextWrapper cw = new ContextWrapper(ClothdetailActivity.this);

                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
                try {
                    File f=new File(directory, cdata.getInfo());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                  //  img.setImageBitmap(b);
                    Glide.with(ClothdetailActivity.this).load(f).into(img);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                int a = img.getLayoutParams().width;
                int b = img.getLayoutParams().height;
                Log.d("saea", a+"and"+b);

                String[] results = englishToKorean(info.getSeason(), info.getType(), info.getDetail1());
                info1 = (TextView) findViewById(R.id.info1);

                info1.setText("시즌: "+results[0]);

                info2 = (TextView) findViewById(R.id.info2);
                info2.setText("타입: "+results[1]);

                info3 = (TextView) findViewById(R.id.info3);
                info3.setText("디테일1: "+results[2]);

                info4 = (TextView) findViewById(R.id.info4);
                info4.setText("태그: "+cdata.getDetail2());
            }

        }else{
            season = info.getSeason();
            type = info.getType();
            imagepath = info.getInfo();
            detail1 = info.getDetail1();
            detail2 = info.getDetail2();
            if(imagepath.contains("dress")){
                final float scale = getResources().getDisplayMetrics().density;
                int width  = (int) (200 * scale);
                img.getLayoutParams().width = width;
            }

            //   Glide.with(this).load(imagepath).into(img);
           // new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user"+info.getUid()+"/"+info.getInfo());
            ContextWrapper cw = new ContextWrapper(ClothdetailActivity.this);

            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
            try {
                File f=new File(directory, info.getInfo());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                //img.setImageBitmap(b);
                Glide.with(ClothdetailActivity.this).load(f).into(img);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            int a = img.getLayoutParams().width;
            int b = img.getLayoutParams().height;
            Log.d("saea", a+"and"+b);

            String[] results = englishToKorean(season, type, detail1);
            info1 = (TextView) findViewById(R.id.info1);

            info1.setText("시즌: "+results[0]);

            info2 = (TextView) findViewById(R.id.info2);
            info2.setText("타입: "+results[1]);

            info3 = (TextView) findViewById(R.id.info3);
            info3.setText("디테일1: "+results[2]);

            info4 = (TextView) findViewById(R.id.info4);
            info4.setText("태그: "+detail2);

            // 추가
            gestureScanner = new GestureDetector(this);
            mAppItem =  new ArrayList<ClothData>();
            getItemList();
        }


        // custom dialog 세팅하기
        dlg  = new ListSelectorDialog(this, "Select an Operator");
        // dialog에 맞는 key, value 세팅
        listk = new String[] {"a", "b", "c"};
        listv = new String[] {"태그달기","수정", "삭제"};

    }

    public void checkOrder(ArrayList<ClothData> mAppItem_){
        mAppItem = mAppItem_;
        ClothData data1 = info;
        int i=0;
        for(i=0; i<mAppItem.size(); i++){
            if(mAppItem.get(i).getInfo().equals(data1.getInfo())){
                break;
            }
        }
        Log.d("saea",  i+"위치");
        thisOrder = i;
    }

    public void changeCloth(ClothData info){
      //  new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user"+info.getUid()+"/"+info.getInfo());
        ContextWrapper cw = new ContextWrapper(ClothdetailActivity.this);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
        try {
            File f=new File(directory, info.getInfo());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
         //   img.setImageBitmap(b);
            Glide.with(ClothdetailActivity.this).load(f).into(img);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String[] results = englishToKorean(info.getSeason(), info.getType(), info.getDetail1());
        info1.setText("시즌: "+results[0]);
        info2.setText("타입: "+results[1]);
        info3.setText("디테일1: "+results[2]);
        info4.setText("태그: "+info.getDetail2());

    }

    // 태그달거나 수정 후 돌아왔을때 값 업데이트해서 보여주기
    @Override
    public void onResume() {
        super.onResume();

    }

    public void afterUpdate(ClothData data){

        String season = data.getSeason();
        String type = data.getType();
        String detail1 = data.getDetail1();
        String detail2 = data.getDetail2();
        ContextWrapper cw = new ContextWrapper(ClothdetailActivity.this);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
        try {
            File f=new File(directory, info.getInfo());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
         //   img.setImageBitmap(b);
            Glide.with(ClothdetailActivity.this).load(f).into(img);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
   // new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user"+data.getUid()+"/"+data.getInfo());
        String[] results = englishToKorean(season,  type, detail1);
        info1.setText("시즌: "+results[0]);
        info2.setText("타입: "+results[1]);
        info3.setText("디테일1: "+results[2]);
        info4.setText("태그: "+detail2);
        if(info_fromcodi == null){
            getItemList();
        }

    }


    // actionbar에 메뉴만들고 이벤트 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_one:
                // show the list dialog.
                dlg.show(listv, listk, new ListSelectorDialog.listSelectorInterface() {

                    // procedure if user cancels the dialog.
                    public void selectorCanceled() {
                    }
                    // procedure for when a user selects an item in the dialog.
                    public void selectedItem(String key, String item) {
                        if(item.equals("삭제")){
                           // Toast.makeText(ClothdetailActivity.this, "ddd", Toast.LENGTH_SHORT).show();
                           DialogSimple();
                        }else if(item.equals("수정")){
                            CustomDialog dialog = new CustomDialog(ClothdetailActivity.this, uid, info);
                            dialog.show();

                        }else if(item.equals("태그달기")) {
                            AddtagDialog dialog = new AddtagDialog(ClothdetailActivity.this, uid, info, "cloth", "cloth");
                            dialog.show();
                        }
                    }
                });
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
            //    return super.onOptionsItemSelected(item);
                if(info_fromcodi != null){
                    finish();
                }else{
                    Intent i = new Intent(ClothdetailActivity.this, SecondActivity.class);
                    i.putExtra("season", set1);
                    i.putExtra("type", set2);
                    i.putExtra("detail1", set3);
                    Log.d("saea", set1+set2+set3);
                    startActivity(i);
                }
                return true;
    }
    }

    // 삭제시 dialog
    public void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("이 옷을 삭제하시겠어요?").setCancelable(
                false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCloth(ClothdetailActivity.this, uid, info);
                        DBManager dbMgr = new DBManager(ClothdetailActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.deleteClothData(ClothDBSqlData.SQL_DB_DELETE_DATA ,info);
                        dbMgr.dbClose();

                    }
                }).setNegativeButton("삭제안함",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
          alert.setTitle("삭제 확인");
        alert.show();
    }


    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if(info_fromcodi == null) {
            return gestureScanner.onTouchEvent(me);
        }
        return false;
    }

    public boolean onDown(MotionEvent e) {
     //   viewA.setText("-" + "DOWN" + "-");
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                if(info_fromcodi == null){
                    Log.d("saea", thisOrder+"/"+mAppItem.size());
                    if(thisOrder < mAppItem.size()-1){
                        thisOrder = thisOrder+1;
                        clothdata = mAppItem.get(thisOrder);
                        info = clothdata;
                        Log.d("saea", clothdata.getInfo());
                        changeCloth(clothdata);
                    }

                }

            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(info_fromcodi == null){
                    if(mAppItem.size() >= thisOrder && thisOrder >= 1){
                        thisOrder = thisOrder-1;
                        clothdata = mAppItem.get(thisOrder);
                        info = clothdata;
                        Log.d("saea", clothdata.getInfo());
                        changeCloth(clothdata);
                    }

                }

            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            //    Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();

            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            //    Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
        return true;
    }

    public void onLongPress(MotionEvent e) {
    //    Toast mToast = Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT);
     //   mToast.show();
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
     ///   viewA.setText("-" + "SCROLL" + "-");
        return true;
    }

    public void onShowPress(MotionEvent e) {
       // viewA.setText("-" + "SHOW PRESS" + "-");
    }

    public boolean onSingleTapUp(MotionEvent e) {
     //   Toast mToast = Toast.makeText(getApplicationContext(), "Single Tap", Toast.LENGTH_SHORT);
     //   mToast.show();
        return true;
    }


    public void getItemList() {
        mAppItem.clear();

      Log.d("saea", "getitemlist : "+set1+set2+set3);

        String set1_en = null;
        String set2_en = null;
        String set3_en = null;

        if(set1.equals("계절무관")){
            set1_en = "all";
        }else if(set1.equals("봄,가을")){
            set1_en = "s,f";
        }else if(set1.equals("여름")){
            set1_en = "summer";
        }else if(set1.equals("겨울")){
            set1_en = "winter";
        }

        if(set2.equals("상의")){
            set2_en = "top";
        }else if(set2.equals("하의")){
            set2_en = "bottom";
        }else if(set2.equals("원피스")){
            set2_en = "dress";
        }else if(set2.equals("가방")){
            set2_en = "bag";
        }else if(set2.equals("신발")){
            set2_en = "shoes";
        }else if(set2.equals("악세서리")){
            set2_en = "acc";
        }else if(set2.equals("아우터")){
            set2_en = "outer";
        }

        if(set3 != null && !set3.equals("-")){
            if(set3.equals("외출복")){
                set3_en = "outdoor";
            }else if(set3.equals("실내복")){
                set3_en = "indoor";
            }
        }

        //set3가 있는 경우 반영해서 아이템 가져오기
        if(set3 != null && !set3.equals("-")){
            ClothData cData = new ClothData(uid, set1_en, set2_en, "", set3_en, "");
           // selectCloth(this, uid, cData, mAppItem);
            DBManager dbMgr = new DBManager(ClothdetailActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData("selectcloth", cData, mAppItem);
            dbMgr.dbClose();
            checkOrder(mAppItem);

        }else{

            ClothData cData = new ClothData(uid, set1_en, set2_en, "", "-", "");
          //  selectCloth(this, uid, cData, mAppItem);
            DBManager dbMgr = new DBManager(ClothdetailActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData("selectcloth", cData, mAppItem);
            dbMgr.dbClose();
            checkOrder(mAppItem);
        }
    }

    public void selectCloth(final Context context, final String uid, final ClothData cData, ArrayList<ClothData> mAppItem) {


        Log.i("saea", "Starting Upload...");
        String season = cData.getSeason();
        String type = cData.getType();
        String detail1 = cData.getDetail1();
        String detail2 = cData.getDetail2();
        selectCloth_Connect(context, uid, season, type, detail1, detail2, mAppItem);
        Log.d("js", "fourth"+String.valueOf(mAppItem.size()));

    }




    public void selectCloth_Connect(final Context context, final String uid, final String season, final String type, final String detail1, final String detail2, final ArrayList<ClothData> mAppItem) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CLOTHDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                ArrayList<ClothData> clothItems = new ArrayList<ClothData>();
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
                                ClothData clothdata = new ClothData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
                                clothItems.add(clothdata);
                            }
                            checkOrder(clothItems);

                        }else{
                            checkOrder(clothItems);
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
                Log.e("saea", "insert22: " + uid+season+type+detail1+detail2);
                params.put("status", "select_cloth");
                params.put("uid", uid);
                params.put("season", season);
                params.put("type", type);
                params.put("detail1", detail1);
                params.put("detail2", detail2);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void deleteCloth(final Context context, final String uid, final ClothData cData) {


                Log.i("saea", "Starting Upload...");
                String season = cData.getSeason();
                String type = cData.getType();
                String info = cData.getInfo();
                String detail1 = cData.getDetail1();
                String detail2 = cData.getDetail2();
                deleteCloth_Connect(context, uid, season, type, info, detail1, detail2);


    }

    public void deleteCloth_Connect(Context context, final String uid, final String season, final String type, final String info, final String detail1, final String detail2) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CLOTHDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"<- error status");
                    if (!error) {
                        if(info_fromcodi == null) {
                            Intent i = new Intent(ClothdetailActivity.this, SecondActivity.class);

                            i.putExtra("season", set1);
                            i.putExtra("type", set2);
                            i.putExtra("detail1", set3);
                            startActivity(i);
                        }else{
                           finish();
                        }


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
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "delete");
                params.put("uid", uid);
                params.put("season", season);
                params.put("type", type);
                params.put("info", info);
                params.put("detail1", detail1);
                params.put("detail2", detail2);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void selectClothbyInfo(final Context context, final String uid, final String info_fromcodi) {



        Log.i("saea", "Starting Upload...");

        selectClothbyInfo_Connect(context, uid, info_fromcodi);

    }




    public void selectClothbyInfo_Connect(final Context context, final String uid, final String info_fromcodi) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CLOTHDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");


                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject cloth = jObj.getJSONObject("cloth");

                        ClothData clothData = new ClothData(cloth.getString("uid"), cloth.getString("season"), cloth.getString("clothtype"), cloth.getString("info")
                        ,cloth.getString("detail1"), cloth.getString("detail2"));
                        info = clothData;
                        if(info.getInfo().contains("dress")){
                            final float scale = getResources().getDisplayMetrics().density;
                            int width  = (int) (200 * scale);
                            img.getLayoutParams().width = width;
                        }
                        //   Glide.with(this).load(imagepath).into(img);
                        new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user"+info.getUid()+"/"+info.getInfo());
                        int a = img.getLayoutParams().width;
                        int b = img.getLayoutParams().height;
                        Log.d("saea", a+"and"+b);

                        String[] results = englishToKorean(info.getSeason(), info.getType(), info.getDetail1());
                        info1 = (TextView) findViewById(R.id.info1);

                        info1.setText("시즌: "+results[0]);

                        info2 = (TextView) findViewById(R.id.info2);
                        info2.setText("타입: "+results[1]);

                        info3 = (TextView) findViewById(R.id.info3);
                        info3.setText("디테일1: "+results[2]);

                        info4 = (TextView) findViewById(R.id.info4);
                        info4.setText("태그: "+clothData.getDetail2());

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
                Log.e("saea", "insert22: " + uid+info_fromcodi);
                params.put("status", "selectbyInfo");
                params.put("uid", uid);
                params.put("info", info_fromcodi);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    public String[] englishToKorean(String set1_en, String set2_en, String set3_en){
        String set1_ko = null;
        String set2_ko = null;
        String set3_ko = null;
        if(set1_en.equals("all")){
            set1_ko = "계절무관";
        }else if(set1_en.equals("s,f")){
            set1_ko = "봄,가을";
        }else if(set1_en.equals("summer")){
            set1_ko = "여름";
        }else if(set1_en.equals("winter")){
            set1_ko = "겨울";
        }

        if(set2_en.equals("top")){
            set2_ko = "상의";
        }else if(set2_en.equals("bottom")){
            set2_ko = "하의";
        }else if(set2_en.equals("dress")){
            set2_ko = "원피스";
        }else if(set2_en.equals("bag")){
            set2_ko = "가방";
        }else if(set2_en.equals("shoes")){
            set2_ko = "신발";
        }else if(set2_en.equals("acc")){
            set2_ko = "악세서리";
        }else if(set2_en.equals("outer")){
            set2_ko = "아우터";
        }

        if(set3_en != null && !set3_en.equals("-")){
            if(set3_en.equals("outdoor")){
                set3_ko = "외출복";
            }else if(set3_en.equals("indoor")){
                set3_ko = "실내복";
            }
        }else{
            set3_ko = "-";
        }
        String[] results = {set1_ko, set2_ko, set3_ko};
        return results;
    }
}

