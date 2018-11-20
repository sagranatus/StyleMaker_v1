package com.sagra.stylemaker_v1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.ClothdetailActivity;
import com.sagra.stylemaker_v1.DB.ClothDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CustomDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private static final int LAYOUT = R.layout.spinnerdialog;

    private Context context;

    private String uid;
    private ClothData info;

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomDialog(Context context,String uid, ClothData info){
        super(context);
        this.context = context;
        this.info = info;
        this.uid = uid;
    }
    static final int DLG_ID_INSERT = 0;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    Spinner spinner1, spinner2, spinner3;
    String set1, set2, set3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        spinner1 = (Spinner)this.findViewById(R.id.set1);
        spinner1.setOnItemSelectedListener(this);
        spinner2 = (Spinner)this.findViewById(R.id.set2);
        spinner3 = (Spinner)this.findViewById(R.id.set3);
        spinner3.setOnItemSelectedListener(this);

        String set1_en = info.getSeason();
        String set2_en =info.getType();
        String set3_en = info.getDetail1();

        if(set1_en.equals("all")){
            set1 = "계절무관";
        }else if(set1_en.equals("s,f")){
            set1 = "봄,가을";
        }else if(set1_en.equals("summer")){
            set1 = "여름";
        }else if(set1_en.equals("winter")){
            set1 = "겨울";
        }

        if(set2_en.equals("top")){
            set2 = "상의";
        }else if(set2_en.equals("bottom")){
            set2 = "하의";
        }else if(set2_en.equals("dress")){
            set2 = "원피스";
        }else if(set2_en.equals("bag")){
            set2 = "가방";
        }else if(set2_en.equals("shoes")){
            set2 = "신발";
        }else if(set2_en.equals("acc")){
            set2 = "악세서리";
        }else if(set2_en.equals("outer")){
            set2 = "아우터";
        }

        if(set3_en != null && !set3_en.equals("-")) {
            if (set3_en.equals("outdoor")) {
                set3 = "외출복";
            } else if (set3_en.equals("indoor")) {
                set3 = "실내복";
            }
        }
        spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(set1));


        populateSpinners();
      //  spinner2.setOnItemSelectedListener(spinSelectedlistener);

        //설정된 계절/옷종류를 가져오기 위해 값을 저장
        set1 = spinner1.getSelectedItem().toString();
        set2 = spinner2.getSelectedItem().toString();

        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(this);
    }

    // spinner2 세팅
    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;

        SharedPreferences setPreference = context.getSharedPreferences("Setting", MODE_PRIVATE);
        String gender = setPreference.getString("gender", "");
        if(gender.equals("여자")){
            fAdapter = ArrayAdapter.createFromResource(context,
                    R.array.set2_array_women,
                    android.R.layout.simple_spinner_item);
        }else{
            fAdapter = ArrayAdapter.createFromResource(context,
                    R.array.set2_array,
                    android.R.layout.simple_spinner_item);
        }


        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(fAdapter);
        spinner2.setSelection(((ArrayAdapter<String>)spinner2.getAdapter()).getPosition(set2));

        spinner3.setVisibility(View.VISIBLE);
        populateSubSpinners(R.array.set3_0);
    }

    private void populateSubSpinners(int itemNum) {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(context,
                itemNum,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(fAdapter);
        spinner3.setSelection(((ArrayAdapter<String>)spinner3.getAdapter()).getPosition(set3));
    }

    // spinner2 선택시 spinner3 변경하기
    private AdapterView.OnItemSelectedListener spinSelectedlistener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    set3 = null;
                    //    String[] set2Array = getResources().getStringArray(R.array.set2_array);
                    SharedPreferences setPreference = context.getSharedPreferences("Setting", MODE_PRIVATE);
                    String gender = setPreference.getString("gender", "");
                    String[] set2Array;
                    if(gender.equals("여자")){
                        set2Array = context.getResources().getStringArray(R.array.set2_array_women);
                    }else{
                        set2Array = context.getResources().getStringArray(R.array.set2_array);
                    }

                    set2 = spinner2.getSelectedItem().toString();

                    String[] set3Array;
                    switch(position){
                        case (0):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.set3_0);
                            set3Array = context.getResources().getStringArray(R.array.set3_0);
                            set3 = set3Array[position];
                            break;
                        case (1):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.set3_0);
                            set3Array = context.getResources().getStringArray(R.array.set3_0);
                            set3 = set3Array[position];
                            break;
                        default:
                            spinner3.setVisibility(View.GONE);
                            set3 = null;
                            break;

                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update:
                set1 = spinner1.getSelectedItem().toString();
                set2 = spinner2.getSelectedItem().toString();
                set3 = spinner3.getSelectedItem().toString();
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
                }else{
                    set3_en = "-";
                }
              //  Toast.makeText(context, set1+set2+set3, Toast.LENGTH_SHORT).show();
                ClothData clothData = new ClothData(info.getUid(), set1_en, set2_en, info.getInfo(), set3_en, info.getDetail2());
                updateCloth(context, uid, clothData);


                //DBManager dbMgr = new DBManager(context);
                //dbMgr.dbOpen();

               // String[] insert = {set1, set2, set3, uid, info.getInfo()};
             //   dbMgr.updateClothData(ClothDBSqlData.SQL_DB_UPDATE_DATA, insert);
              //  dbMgr.dbClose();
              //  ((ClothdetailActivity) context).onResume();
            //    cancel();
                break;
            case R.id.set2:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.set1:
                Spinner spinner = (Spinner) findViewById(R.id.set1);
                set1 = spinner.getSelectedItem().toString();
                break;
            case R.id.set3:
                Spinner spinner3 = (Spinner)findViewById(R.id.set3);
                set3 = spinner3.getSelectedItem().toString();
                break;
    }

}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
    }
