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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.DB.CodiDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.DailyDBSqlData;
import com.sagra.stylemaker_v1.adapter.CodiAdapter;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.etc.BottomNavigationViewHelper;
import com.sagra.stylemaker_v1.etc.Fonttype;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.Server_DailyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 세번째 tab, 가진 코디 보기
public class ThirdActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<CodiData> mAppItem = null;
    CodiAdapter adapter;
    String uid;
    String first;

    Spinner spinner0, spinner1, spinner2;
    String type0,type1,type2;
    String date = null;
    String from;
    String frommain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();
        first = "yes";

        // spinner 세팅하기
        spinner0 = (Spinner) this.findViewById(R.id.type0);
        spinner0.setOnItemSelectedListener(this);

        spinner1 = (Spinner) this.findViewById(R.id.type1);
        spinner1.setOnItemSelectedListener(spinSelectedlistener);
        populateSpinners();
        spinner2 = (Spinner) this.findViewById(R.id.type2);
        spinner2.setOnItemSelectedListener(this);

        // 계절 정보 가져와서 spinner에 보이기
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        String season = wPreference.getString("season", "");
        spinner0.setSelection(((ArrayAdapter<String>)spinner0.getAdapter()).getPosition(season));
        type0 = season;
        type1 = "평상복";

        Intent intent = getIntent();
        String season_from = intent.getStringExtra("season");
        if(season_from != null){
            String type_from = intent.getStringExtra("type");
            Log.d("saea", season_from + type_from);
           // spinner0.setSelection(((ArrayAdapter<String>)spinner0.getAdapter()).getPosition(season_from));
           // spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(type_from));
        }

        // bottomnavigationview 세팅
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
        }

        MenuItem menuItem = menu.getItem(2 );
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_one:
                        Intent i = new Intent(ThirdActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(ThirdActivity.this, SecondActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(ThirdActivity.this, ThirdActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(ThirdActivity.this, FourthActivity.class);
                        startActivity(i4);
                        break;
                    case R.id.action_fifth:
                        Intent i5 = new Intent(ThirdActivity.this, FifthActivity.class);
                        startActivity(i5);
                        break;
                }
                return false;
            }

        });

         // 인텐트를 가져온다. from calendarActivity / firstActivity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                date= null;
            } else {
                date= extras.getString("date");
                frommain =  extras.getString("frommain");
            }
        } else {
            date= (String) savedInstanceState.getSerializable("date");
            frommain =  (String) savedInstanceState.getSerializable("frommain");

        }

        // 인텐트가 없는 경우에 actionbar setting
        if(date == null){
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            TextView mytext = (TextView) findViewById(R.id.mytext);
            Fonttype.setFont( "Billabong",ThirdActivity.this, mytext);

            //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.list);
        }

        // 인텐트가 있는 경우에 actionbar setting 과 nivagationview 안보이게 설정
        if(date != null){
            bottomNavigationView.setVisibility(View.INVISIBLE);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            //from calendarActivity / firstActivity 시에 actionbar 수정 필요
            actionBar = getSupportActionBar();
            //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            setTitle("뒤로가기");
        }

        // 코디 가져오기
        getCodi c = new getCodi(this);
        c.run();

    }

    // actionbar 메뉴에 버튼 추가 및 클릭이벤트 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(date == null ) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.topmenu_third, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_one:
                Intent i = new Intent(ThirdActivity.this, AddcodiActivity.class);
                startActivity(i);
                return true;
            default:
                if(date == null) {
                    Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }else{
                    finish();
                    return true;
                }
        }
    }


    // 각 아이템 클릭시 이벤트
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long l_position) {

            CodiData codiData = (CodiData) parent.getAdapter().getItem(position);
            // intent가 있는 경우는 dailycodi정보를 입력 혹은 수정한다
            if(date != null){
                CodiData cData = new CodiData(codiData.getUid(), date, codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(), codiData.getShoes(),
                        codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag());

                selectDailybyInfo(ThirdActivity.this, uid, date, cData); // 있는지 없는 확인한 뒤에 insert 혹은 update 한다.

            }else{
                // intent가 없는 경우는 detail정보를 보러간다
                Intent i = new Intent(ThirdActivity.this, CodidetailActivity.class);
                i.putExtra("info", codiData);
                startActivity(i);
            }


        }
    };


    // dailyDB 조정하기
    private void insertDailyDB(CodiData codiData) {

        CodiData cData = new CodiData(uid, date, codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(), codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag());

        Server_DailyData.insertDaily(ThirdActivity.this, uid, cData);
        DBManager dbMgr = new DBManager(ThirdActivity.this);
        dbMgr.dbOpen();
        dbMgr.insertDailyData(DailyDBSqlData.SQL_DB_INSERT_DATA, cData);
        dbMgr.dbClose();
        if(frommain != null){
            Intent i = new Intent(ThirdActivity.this, MainActivity.class);
            startActivity(i);
        }else{
            Intent i = new Intent(ThirdActivity.this, FifthActivity.class);
            startActivity(i);
        }
    }

    private void updateDailyDB(CodiData codiData) {
            updateDaily(ThirdActivity.this, uid, codiData);
        Log.d("saea","this"+uid+date+codiData.getNumber()+codiData.getSeason()+codiData.getType());

    }


    // 코디정보 grid item으로 가져오기
    private void getItemListbySeasonAndType(ArrayList<CodiData> aCDataList, String season, String type1, String type2) {
        mAppItem.clear();
        Log.d("saea", season+type1+type2);

        String type1_en = null;
        if(type1.equals("평상복")){
            type1_en = "normal";
        }else if(type1.equals("정장")){
            type1_en = "suit";
        }else if(type1.equals("홈웨어")){
            type1_en = "homeware";
        }else if(type1.equals("특수")){
            type1_en = "special";
        }
        String type_en = null;
        String type2_en = null;

        if(type2 != null){
            if(type2.equals("결혼식")){
                type2_en = "wedding";
            }else if(type2.equals("운동복")){
                type2_en = "sportsware";
            }else if(type2.equals("소풍")){
                type2_en = "picnic";
            }
            type_en = type1_en+"&"+type2_en;
        }else{
            type_en = type1_en;
        }
        //  Toast.makeText(this, numberStr+seasonStr+typeStr, Toast.LENGTH_SHORT).show();

        String season_en= null;
        if( season.equals("계절무관")){
            season_en = "all";
        }else if( season.equals("봄,가을")){
            season_en = "s,f";
        }else if( season.equals("여름")){
            season_en = "summer";
        }else if( season.equals("겨울")){
            season_en = "winter";
        }

      //  selectCodi(ThirdActivity.this, uid, season_en, type_en);
        DBManager dbMgr = new DBManager(ThirdActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectCodiData(CodiDBSqlData.SQL_DB_SELECT_SEASON_TYPE, uid, season_en, type_en, mAppItem);
        dbMgr.dbClose();

        GridView gridView = (GridView) findViewById(R.id.grid_codi);
        adapter = new CodiAdapter(ThirdActivity.this, R.layout.codi_item_layout, mAppItem, false);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(mItemClickListener);
    }


    // spinner 값 변경시 다른 코디 가져오기
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.type0:

                Spinner spinner = (Spinner) this.findViewById(R.id.type0);
                type0 = spinner.getSelectedItem().toString();
                Spinner spinner1 = (Spinner) this.findViewById(R.id.type1);
                type1 = spinner1.getSelectedItem().toString();
                //Toast.makeText(this, type0+type1+type2, Toast.LENGTH_SHORT).show();

                if(first == "no"){
                    getCodiagain c = new getCodiagain("thread");
                    c.run();
                }
                break;
            case R.id.type2:
                Spinner spinner2 = (Spinner) findViewById(R.id.type2);
                type2 = spinner2.getSelectedItem().toString();
                // Toast.makeText(this, type1 + type2, Toast.LENGTH_SHORT).show();
                if(first == "no"){
                    getCodiagain c = new getCodiagain("thread");
                    c.run();
                }
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //spinner 2,3 관련된 코드
    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                R.array.type1_array,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(fAdapter);
    }

    private void populateSubSpinners(int itemNum) {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                itemNum,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(fAdapter);
    }

    private AdapterView.OnItemSelectedListener spinSelectedlistener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String[] type1Array = getResources().getStringArray(R.array.type1_array);
                    type1 = type1Array[position];
                    // spinner1가 평상복, 정장, 등일 경우에는 spinner2가 안보이고 특수인 경우에만 보인다
                    switch (position) {
                        case (0):
                            spinner2.setVisibility(View.GONE);
                            type2 = null;
                            break;
                        case (1):
                            spinner2.setVisibility(View.GONE);
                            type2 = null;
                            break;
                        case (2):
                            spinner2.setVisibility(View.GONE);
                            type2 = null;
                            break;

                        case (3):
                            spinner2.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.type2_array);
                            type2 = spinner2.getSelectedItem().toString();

                            break;
                    }
                    if(first == "no" && type2 == null){
                        getCodiagain c = new getCodiagain("thread");
                        c.run();
                    }
                    first = "no";
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    public class getCodi implements Runnable {
        Context context;
        public getCodi(Context context){
            this.context = context;

        }

        public void run(){
            mAppItem =  new ArrayList<CodiData>();
            getItemListbySeasonAndType(mAppItem, type0, type1, null);
        }
    }

    public class getCodiagain implements Runnable {
        String str;
        public getCodiagain(String str){
            this.str = str;

        }

        public void run(){
            mAppItem.clear();
            if(type2 != null){
                getItemListbySeasonAndType(mAppItem,type0, type1, type2);
            }else{
                getItemListbySeasonAndType(mAppItem,type0, type1, null);
            }
        }
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

    public void selectDailybyInfo(final Context context, final String uid, final String date, final CodiData cData) {

        Log.i("saea", "Starting Upload...");
        selectDailybyInfo_Connect(context, uid, date, cData);
    }




    public void selectDailybyInfo_Connect(final Context context, final String uid, final String date, final CodiData cData) {
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

                        updateDailyDB(cData);


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        insertDailyDB(cData);
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
                params.put("status", "selectbyInfo");
                params.put("uid", uid);
                params.put("date", date);
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
                        DBManager dbMgr = new DBManager(ThirdActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.updateDailyData(DailyDBSqlData.SQL_DB_UPDATE_DATE, new String[]{codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag(), codiData.getUid(), codiData.getNumber()});
                        dbMgr.dbClose();
                        if(frommain != null){
                            Intent i = new Intent(ThirdActivity.this, MainActivity.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(ThirdActivity.this, FifthActivity.class);
                            startActivity(i);
                        }

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
                Log.d("saea", "update: "+codiData.getNumber()+codiData.getTop());
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
