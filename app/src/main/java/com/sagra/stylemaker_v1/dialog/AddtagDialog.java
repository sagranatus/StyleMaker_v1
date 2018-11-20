package com.sagra.stylemaker_v1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.AddstyleActivity;
import com.sagra.stylemaker_v1.ClothdetailActivity;
import com.sagra.stylemaker_v1.CodidetailActivity;
import com.sagra.stylemaker_v1.DB.ClothDBSqlData;
import com.sagra.stylemaker_v1.DB.CodiDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.DailyDBSqlData;
import com.sagra.stylemaker_v1.MainActivity;
import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddtagDialog extends Dialog implements View.OnClickListener {
    private static final int LAYOUT = R.layout.spinnerdialog;

    private Context context;

    String content;
    EditText tag;
    String detailtype;
    String date;
    String uid;
    private ClothData info;
    private CodiData codiData;

    public AddtagDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public AddtagDialog(Context context, String uid, ClothData info, String detailtype, String date) {
        super(context);
        this.context = context;
        this.info = info;
        this.detailtype = detailtype;
        this.date = date;
        this.uid = uid;
    }

    public AddtagDialog(Context context, String uid, CodiData codiData, String detailtype, String date) {
        super(context);
        this.context = context;
        this.codiData = codiData;
        this.detailtype = detailtype;
        this.date = date;
        this.uid = uid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtagdialog);


        tag = (EditText) findViewById(R.id.tag);
        TextView tv = (TextView) findViewById(R.id.tv);
        if(detailtype.equals("cloth")){
            tag.setText(info.getDetail2());
        }else{
            tv.setText("코디에 맞는 스타일 태그를 달아보세요.");
            tag.setHint("#청청 #복고풍 #세미정장 #댄디");
            tag.setText(codiData.getTag());
         //   tag.setText(info[10]);

        }

        tag.setSelection(tag.getText().length());
        ImageButton addtag = (ImageButton) findViewById(R.id.addtag);

        addtag.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtag:
                content = tag.getText().toString();
             //   Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
                if(detailtype.equals("cloth")){
                  /*  DBManager dbMgr = new DBManager(context);
                    dbMgr.dbOpen();
                    String[] insert = {content, uid, info.getInfo()};
                    dbMgr.updateClothData(ClothDBSqlData.SQL_DB_UPDATE_DATA_DETAIL2, insert);
                    dbMgr.dbClose(); */
                  ClothData clothData = new ClothData(info.getUid(), info.getSeason(), info.getType(), info.getInfo(), info.getDetail1(), content);
                  updateCloth(context, uid, clothData);

                }else if(detailtype.equals("codi")){
                    if(date != null){
                      /*  DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        String[] insert = {content, uid, info[1]};
                        dbMgr.updateData(DaliyDBSqlData.SQL_DB_UPDATE_DATA_TAG, insert);
                        dbMgr.dbClose(); */
                        CodiData cData = new CodiData(codiData.getUid(), codiData.getNumber(), codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), content);
                        updateDaily(context, uid, cData);
                    }else{
                     /*    DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        String[] insert = {content, uid, info[1]};
                        dbMgr.updateData(CodiDBSqlData.SQL_DB_UPDATE_DATA_TAG, insert);
                        dbMgr.dbClose(); */
                        CodiData cData = new CodiData(codiData.getUid(), codiData.getNumber(), codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), content);
                        updateCodi(context, uid, cData);
                    }

                    ((CodidetailActivity) context).onResume();
                }else if(detailtype.equals("main")){
                    if(date != null){
                       /*   DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        String[] insert = {content, uid, info[1]};
                        dbMgr.updateData(DaliyDBSqlData.SQL_DB_UPDATE_DATA_TAG, insert);
                        dbMgr.dbClose();*/
                        CodiData cData = new CodiData(codiData.getUid(), codiData.getNumber(), codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), content);
                        updateDaily(context, uid, cData);
                    }else{

                        /*    DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        String[] insert = {content, uid, info[1]};
                        dbMgr.updateData(CodiDBSqlData.SQL_DB_UPDATE_DATA_TAG, insert);
                        dbMgr.dbClose();*/
                    }


                }else{
                    if(date != null){
                       /*  DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        String[] insert = {content, uid, info[1]};
                        dbMgr.updateData(DaliyDBSqlData.SQL_DB_UPDATE_DATA_TAG, insert);
                        dbMgr.dbClose(); */
                        CodiData cData = new CodiData(codiData.getUid(), codiData.getNumber(), codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), content);
                        updateDaily(context, uid, cData);
                    }else{
                        CodiData cData = new CodiData(codiData.getUid(), codiData.getNumber(), codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), content);
                        updateCodi(context, uid, cData);
                       /*  DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        String[] insert = {content, uid, info[1]};
                        dbMgr.updateData(CodiDBSqlData.SQL_DB_UPDATE_DATA_TAG, insert);
                        dbMgr.dbClose(); */

                    }


                }

                cancel();
                break;
        }
    }

    public void updateCloth(final Context context, final String uid, final ClothData cData) {


        Log.i("saea", "Starting Upload...");
        String season = cData.getSeason();
        String type = cData.getType();
        String info = cData.getInfo();
        String detail1 = cData.getDetail1();
        String detail2 = cData.getDetail2();
        updateCloth_Connect(context, uid, season, type, info, detail1, detail2);


    }


    public void updateCloth_Connect(final Context context, final String uid, final String season, final String type, final String info, final String detail1, final String detail2) {
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
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject cloth = jObj.getJSONObject("cloth");

                        ClothData clothdata = new ClothData(cloth.getString("uid"), cloth.getString("season"),
                                cloth.getString("clothtype"), cloth.getString("info"), cloth.getString("detail1"), cloth.getString("detail2"));
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.updateClothData(ClothDBSqlData.SQL_DB_UPDATE_DATA, new String[]{clothdata.getSeason(), clothdata.getType(), clothdata.getDetail1(),
                                clothdata.getDetail2(), clothdata.getUid(), clothdata.getInfo()});
                        dbMgr.dbClose();
                        ((ClothdetailActivity) context).afterUpdate(clothdata);
                        cancel();

                      /*)  JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String gender = user.getString("gender");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다
    */
                        Log.d("saea", "resulttoday:"+ uid);


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
                params.put("status", "update");
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


    public void updateCodi(final Context context, final String uid, final CodiData cData) {


        Log.i("saea", "Starting Upload...");
        updateCodi_Connect(context, uid, cData);


    }


    public void updateCodi_Connect(final Context context, final String uid, final CodiData codiData) {
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
                    Log.d("saea", error+"saea");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject codi = jObj.getJSONObject("codi");

                        CodiData codiData = new CodiData(codi.getString("uid"), codi.getString("numberval"), codi.getString("season"),
                                codi.getString("type"), codi.getString("top"), codi.getString("bottom"),
                                codi.getString("shoes"), codi.getString("outdoor"),codi.getString("bag"),codi.getString("acc"), codi.getString("tag"));
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.updateCodiData(CodiDBSqlData.SQL_DB_UPDATE_DATA, new String[]{codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag(), codiData.getUid(), codiData.getNumber()});
                        dbMgr.dbClose();
                        if(detailtype.equals("codi")){
                            ((CodidetailActivity) context).afterUpdate(codiData);
                            cancel();

                        }else if(detailtype.equals("style")){
                            ((AddstyleActivity) context).afterAddTag(codiData);
                        }

                      /*)  JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String gender = user.getString("gender");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다
    */
                        Log.d("saea", "resulttoday:"+ uid);


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
                Log.d("saea", codiData.getNumber()+codiData.getTop());
                params.put("status", "update");
                params.put("uid", uid);
                params.put("season", codiData.getSeason());
                params.put("number", codiData.getNumber());
                params.put("type", codiData.getType());
                params.put("top", codiData.getTop());
                params.put("bottom", codiData.getBottom());
                params.put("shoes", codiData.getShoes());
                params.put("outer", codiData.getOuter());
                params.put("bag", codiData.getBag());
                params.put("acc", codiData.getAccessories());
                params.put("tag", codiData.getTag());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void updateDaily(final Context context, final String uid, final CodiData cData) {


        Log.i("saea", "Starting Upload...");
        updateDaily_Connect(context, uid, cData);


    }


    public void updateDaily_Connect(final Context context, final String uid, final CodiData codiData) {
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
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject codi = jObj.getJSONObject("daily");

                        CodiData codiData = new CodiData(codi.getString("uid"), codi.getString("dateval"), codi.getString("season"),
                                codi.getString("type"), codi.getString("top"), codi.getString("bottom"),
                                codi.getString("shoes"), codi.getString("outdoor"),codi.getString("bag"),codi.getString("acc"), codi.getString("tag"));

                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.updateDailyData(DailyDBSqlData.SQL_DB_UPDATE_DATE, new String[]{codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag(), codiData.getUid(), codiData.getNumber()});
                        dbMgr.dbClose();

                        if(detailtype.equals("codi")){
                            ((CodidetailActivity) context).afterUpdate(codiData);
                            cancel();

                        }else if(detailtype.equals("style")){
                            ((AddstyleActivity) context).afterAddTag(codiData);
                        }else if(detailtype.equals("main")){

                            ((MainActivity) context).afterAddTag(codiData);
                        }
                        //   Intent i = new Intent(AddcodiActivity.this, CodidetailActivity.class);
                        //   i.putExtra("info", codiData );
                        //   startActivity(i);

                      /*)  JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String gender = user.getString("gender");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다
    */
                        Log.d("saea", "resulttoday:"+ uid);


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
                Log.d("saea", codiData.getNumber()+codiData.getTop());
                params.put("status", "update");
                params.put("uid", uid);
                params.put("season", codiData.getSeason());
                params.put("date", codiData.getNumber());
                params.put("type", codiData.getType());
                params.put("top", codiData.getTop());
                params.put("bottom", codiData.getBottom());
                params.put("shoes", codiData.getShoes());
                params.put("outer", codiData.getOuter());
                params.put("bag", codiData.getBag());
                params.put("acc", codiData.getAccessories());
                params.put("tag", codiData.getTag());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}