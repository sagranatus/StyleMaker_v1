package com.sagra.stylemaker_v1;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.adapter.ClothAdapter;
import com.sagra.stylemaker_v1.data.ClothData;
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

// 두번째 tab, 가진 옷 보기
public class SecondActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private ArrayList<ClothData> mAppItem = null;
    Spinner spinner2, spinner3;
    String set1, set2, set3;
    String first;
    ClothAdapter adapter;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);

        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // actionbar setting
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        TextView mytext = (TextView) findViewById(R.id.mytext);
        Fonttype.setFont( "Billabong",SecondActivity.this, mytext);

        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.list);

        first = "yes";
        //spinner 세팅하기
        Spinner spinner1 = (Spinner)this.findViewById(R.id.set1);
        spinner2 = (Spinner)this.findViewById(R.id.set2);
        spinner3 = (Spinner)this.findViewById(R.id.set3);
        spinner3.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        //  spinner2.setOnItemSelectedListener(spinSelectedlistener);
        spinner1.setOnItemSelectedListener(this);
        populateSpinners();
        Log.d("saea",set1+set2+set3);

        Intent intent = getIntent();
        // 계절 가져와서 spinner에 반영
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        String season = wPreference.getString("season", "");
        spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(season));

        String seasonFromdetail = intent.getStringExtra("season");
        String type = intent.getStringExtra("type");
        String detail1 = intent.getStringExtra("detail1");
        Log.d("saea", seasonFromdetail+type+detail1);
        if(type!=null){
            set1 = seasonFromdetail;
            set2 = type;
            set3 = detail1;
            spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(seasonFromdetail));
            spinner2.setSelection(((ArrayAdapter<String>)spinner2.getAdapter()).getPosition(type));
            spinner3.setSelection(((ArrayAdapter<String>)spinner3.getAdapter()).getPosition(detail1));

        }else{
            set1 = spinner1.getSelectedItem().toString();
            set2 = spinner2.getSelectedItem().toString();
            set3 = spinner3.getSelectedItem().toString();
        }



        // gridview에 옷 정보를 가져온다
        mAppItem =  new ArrayList<ClothData>();


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

        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_one:
                        Intent i = new Intent(SecondActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(SecondActivity.this, SecondActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(SecondActivity.this, ThirdActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(SecondActivity.this, FourthActivity.class);
                        startActivity(i4);
                        break;
                    case R.id.action_fifth:
                        Intent i5 = new Intent(SecondActivity.this, FifthActivity.class);
                        startActivity(i5);
                        break;
                }
                return false;
            }

        });

    }

    //actionbar 위에 버튼 넣고 select 이벤트 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_one:
                Intent i = new Intent(SecondActivity.this, AddclothActivity.class);
                startActivity(i);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    // spinner2,3 세팅
    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;

        SharedPreferences setPreference = getSharedPreferences("Setting", MODE_PRIVATE);
        String gender = setPreference.getString("gender", "");
        if(gender.equals("여자")){
            fAdapter = ArrayAdapter.createFromResource(this,
                    R.array.set2_array_women,
                    android.R.layout.simple_spinner_item);
        }else{
            fAdapter = ArrayAdapter.createFromResource(this,
                    R.array.set2_array,
                    android.R.layout.simple_spinner_item);
        }


        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(fAdapter);

        spinner3.setVisibility(View.VISIBLE);
        populateSubSpinners(R.array.set3_0);

    }

    private void populateSubSpinners(int itemNum) {
        Log.d("saea", "populateSubspinners");
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                itemNum,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(fAdapter);
    }
    // spinner2 선택시 spinner3 변경하기
    private AdapterView.OnItemSelectedListener spinSelectedlistener =
            new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    set3 = null;
                    //    String[] set2Array = getResources().getStringArray(R.array.set2_array);
                    SharedPreferences setPreference = getSharedPreferences("Setting", MODE_PRIVATE);
                    String gender = setPreference.getString("gender", "");
                    String[] set2Array;
                    if(gender.equals("여자")){
                        set2Array = getResources().getStringArray(R.array.set2_array_women);
                    }else{
                        set2Array = getResources().getStringArray(R.array.set2_array);
                    }

                    set2 = spinner2.getSelectedItem().toString();
                 //   getClothagain c = new getClothagain("thread");
              //      c.run();
                    String[] set3Array;
                    switch(position){
                        case (0):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.set3_0);
                            set3Array = getResources().getStringArray(R.array.set3_0);
                            set3 = set3Array[position];
                            break;
                        case (1):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.set3_0);
                            set3Array = getResources().getStringArray(R.array.set3_0);
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



    // griditem 선택시 이벤트
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long l_position) {
            // parent는 AdapterView의 속성의 모두 사용 할 수 있다.
            ClothData clothdata = (ClothData) parent.getAdapter().getItem(position);
           // String[] info = clothdata.getcDataArray();
            Intent i = new Intent(SecondActivity.this, ClothdetailActivity.class);
            i.putExtra("info", clothdata);
            i.putExtra("season", set1);
            i.putExtra("type", set2);
            i.putExtra("detail1", set3);
            Log.d("saea", set1+set2+set3);
            startActivity(i);

        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(null!=mAppItem)
        {
            mAppItem.clear();
        }
    }

    // grid에 아이템 가져오기
    public void getItemList() {
        mAppItem.clear();
     //   mAppItem.clear();
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
          //  selectCloth(this, uid, cData, mAppItem);

            DBManager dbMgr = new DBManager(SecondActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData("selectcloth", cData, mAppItem);
            dbMgr.dbClose();

        }else{
                ClothData cData = new ClothData(uid, set1_en, set2_en, "", "-", "");
         //   selectCloth(this, uid, cData, mAppItem);
            DBManager dbMgr = new DBManager(SecondActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData("selectcloth", cData, mAppItem);
            dbMgr.dbClose();

        }
        String saea;
        if(!mAppItem.isEmpty()){
            saea = mAppItem.get(0).getSeason();
        }else{
            saea="saea";
        }
        Log.d("saea", saea);
        int number = mAppItem.size() % 3;
        if(number == 2){
            mAppItem.add(new ClothData("", "", "", "", "", ""));
        }else if(number == 1){
            mAppItem.add(new ClothData("", "", "", "", "", ""));
            mAppItem.add(new ClothData("", "", "", "", "", ""));
        }
        GridView gridView = (GridView) findViewById(R.id.grid_main);
        adapter = new ClothAdapter(SecondActivity.this, R.layout.cloth_item_layout, mAppItem);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(mItemClickListener);

    }

    // spinner를 바꿀때마다 item 새로 불러오기
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d("saea", "itemselected");
        switch (parent.getId()) {
            case R.id.set1:
                Log.d("saea", "selected1");
                Spinner spinner = (Spinner) findViewById(R.id.set1);
                set1 = spinner.getSelectedItem().toString();
               // getClothagain c = new getClothagain("thread");
               // c.run();
                if(first == "no"){
                    getClothagain c = new getClothagain("thread");
                    c.run();
                }

                break;
            case R.id.set2:
                Log.d("saea", "selected2");
                Spinner spinner2 = (Spinner)findViewById(R.id.set2);
                set2 = spinner2.getSelectedItem().toString();
               // getClothagain c2 = new getClothagain("thread");
               // c2.run();
                   if(first == "no"){
                    getClothagain c = new getClothagain("thread");
                    c.run();
                }

                break;
            case R.id.set3:
                Log.d("saea", "selected3");
                Spinner spinner3 = (Spinner)findViewById(R.id.set3);
                set3 = spinner3.getSelectedItem().toString();
               // getClothagain c3 = new getClothagain("thread");
               // c3.run();
                first = "no";
                   if(first== "no"){
                    getClothagain c = new getClothagain("thread");
                    c.run();
                }
                break;

                }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public class getClothagain implements Runnable {
        String str;
        public getClothagain(String str){
            this.str = str;

        }

        public void run(){
            getItemList();
        }
    }


    public void selectClothAll(final Context context, final String uid, ArrayList<ClothData> mAppItem) {


        Log.i("saea", "Starting Upload...");
        selectCloth_Connect(context, uid, mAppItem);

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
                                ClothData clothdata = new ClothData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
                                String[] result = {arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]};
                                results.add(result);
                                clothItems.add(clothdata);
                                Log.d("saea", String.valueOf(clothItems.size()));
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

}