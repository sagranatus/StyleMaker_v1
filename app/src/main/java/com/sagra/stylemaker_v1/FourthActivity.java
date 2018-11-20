package com.sagra.stylemaker_v1;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.adapter.StyleAdapter;
import com.sagra.stylemaker_v1.data.StyleData;
import com.sagra.stylemaker_v1.etc.BottomNavigationViewHelper;
import com.sagra.stylemaker_v1.etc.Fonttype;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FourthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String uid;
    String set1;
    private ArrayList<StyleData> mAppItem = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // actionbar setting
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        TextView mytext = (TextView) findViewById(R.id.mytext);
        Fonttype.setFont( "Billabong",FourthActivity.this, mytext);

        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.list);


        Spinner spinner1 = (Spinner)this.findViewById(R.id.set1);
        spinner1.setOnItemSelectedListener(this);

        // 계절 가져와서 spinner에 반영
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        String season = wPreference.getString("season", "");
        spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(season));
        String seasonfroomdetail = getIntent().getStringExtra("season");
        if(seasonfroomdetail != null){
            Log.d("saea", "here"+seasonfroomdetail);
          // spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(seasonfroomdetail));
        }
        // 설정된 계절/옷종류를 가져오기 위해 값을 저장
        set1 = spinner1.getSelectedItem().toString();

        mAppItem =  new ArrayList<StyleData>();
        getStyle c = new getStyle(this);
        c.run();

        // bottomnavigationview 세팅
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
        }


        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        //  BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_one:
                        Intent i = new Intent(FourthActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(FourthActivity.this, SecondActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(FourthActivity.this, ThirdActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(FourthActivity.this, FourthActivity.class);
                        startActivity(i4);
                        break;
                    case R.id.action_fifth:
                        Intent i5 = new Intent(FourthActivity.this, FifthActivity.class);
                        startActivity(i5);
                        break;
                }
                return false;
            }

        });
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class getStyle implements Runnable {
        Context context;
        public getStyle(Context context){
            this.context = context;

        }

        public void run(){
            getItemList();

        }
    }

    // grid에 아이템 가져오기
    private void getItemList() {

        String set1_en = null;

        if(set1.equals("계절무관")){
            set1_en = "all";
        }else if(set1.equals("봄,가을")){
            set1_en = "s,f";
        }else if(set1.equals("여름")){
            set1_en = "summer";
        }else if(set1.equals("겨울")){
            set1_en = "winter";
        }
        selectStyle(FourthActivity.this, uid, set1_en);

    }

    // griditem 선택시 이벤트
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long l_position) {
            // parent는 AdapterView의 속성의 모두 사용 할 수 있다.
           StyleData styledata = (StyleData) parent.getAdapter().getItem(position);
            Intent i = new Intent(FourthActivity.this, StyledetailActivity.class);
            i.putExtra("info", styledata);
            startActivity(i);
        }
    };

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

                            GridView gridView = (GridView) findViewById(R.id.grid_main);
                            StyleAdapter adapter = new StyleAdapter(context, R.layout.style_item_layout, styleItems);
                            gridView.setAdapter(adapter);
                            gridView.setOnItemClickListener(mItemClickListener);

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
}
