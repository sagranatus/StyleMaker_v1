package com.sagra.stylemaker_v1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.data.StyleData;
import com.sagra.stylemaker_v1.dialog.ListSelectorDialog;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StyledetailActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
    String uid;
    TextView info1, info2, info3;
    ListSelectorDialog dlg;
    String[] listk, listv;
    StyleData info;
    ImageView img;
    private ArrayList<StyleData> mAppItem = null;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureScanner;
    String set1;
    int thisOrder;
    StyleData styledata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styledetail);
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
        info = (StyleData) intent.getSerializableExtra("info");

        String season = null;
        String titletag;
        String clothtags;

        season = info.getSeason();
        titletag = info.getCoditag();
        clothtags = info.getClothtag();

        set1 = season;

        img = (ImageView) findViewById(R.id.styleimg);

        new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getNumber()+".png");
        info1 = (TextView) findViewById(R.id.info1);
        info1.setText("시즌: "+season);

        info2 = (TextView) findViewById(R.id.info2);
        info2.setText("타이틀: "+titletag);

        info3 = (TextView) findViewById(R.id.info3);
        info3.setText("옷태그: "+clothtags);


        // custom dialog setting
        dlg  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정하기
        listk = new String[] {"a", "b", "c"};
        listv = new String[] {"인스타그램보내기","갤러리에저장", "삭제"};


        gestureScanner = new GestureDetector(this);
        mAppItem =  new ArrayList<StyleData>();
        getItemList();


    }

    public void checkOrder(ArrayList<StyleData> mAppItem_){
        mAppItem = mAppItem_;
        StyleData data1 = info;
        int i=0;
        for(i=0; i<mAppItem.size(); i++){
            if(mAppItem.get(i).getNumber().equals(data1.getNumber())){
                break;
            }
        }
        Log.d("saea",  i+"위치");
        thisOrder = i;
    }

    public void changeStyle(StyleData info){
        String season = info.getSeason();
        String titletag = info.getCoditag();
        String clothtags = info.getClothtag();

        new DownloadImageTask(img).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getNumber()+".png");
        info1 = (TextView) findViewById(R.id.info1);
        info1.setText("시즌: "+season);

        info2 = (TextView) findViewById(R.id.info2);
        info2.setText("타이틀: "+titletag);

        info3 = (TextView) findViewById(R.id.info3);
        info3.setText("옷태그: "+clothtags);
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
                        if(item.equals("인스타그램보내기")){

                          //  String filePlace = Environment.getExternalStoragePublicDirectory(
                                //    Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker" + File.separator + info.getNumber()+".jpg";
                         //   Log.d("saea", filePlace);
                          //  Bitmap bitmap = BitmapFactory.decodeFile(filePlace);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(StyledetailActivity.this.getContentResolver(), bitmap, "Title", null);
                           Uri uri =  Uri.parse(path);

                          //  File f = new File(filePlace);
                         //   Uri uri = Uri.fromFile(f);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,"YOUR TEXT TO SHARE IN INSTAGRAM");
                            shareIntent.setPackage("com.instagram.android");
                            startActivity(shareIntent);

                            Toast.makeText(StyledetailActivity.this, "인스타그램에 보내기!!", Toast.LENGTH_SHORT).show();
                        }else if(item.equals("갤러리에저장")){
                            Toast.makeText(StyledetailActivity.this, "갤러리에 저장!!", Toast.LENGTH_SHORT).show();

                        }else if(item.equals("삭제")){
                            DialogSimple();

                        }
                    }
                });
                return true;
            default:
                Intent i = new Intent(StyledetailActivity.this, FourthActivity.class);
                startActivity(i);
                return true;

        }
    }


    // 삭제 이벤트
    public void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("이 스타일북을 삭제하시겠어요?").setCancelable(
                false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            deleteStyle(StyledetailActivity.this, uid, info);
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

                if(thisOrder < mAppItem.size()-1){
                    thisOrder = thisOrder+1;
                }
                styledata = mAppItem.get(thisOrder);
                Log.d("saea", styledata.getNumber());

               changeStyle(styledata);

            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                if(thisOrder >= 1){
                    thisOrder = thisOrder-1;
                }
                styledata = mAppItem.get(thisOrder);
                Log.d("saea", styledata.getNumber());
                changeStyle(styledata);

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

    // grid에 아이템 가져오기
    private void getItemList() {
        //set3가 있는 경우 반영해서 아이템 가져오기

        selectStyle(StyledetailActivity.this, uid, info.getSeason());

    }

    public void selectStyle(final Context context, final String uid, String season) {


        Log.i("saea", "Starting Upload...");

        selectStyle_Connect(context, uid, season);

    }


    public void selectStyle_Connect(final Context context, final String uid, final String season) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_STYLEDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                ArrayList<String[]> results = new ArrayList<String[]>();
                ArrayList<StyleData> styleItems = new ArrayList<StyleData>();
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
                                StyleData styledata = new StyleData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);

                                styleItems.add(styledata);
                                Log.d("saea", String.valueOf(styleItems.size()));
                            }

                            checkOrder(styleItems);

                        }else{
                            checkOrder(styleItems);
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
                Log.e("saea", "select"+uid+season);
                params.put("status", "select");
                params.put("uid", uid);
                params.put("season", season);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void deleteStyle(final Context context, final String uid, final StyleData sData) {


        Log.i("saea", "Starting Upload...");
        String number = sData.getNumber();
        deleteStyle_Connect(context, uid, number);


    }

    public void deleteStyle_Connect(Context context, final String uid, final String number) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_STYLEDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"<- error status");
                    if (!error) {

                        Intent i = new Intent(StyledetailActivity.this, FourthActivity.class);
                        i.putExtra("season", info.getSeason());
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
}
