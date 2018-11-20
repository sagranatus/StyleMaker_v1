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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.DB.CodiDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.DailyDBSqlData;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.dialog.AddtagDialog;
import com.sagra.stylemaker_v1.dialog.ListSelectorDialog;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CodidetailActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, View.OnClickListener {
    ListSelectorDialog dlg;
    String[] listk, listv;
    CodiData info;
    String date, from;
    String number;
    String uid;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ArrayList<CodiData> mAppItem = null;
    private GestureDetector gestureScanner;
    String type0, type1;
    int thisOrder;
    CodiData codidata;
    ImageView img, img2, img3, img4, img5, img6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codidetail);

        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // actionbar setting
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("뒤로가기");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        // custom dialog setting
        dlg  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정하기
        listk = new String[] {"a", "b", "c","d", "e"};
        listv = new String[] {"스타일북만들기","태그달기", "코디함에 저장" ,"수정", "삭제"};

        // 인텐트 값 받아와서 보여주기
        Intent intent = getIntent();
        info = (CodiData) intent.getSerializableExtra("info");
        Log.d("saea", info.getUid()+info.getTop()+info.getBottom());
        Log.d("saea", info.getShoes()+info.getUid());
        // 인텐트 값 받아와서 보여주기
        Intent intent2 = getIntent();
        date = intent2.getStringExtra("date");
        number = info.getNumber();
        String season = info.getSeason();
        String type = info.getType();
        String top = info.getTop();
        String pants = info.getBottom();
        String shoes = info.getShoes();
        String outer = info.getOuter();
        String bag = info.getBag();
        String accessories = info.getAccessories();
        String tag = info.getTag();


       img = (ImageView) findViewById(R.id.imageView1);
       img2 = (ImageView) findViewById(R.id.imageView2);
       img3 = (ImageView) findViewById(R.id.imageView3);
       img4 = (ImageView) findViewById(R.id.imageView4);
       img5 = (ImageView) findViewById(R.id.imageView5);
       img6 = (ImageView) findViewById(R.id.imageView6);

        //    info = new String[]{uid, number, season, type, top, pants, shoes, outer, bag, accessories, tag};
        if(top.contains("dress")){
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
        ContextWrapper cw = new ContextWrapper(CodidetailActivity.this);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);

        if(!info.getTop().isEmpty()){

            // new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getTop());
            try {
                File f=new File(directory, info.getTop());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // img.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img);
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
              //  img2.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img2);
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
              //  img3.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img3);
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
               // img4.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img4);
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
               // img5.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img5);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!info.getAccessories().isEmpty()) {
            //   new DownloadImageTask(viewHolder.imageView6).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getAccessories());
            try {
                File f=new File(directory, info.getAccessories());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // img6.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img6);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        img.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);

        String[] results = englishToKorean(season, type);
        TextView info1 = (TextView) findViewById(R.id.info1);
        info1.setText("넘버: "+number);

        TextView info2 = (TextView) findViewById(R.id.info2);
        info2.setText("시즌: "+results[0]);

        TextView info3 = (TextView) findViewById(R.id.info3);
        info3.setText("타입: "+results[1]);

        TextView info4 = (TextView) findViewById(R.id.info4);
        info4.setText("태그: "+tag);

        // 추가


        gestureScanner = new GestureDetector(this);
        mAppItem =  new ArrayList<CodiData>();
        type0 = season;
        String type2 = null;
        if(type.contains("&")){
            int indexOf = type.indexOf("&");
            type1 = type.substring(0, indexOf);
            type2 =   type.substring(indexOf, type.length());
        }else{
            type1 = type;
        }
        type1 = type;

        if(date == null){
            getItemListbySeasonAndType(mAppItem, type0, type1, type2);

        }

    }

    public void afterUpdate(CodiData codidata) {
        info = codidata;
        if (info.getTop().contains("dress")) {
            final float scale = getResources().getDisplayMetrics().density;
            int height = (int) (300 * scale);
            img.getLayoutParams().height = height;
            img2.getLayoutParams().height = 0;
        } else {
            final float scale = getResources().getDisplayMetrics().density;
            int height = (int) (150 * scale);
            img.getLayoutParams().height = height;
            img2.getLayoutParams().height = height;
        }
        img.setImageBitmap(null);
        img2.setImageBitmap(null);
        img3.setImageBitmap(null);
        img4.setImageBitmap(null);
        img5.setImageBitmap(null);
        img6.setImageBitmap(null);


        ContextWrapper cw = new ContextWrapper(CodidetailActivity.this);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);

        if(!info.getTop().isEmpty()){

            // new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getTop());
            try {
                File f=new File(directory, info.getTop());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // img.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img);
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
               // img2.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img2);
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
              //  img3.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img3);
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
                Glide.with(CodidetailActivity.this).load(f).into(img4);
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
               // img5.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img5);

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!info.getAccessories().isEmpty()) {
            //   new DownloadImageTask(viewHolder.imageView6).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getAccessories());
            try {
                File f=new File(directory, info.getAccessories());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // img6.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img6);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        String[] results = englishToKorean(info.getSeason(), info.getType());
        TextView info1 = (TextView) findViewById(R.id.info1);
        info1.setText("넘버: " + number);

        TextView info2 = (TextView) findViewById(R.id.info2);
        info2.setText("시즌: " + results[0]);

        TextView info3 = (TextView) findViewById(R.id.info3);
        info3.setText("타입: " + results[1]);

        TextView info4 = (TextView) findViewById(R.id.info4);
        info4.setText("태그: " + info.getTag());

        mAppItem = new ArrayList<CodiData>();
        type0 = info.getSeason();
        String type2 = null;
        String type = info.getType();
        if (type.contains("&")) {
            int indexOf = type.indexOf("&");
            type1 = type.substring(0, indexOf);
            type2 = type.substring(indexOf, type.length());
        } else {
            type1 = type;
        }
        type1 = type;

        if (date == null) {
            getItemListbySeasonAndType(mAppItem, type0, type1, type2);

        }
    }

    public void checkOrder(ArrayList<CodiData> mAppItem_){
        mAppItem = mAppItem_;
        CodiData data1 = info;
        int i=0;
        for(i=0; i<mAppItem.size(); i++){
            if(mAppItem.get(i).getNumber().equals(data1.getNumber())){
                break;
            }
        }
        Log.d("saea",  i+"위치");
        thisOrder = i;
    }

    // 수정후에나 태그 단 후에 업데이트하기 위해 값 다시 가져오기
    @Override
    public void onResume() {
        super.onResume();
       }

    // actionbar 위에 버튼 생성 및 이벤트
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
                        if(item.equals("스타일북만들기")){
                            Intent i = new Intent(CodidetailActivity.this, AddstyleActivity.class);
                            i.putExtra("info", info);
                            i.putExtra("date", date);
                           // i.putExtra("from", from);
                            startActivity(i);
                        }else if(item.equals("코디함에 저장")){
                            if(date != null){
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                                String number = info.getNumber()+"_"+timeStamp;
                                ArrayList<CodiData> cDataList = new ArrayList<CodiData>();
                                DBManager dbMgr = new DBManager(CodidetailActivity.this);
                                dbMgr.dbOpen();
                                dbMgr.selectcodi(CodiDBSqlData.SQL_DB_SELECT_CODI, uid, number, cDataList);
                                dbMgr.dbClose();
                                if(cDataList.isEmpty()){
                                    CodiData cData = info;

                                    dbMgr.dbOpen();
                                    dbMgr.insertCodiData(CodiDBSqlData.SQL_DB_INSERT_DATA, cData);
                                    dbMgr.dbClose();
                                    Toast.makeText(CodidetailActivity.this, "코디함에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(CodidetailActivity.this, "이미 코디함에 담겨 있습니다.", Toast.LENGTH_SHORT).show();
                            }




                        }else if(item.equals("삭제")){
                            DialogSimple();

                        }else if(item.equals("수정")){
                            Intent i = new Intent(CodidetailActivity.this, AddcodiActivity.class);
                            if(date != null){
                                Log.d("saea", "codiupdate_date exist");
                                i.putExtra("codiupdate_date", date);
                            }
                            i.putExtra("codiupdate", "yes");
                            Log.d("saea", info.getNumber());
                            i.putExtra("info", info);
                            startActivity(i);
                        }else if(item.equals("태그달기")) {
                            AddtagDialog dialog = new AddtagDialog(CodidetailActivity.this, uid, info, "codi", date);
                            dialog.show();
                        }

                    }
                });
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
               if(date != null){
                    Intent i = new Intent(CodidetailActivity.this, FifthActivity.class);
                    startActivity(i);
                    return true;
                }else {
                    Intent i = new Intent(CodidetailActivity.this, ThirdActivity.class);
                    startActivity(i);
                    return true;
                }
        }
    }

    // 삭제 이벤트
    public void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("이 옷을 삭제하시겠어요?").setCancelable(
                false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                        //  파일 직접 삭제
                        if(date==null){
                            deleteCodi(CodidetailActivity.this, uid, info);

                        }else{
                            deleteDaily(CodidetailActivity.this, uid, date);

                        }



                    }
                }).setNegativeButton("삭제안함",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("삭제 확인");
        // Icon for AlertDialog
        //   alert.setIcon(R.drawable.icon);
        alert.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
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
                //  Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                if(date == null) {
                    if(thisOrder < mAppItem.size()-1){
                        thisOrder = thisOrder+1;
                    }
                    codidata = mAppItem.get(thisOrder);
                    Log.d("saea", codidata.getNumber());
                    changeCodi(codidata);
                }
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(date == null) {
                    if(thisOrder >= 1){
                        thisOrder = thisOrder-1;
                    }
                    codidata = mAppItem.get(thisOrder);
                    Log.d("saea", codidata.getNumber());

                    changeCodi(codidata);
                }
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //     Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();

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


    private void getItemListbySeasonAndType(ArrayList<CodiData> aCDataList, String season, String type1, String type2) {
        mAppItem.clear();
        Log.d("saea1", season+type1+type2);
        String type= null;
        if(type2 != null){
            type = type1+type2;
        }else{
            type = type1;
        }


        Log.d("saea", season+type);
       // selectCodi(CodidetailActivity.this, uid, season, type);
        DBManager dbMgr = new DBManager(CodidetailActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectCodiData(CodiDBSqlData.SQL_DB_SELECT_SEASON_TYPE, uid, season, type, mAppItem);
        dbMgr.dbClose();
        checkOrder(mAppItem);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(CodidetailActivity.this, ClothdetailActivity.class);
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

    public void changeCodi(CodiData codidata){
        info = codidata;
        //    info = new String[]{uid, number, season, type, top, pants, shoes, outer, bag, accessories, tag};
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
        img.setImageDrawable(null);
        img2.setImageDrawable(null);
        img3.setImageDrawable(null);
        img4.setImageDrawable(null);
        img5.setImageDrawable(null);
        img6.setImageDrawable(null);
        ContextWrapper cw = new ContextWrapper(CodidetailActivity.this);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);

        if(!info.getTop().isEmpty()){

            // new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getTop());
            try {
                File f=new File(directory, info.getTop());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // img.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img);
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
               // img2.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img2);
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
                Glide.with(CodidetailActivity.this).load(f).into(img3);
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
                Glide.with(CodidetailActivity.this).load(f).into(img4);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!info.getBag().isEmpty()) {
            Log.d("saea","bag is not empty");

            //   new DownloadImageTask(viewHolder.imageView5).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getBag());
            try {
                File f=new File(directory, info.getBag());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
              //  img5.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img5);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!info.getAccessories().isEmpty()) {
            //   new DownloadImageTask(viewHolder.imageView6).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getAccessories());
            try {
                File f=new File(directory, info.getAccessories());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                //img6.setImageBitmap(b);
                Glide.with(CodidetailActivity.this).load(f).into(img6);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        String[] results = englishToKorean(info.getSeason(), info.getType());
        TextView info1 = (TextView) findViewById(R.id.info1);
        info1.setText("넘버: "+info.getNumber());

        TextView info2 = (TextView) findViewById(R.id.info2);
        info2.setText("시즌: "+results[0]);

        TextView info3 = (TextView) findViewById(R.id.info3);
        info3.setText("타입: "+results[1]);

        TextView info4 = (TextView) findViewById(R.id.info4);
        info4.setText("태그: "+info.getTag());


    }

    public void selectCodi(final Context context, final String uid, String season, String type) {


        Log.i("saea", "Starting Upload...");

        selectCodi_Connect(context, uid, season, type);

    }


    public void selectCodi_Connect(final Context context, final String uid, final String season, final String type) {
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

                                codiItems.add(codidata);
                                Log.d("saea", String.valueOf(codiItems.size()));
                            }

                            checkOrder(codiItems);


                        }else{
                            checkOrder(codiItems);
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
                Log.e("saea", "select"+uid+season+type);
                params.put("status", "select");
                params.put("uid", uid);
                params.put("season", season);
                params.put("type", type);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void deleteCodi(final Context context, final String uid, final CodiData cData) {


        Log.i("saea", "Starting Upload...");
        String number = cData.getNumber();
        deleteCodi_Connect(context, uid, number);


    }

    public void deleteCodi_Connect(Context context, final String uid, final String number) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CODIDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"<- error status");
                    if (!error) {

                        DBManager dbMgr = new DBManager(CodidetailActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.deleteCodiData(CodiDBSqlData.SQL_DB_DELETE_DATA, uid, number);
                        dbMgr.dbClose();
                        Intent i = new Intent(CodidetailActivity.this, ThirdActivity.class);

                        i.putExtra("season", info.getSeason());
                        i.putExtra("type", info.getType());
                        startActivity(i);




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
                params.put("number", number);
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
                        DBManager dbMgr = new DBManager(CodidetailActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.deleteDailyData(DailyDBSqlData.SQL_DB_DELETE_DATA, uid, date);
                        dbMgr.dbClose();
                        Intent i = new Intent(CodidetailActivity.this, FifthActivity.class);
                        startActivity(i);

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

    public String[] englishToKorean(String season, String type){
        String season_ko = null;
        String type2_en= null;
        String type_en = null;
        String type_ko = null;
        if(season.equals("all")){
            season_ko = "계절무관";
        }else if(season.equals("s,f")){
            season_ko = "봄,가을";
        }else if(season.equals("summer")){
            season_ko = "여름";
        }else if(season.equals("winter")){
            season_ko = "겨울";
        }

        if(type.equals("normal")){
            type_ko = "평상복";
        }else if(type.equals("suit")){
            type_ko = "정장";
        }else if(type.equals("homeware")){
            type_ko = "홈웨어";
        }else if(type.equals("special")){
            type_ko = "특수";
            int indexOf = type.indexOf("&");
            type2_en =   type.substring(indexOf, type.length());
            if(type2_en.equals("wedding")){
                type_ko = type_ko+"-결혼식";
            }else if(type2_en.equals("sportsware")){
                type_ko = type_ko+"-운동복";
            }else if(type2_en.equals("picnic")){
                type_ko = type_ko+"-소풍";
            }
        }


        String[] results = {season_ko, type_ko};
        return results;
    }



}
