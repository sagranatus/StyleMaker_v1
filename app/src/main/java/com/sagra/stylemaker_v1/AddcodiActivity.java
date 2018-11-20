package com.sagra.stylemaker_v1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.DB.CodiDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.DailyDBSqlData;
import com.sagra.stylemaker_v1.adapter.MyAdapter;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.DownloadImageTask;
import com.sagra.stylemaker_v1.server.Server_CodiData;
import com.sagra.stylemaker_v1.server.Server_DailyData;

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

// 코디 만들기 -> 세번째 tab에서 add할때
public class AddcodiActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String set1, set2, set3;
    ArrayList<ClothData> mAppItem;
    MyAdapter adapter;
    Spinner spinner2,spinner3;
    Spinner spinner4, spinner5, spinner6;
    String type0, type1, type2;
    ImageButton saveBtn;
    ImageView iv, iv2, iv3, iv4, iv5, iv6;
    String date = null;
    String frommain = null;
    int position_items = -1;
    String codiupdate;
    String codiupdate_date;
    String codiupdatefirst;
    String first;
    CodiData info;
    String top, number;
    String tag = "";
    float scale;
    String uid;

    ContextWrapper cw;
    File directory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // actionbar setting
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("뒤로가기");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcodi);
        codiupdatefirst = "yes";
        first = "yes";
        scale = AddcodiActivity.this.getResources().getDisplayMetrics().density;
        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        cw = new ContextWrapper(AddcodiActivity.this);
        directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
        //spinner1,2,3은 옷 종류에 관한 것이고 4,5,6은 코디 종류에 관한 것이다
        Spinner spinner1 = (Spinner) this.findViewById(R.id.set1);
        spinner1.setOnItemSelectedListener(this);
        spinner2 = (Spinner) this.findViewById(R.id.set2);
    //    spinner2.setOnItemSelectedListener(spinSelectedlistener_cloth);
        spinner3 = (Spinner) this.findViewById(R.id.set3);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setOnItemSelectedListener(this);

        populateSpinners_cloth();

        spinner4 = (Spinner) this.findViewById(R.id.type0);
        spinner4.setOnItemSelectedListener(this);
        spinner5 = (Spinner) this.findViewById(R.id.type1);
        spinner5.setOnItemSelectedListener(spinSelectedlistener);
        populateSpinners();
        spinner6 = (Spinner) this.findViewById(R.id.type2);
        spinner6.setOnItemSelectedListener(this);


        saveBtn = (ImageButton) this.findViewById(R.id.save);
        saveBtn.setOnClickListener(this);

        iv = (ImageView) findViewById(R.id.imageView1);
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv3 = (ImageView) findViewById(R.id.imageView3);
        iv4 = (ImageView) findViewById(R.id.imageView4);
        iv5 = (ImageView) findViewById(R.id.imageView5);
        iv6 = (ImageView) findViewById(R.id.imageView6);

        // 인텐트를 가져온다. from calendarActivity / from firstActivity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                date= null;
                frommain = null;
                top = null;
            } else {
                top = extras.getString("top");
                date= extras.getString("date");
                frommain =  extras.getString("frommain");
            }
        } else {
            top = (String) savedInstanceState.getSerializable("top");
            date= (String) savedInstanceState.getSerializable("date");
            frommain =  (String) savedInstanceState.getSerializable("frommain");
        }

        //  인텐트가 있는 경우 actionbar setting
        if(date != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            setTitle("뒤로가기");
        }

        // 계절정보 가져오기 및 spinner 세팅
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        String season = wPreference.getString("season", "");
        spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(season));
        spinner4.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(season));

        set1 =season;
        set2 = "상의";
        // cloth값 가져와서 보여주기
        mAppItem = new ArrayList<ClothData>();

        // 인텐트 받기 - 코디수정으로 온 경우
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                codiupdate = null;
                codiupdate_date = null;
            } else {
                codiupdate =  extras.getString("codiupdate");
                codiupdate_date =  extras.getString("codiupdate_date");
            }
        } else {
            date= (String) savedInstanceState.getSerializable("codiupdate");
        }

        // 코디수정인 경우 값 받아서 각 이미지뷰에 지정하기
        if(codiupdate != null){
            info = (CodiData) getIntent().getSerializableExtra("info");
            Log.d("saea", info.getNumber());
          //  new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getTop());
            try {
                File f=new File(directory, info.getTop());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                iv.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            iv.setTag(info.getTop());

            try {
                File f=new File(directory, info.getBottom());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                iv2.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
          //  new DownloadImageTask(iv2).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getBottom());
            iv2.setTag(info.getBottom());

                try {
                    File f=new File(directory, info.getShoes());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    iv3.setImageBitmap(b);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
          //  new DownloadImageTask(iv3).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getShoes());
            iv3.setTag(info.getShoes());

            try {
                File f=new File(directory, info.getOuter());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                iv4.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
          //  new DownloadImageTask(iv4).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getOuter());
            iv4.setTag(info.getOuter());

            try {
                File f=new File(directory, info.getBag());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                iv5.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
           // new DownloadImageTask(iv5).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getBag());
            iv5.setTag(info.getBag());

            try {
                File f=new File(directory, info.getAccessories());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                iv6.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
          //  new DownloadImageTask(iv6).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getAccessories());
            iv6.setTag(info.getAccessories());
        }


    }


    // 뒤로가기 시에 왔던 곳으로 이동
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = getIntent();
        String frommain = intent.getStringExtra("frommain");

        if(frommain != null){
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }else if(date!=null){
            Intent myIntent = new Intent(getApplicationContext(), FifthActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }else{
            Intent myIntent = new Intent(getApplicationContext(), ThirdActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAppItem) {
            mAppItem.clear();
        }
    }

    // 각 옷 아이템 가져와서 보여주기
    private void getItemList() {
        mAppItem.clear();
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
            DBManager dbMgr = new DBManager(AddcodiActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData("selectcloth", cData, mAppItem);
            dbMgr.dbClose();
        }else{
            ClothData cData = new ClothData(uid, set1_en, set2_en, "", "-", "");
         //   selectCloth(this, uid, cData, mAppItem);
            DBManager dbMgr = new DBManager(AddcodiActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectClothData("selectcloth", cData, mAppItem);
            dbMgr.dbClose();
        }

       mAppItem.add(new ClothData("", "", "", "", "", ""));


        adapter = new MyAdapter(
                getApplicationContext(), // 현재 화면의 제어권자
                R.layout.addcodi_gallery_layout,
                mAppItem);

        Gallery g = (Gallery) findViewById(R.id.gallery1);
        g.setAdapter(adapter);

        iv = (ImageView) findViewById(R.id.imageView1);
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv3 = (ImageView) findViewById(R.id.imageView3);
        iv4 = (ImageView) findViewById(R.id.imageView4);
        iv5 = (ImageView) findViewById(R.id.imageView5);
        iv6 = (ImageView) findViewById(R.id.imageView6);

        if(top != null){
            if(top.contains("dress")){
                iv2.setImageBitmap(null);
                iv2.setTag("");
                int height2  = (int) (300 * scale);
                iv.getLayoutParams().height = height2;
                iv2.getLayoutParams().height = 0;
            }
        }



        // view에서 값 선택시에 이벤트
        g.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) { // 선택되었을 때 콜백메서드
                ClothData gridItem = (ClothData) parent.getAdapter().getItem(position);
                position_items = position;
                Log.d("saea", gridItem.getInfo());
                // 옷을 고르면 옷이 iv에 따라 나오고 Tag에 저장된다
                if (set2.equals("상의")) {
                    iv.setImageBitmap(null);
                    int height = (int) (150 * scale);
                    iv.getLayoutParams().height = height;
                    iv2.getLayoutParams().height = height;


                  // new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        iv.setImageBitmap(null);
                    }else {
                        try {
                            File f = new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv.setImageBitmap(b);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    //   Glide.with(AddcodiActivity.this).load(bitmap).into(iv);
                    String path = gridItem.getInfo();
                    iv.setTag(path);

                    if (codiupdate != null && codiupdatefirst.equals("yes")) {
                        info = (CodiData) getIntent().getSerializableExtra("info");

                        Log.d("saea", info.getNumber());
                        if (info.getTop().contains("dress")) {
                            iv2.setImageBitmap(null);
                            iv2.setTag("");
                            int height2 = (int) (300 * scale);
                            iv.getLayoutParams().height = height2;
                            iv2.getLayoutParams().height = 0;
                        }
                    //    new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getTop());
                        if(info.getTop().equals("")){
                            iv.setImageBitmap(null);
                        }else {
                            try {
                                File f = new File(directory, info.getTop());
                                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                                iv.setImageBitmap(b);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        iv.setTag(info.getTop());

                        codiupdatefirst = "no";
                    }


                } else if (set2.equals("하의")) {
                    String ivtag = "";
                    if (iv.getTag() != null) {
                        ivtag = iv.getTag().toString();
                        Log.d("saea", ivtag);
                    }
                    if (ivtag.contains("dress")) {
                        iv.setImageBitmap(null);
                        iv.setTag("");
                    }

                    iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.maketop_height);
                    iv2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.maketop_height);


                   // new DownloadImageTask(iv2).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        iv2.setImageBitmap(null);
                    }else{
                        try {
                            File f=new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv2.setImageBitmap(b);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                    }
                    String path = gridItem.getInfo();
                    iv2.setTag(path);
                } else if (set2.equals("신발")) {
                 //   new DownloadImageTask(iv3).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        iv3.setImageBitmap(null);
                    }else{
                        try {
                            File f=new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv3.setImageBitmap(b);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String path = gridItem.getInfo();
                    iv3.setTag(path);

                } else if (set2.equals("아우터")) {
                   // new DownloadImageTask(iv4).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        Log.d("saea", "outer satatus null");
                        iv4.setImageBitmap(null);
                    }else{
                        try {
                            File f=new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv4.setImageBitmap(b);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    String path = gridItem.getInfo();
                    iv4.setTag(path);
                } else if (set2.equals("가방")) {
                  //  new DownloadImageTask(iv5).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        iv5.setImageBitmap(null);
                    }else{
                        try {
                            File f=new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv5.setImageBitmap(b);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String path = gridItem.getInfo();
                    iv5.setTag(path);
                } else if (set2.equals("악세서리")) {
                   // new DownloadImageTask(iv6).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        iv6.setImageBitmap(null);
                    }else{
                        try {
                            File f=new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv6.setImageBitmap(b);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String path = gridItem.getInfo();
                    iv6.setTag(path);
                } else if (set2.equals("원피스")) {
                    iv2.setImageBitmap(null);
                    iv2.setTag("");
                    iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.makedress_height);
                    iv2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.makedress_iv2height);
                //    new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user" + uid + "/" + gridItem.getInfo());
                    if(gridItem.getInfo().equals("")){
                        iv.setImageBitmap(null);
                    }else{
                        try {
                            File f=new File(directory, gridItem.getInfo());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            iv.setImageBitmap(b);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String path = gridItem.getInfo();
                    iv.setTag(path);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

            }


// spinner 관련
    //spinner (옷) 관련된 코드

    private void populateSpinners_cloth() {
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

        populateSubSpinners_cloth(R.array.set3_0);
    }

    private void populateSubSpinners_cloth(int itemNum) {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                itemNum,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(fAdapter);
    }

    private AdapterView.OnItemSelectedListener spinSelectedlistener_cloth =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    //    String[] set2Array = getResources().getStringArray(R.array.set2_array);
                    SharedPreferences setPreference = getSharedPreferences("Setting", MODE_PRIVATE);
                    String gender = setPreference.getString("gender", "");
                    String[] set2Array;
                    if(gender.equals("여자")){
                        set2Array = getResources().getStringArray(R.array.set2_array_women);
                    }else{
                        set2Array = getResources().getStringArray(R.array.set2_array);
                    }
                    set2 = set2Array[position];
                    if(!set2.equals("상의") && !set2.equals("하의")){
                        set3 = null;
                    }
                //    mAppItem.clear();
                    getItemList();
                //    adapter.notifyDataSetChanged();

                    if(set2.equals("상의")){
                      //  getFirstItem(adapter, iv);
                    }else if(set2.equals("하의")){
                        int height  = (int) (150 * scale);
                        iv.getLayoutParams().height = height ;
                        iv2.getLayoutParams().height = height ;
                        String ivtag = iv.getTag().toString();
                        if(ivtag.contains("원피스")){
                            iv.setImageBitmap(null);
                            iv.setTag("");
                        }

                        //getFirstItem(adapter, iv2);
                    }else if(set2.equals("신발")){
                       // getFirstItem(adapter, iv3);

                    }else if(set2.equals("아우터")){
                      //  getFirstItem(adapter, iv4);
                    }else if(set2.equals("가방")){
                      //  getFirstItem(adapter, iv5);
                    }else if(set2.equals("악세서리")){
                       // getFirstItem(adapter, iv6);
                    }else if(set2.equals("원피스")){

                        iv2.setImageBitmap(null);
                        iv2.setTag("");
                        int height2  = (int) (300 * scale);
                        iv.getLayoutParams().height = height2 ;
                        iv2.getLayoutParams().height = 0;

                     //   getFirstItem(adapter, iv);

                    }


                    String[] set3Array;
                    switch(position){
                        case (0):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners_cloth(R.array.set3_0);
                            set3Array = getResources().getStringArray(R.array.set3_0);
                            set3 = set3Array[position];
                            break;
                        case (1):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners_cloth(R.array.set3_0);
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

    //spinner (코디) 관련된 코드
    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                R.array.type1_array,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(fAdapter);
    }

    private void populateSubSpinners(int itemNum) {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                itemNum,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner6.setAdapter(fAdapter);
    }

    private AdapterView.OnItemSelectedListener spinSelectedlistener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String[] type1Array = getResources().getStringArray(R.array.type1_array);
                    type1 = type1Array[position];
                    switch (position) {
                        case (0):
                            spinner6.setVisibility(View.GONE);
                            type2 = null;
                            break;
                        case (1):
                            spinner6.setVisibility(View.GONE);
                            type2 = null;
                            break;
                        case (2):
                            spinner6.setVisibility(View.GONE);
                            type2 = null;
                            break;

                        case (3):
                            spinner6.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.type2_array);
                            type2 = spinner6.getSelectedItem().toString();
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    // spinner 선택시 이벤트
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.set1:
                Spinner spinner = (Spinner) findViewById(R.id.set1);
                set1 = spinner.getSelectedItem().toString();
                if(first == "no"){
                    getItemList();
                }

                break;
            case R.id.set2:
                Spinner spinner2 = (Spinner) findViewById(R.id.set2);
                set2 = spinner2.getSelectedItem().toString();

                Log.d("saea","set2 selected");

                if(first == "no"){
                    getItemList();
                }

                break;
            case R.id.set3:
                Spinner spinner3 = (Spinner) findViewById(R.id.set3);
                set3 = spinner3.getSelectedItem().toString();

                Log.d("saea","set3 selected");

                first = "no";
                if(first == "no"){
                    getItemList();
                }

                break;
            case R.id.type0:
                type0 = spinner4.getSelectedItem().toString();
                break;
            case R.id.type2:
                Spinner spinner6 = (Spinner) findViewById(R.id.type2);
                type2 = spinner6.getSelectedItem().toString();

                break;

        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // 저장시에 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                // 인텐트가 있는 경우 daily코디정보에 수정 혹은 삽입
                if(date != null || codiupdate_date != null){

                    ArrayList<CodiData> aCDataList = new ArrayList<CodiData>();
                  //  selectDailybyInfo(AddcodiActivity.this, uid, date); // 있는경우 update 없으면 insert
                    DBManager dbMgr = new DBManager(AddcodiActivity.this);
                    dbMgr.dbOpen();
                    if(date != null){
                        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date, aCDataList);
                    }else{
                        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, codiupdate_date, aCDataList);
                    }

                    dbMgr.dbClose();
                    if(!aCDataList.isEmpty()){
                        Log.d("saea", "jaesub1");
                       updateDailyDB(aCDataList.get(0));
                    }else{
                        Log.d("saea", "jaesub2");
                        insertDailyDB();
                    }

                    // 코디 수정에서 온 경우 업데이트
                }else if(codiupdate != null){
                    Log.d("saea", "jaesub3");
                    updateDB();
                    // 처음인 경우 코디 삽입
                }else{
                    Log.d("saea", "jaesub4");
                    insertDB();
                }

                break;
        }


    }

    // 코디 DB 관련
    private void insertDB() {
        String numberStr = null;
        String seasonStr = null;
        String typeStr = null;
        String topStr = "";
        String pantsStr = "";
        String shoesStr = "";
        String outerStr = "";
        String bagStr = "";
        String accessoriesStr = "";

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        numberStr = dayTime.format(new Date(time)); // number에는 코디저장한 시간으로 저장한다!

        seasonStr = type0;

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
        String typeStr_en = null;
        String type2_en = null;

        if(type2 != null){
            if(type2.equals("결혼식")){
                type2_en = "wedding";
            }else if(type2.equals("운동복")){
                type2_en = "sportsware";
            }else if(type2.equals("소풍")){
                type2_en = "picnic";
            }
            typeStr = type1+"&"+type2;
            typeStr_en = type1_en+"&"+type2_en;
        }else{
            typeStr = type1;
            typeStr_en = type1_en;
        }

        String seasonStr_en= null;
        if( seasonStr.equals("계절무관")){
            seasonStr_en = "all";
        }else if( seasonStr.equals("봄,가을")){
            seasonStr_en = "s,f";
        }else if( seasonStr.equals("여름")){
            seasonStr_en = "summer";
        }else if( seasonStr.equals("겨울")){
            seasonStr_en = "winter";
        }

        if(iv.getTag() != null){
            topStr = iv.getTag().toString();
        }
        if(iv2.getTag() != null){
            pantsStr = iv2.getTag().toString();
        }
        if(iv3.getTag() != null){
            shoesStr = iv3.getTag().toString();
        }
        if(iv4.getTag() != null){
            outerStr = iv4.getTag().toString();
        }
        if(iv5.getTag() != null){
            bagStr = iv5.getTag().toString();
        }
        if(iv6.getTag() != null){
            accessoriesStr = iv6.getTag().toString();
        }


        String tagStr = tag;
        CodiData cData = new CodiData(uid, numberStr, seasonStr_en, typeStr_en, topStr, pantsStr, shoesStr, outerStr, bagStr, accessoriesStr, tagStr);

        Server_CodiData.insertCodi(AddcodiActivity.this, uid, cData);

        DBManager dbMgr = new DBManager(AddcodiActivity.this);
        dbMgr.dbOpen();
        dbMgr.insertCodiData(CodiDBSqlData.SQL_DB_INSERT_DATA, cData);
        dbMgr.dbClose();
        Toast.makeText(this, seasonStr+"/"+typeStr+" 코디가 저장되었습니다", Toast.LENGTH_SHORT).show();

    }


    private void updateDB() {

        String numberStr = null;
        String seasonStr = null;
        String typeStr = null;
        String topStr = "";
        String pantsStr = "";
        String shoesStr = "";
        String outerStr = "";
        String bagStr = "";
        String accessoriesStr = "";


        seasonStr = type0;
        Log.d("saea", "update "+type0+type1+type2);
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
        String typeStr_en = null;
        String type2_en = null;

        if(type2 != null){
            if(type2.equals("결혼식")){
                type2_en = "wedding";
            }else if(type2.equals("운동복")){
                type2_en = "sportsware";
            }else if(type2.equals("소풍")){
                type2_en = "picnic";
            }
            typeStr_en = type1_en+"&"+type2_en;
        }else{
            typeStr_en = type1_en;
        }

        String seasonStr_en= null;
        if( seasonStr.equals("계절무관")){
            seasonStr_en = "all";
        }else if( seasonStr.equals("봄,가을")){
            seasonStr_en = "s,f";
        }else if( seasonStr.equals("여름")){
            seasonStr_en = "summer";
        }else if( seasonStr.equals("겨울")){
            seasonStr_en = "winter";
        }

        if(iv.getTag() != null){
            topStr = iv.getTag().toString();
        }
        if(iv2.getTag() != null){
            pantsStr = iv2.getTag().toString();
        }
        if(iv3.getTag() != null){
            shoesStr = iv3.getTag().toString();
        }
        if(iv4.getTag() != null){
            outerStr = iv4.getTag().toString();
        }
        if(iv5.getTag() != null){
            bagStr = iv5.getTag().toString();
        }
        if(iv6.getTag() != null){
            accessoriesStr = iv6.getTag().toString();
        }

        String tagStr = tag;
        CodiData cData = new CodiData(uid, info.getNumber(), seasonStr_en, typeStr_en, topStr, pantsStr, shoesStr, outerStr, bagStr, accessoriesStr, tagStr);
        Log.d("saea", "uodate "+seasonStr_en+typeStr_en);

        updateCodi(AddcodiActivity.this, uid, cData); // 이는 코디수정에서 오는 것

    }



    private void insertDailyDB() {

        String numberStr = null;
        String seasonStr = null;
        String typeStr = null;
        String topStr = "";
        String pantsStr = "";
        String shoesStr = "";
        String outerStr = "";
        String bagStr = "";
        String accessoriesStr = "";

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      //  numberStr = dayTime.format(new Date(time)); // number에는 코디저장한 시간으로 저장한다!
        numberStr = date;
        seasonStr = type0;

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
        String typeStr_en = null;
        String type2_en = null;

        if(type2 != null){
            if(type2.equals("결혼식")){
                type2_en = "wedding";
            }else if(type2.equals("운동복")){
                type2_en = "sportsware";
            }else if(type2.equals("소풍")){
                type2_en = "picnic";
            }
            typeStr = type1+"&"+type2;
            typeStr_en = type1_en+"&"+type2_en;
        }else{
            typeStr = type1;
            typeStr_en = type1_en;
        }
        //  Toast.makeText(this, numberStr+seasonStr+typeStr, Toast.LENGTH_SHORT).show();

        String seasonStr_en= null;
        if( seasonStr.equals("계절무관")){
            seasonStr_en = "all";
        }else if( seasonStr.equals("봄,가을")){
            seasonStr_en = "s,f";
        }else if( seasonStr.equals("여름")){
            seasonStr_en = "summer";
        }else if( seasonStr.equals("겨울")){
            seasonStr_en = "winter";
        }

        if(iv.getTag() != null){
            topStr = iv.getTag().toString();
        }
        if(iv2.getTag() != null){
            pantsStr = iv2.getTag().toString();
        }
        if(iv3.getTag() != null){
            shoesStr = iv3.getTag().toString();
        }
        if(iv4.getTag() != null){
            outerStr = iv4.getTag().toString();
        }
        if(iv5.getTag() != null){
            bagStr = iv5.getTag().toString();
        }
        if(iv6.getTag() != null){
            accessoriesStr = iv6.getTag().toString();
        }

        String tagStr = tag;
        CodiData cData = new CodiData(uid, numberStr, seasonStr_en, typeStr_en, topStr, pantsStr, shoesStr, outerStr, bagStr, accessoriesStr, tagStr);
        Log.d("saea","here"+cData.getUid()+cData.getSeason()+cData.getType()+cData.getBag()+cData.getBottom()+cData.getBag()+cData.getTop()+cData.getNumber());


        Server_DailyData.insertDaily(AddcodiActivity.this, uid, cData);
        DBManager dbMgr = new DBManager(AddcodiActivity.this);
        dbMgr.dbOpen();
        dbMgr.insertDailyData(DailyDBSqlData.SQL_DB_INSERT_DATA, cData);
        dbMgr.dbClose();

        Toast.makeText(this, seasonStr+"/"+typeStr+" 코디가 선택되었습니다", Toast.LENGTH_SHORT).show();
        if(frommain != null){
            Intent i = new Intent(AddcodiActivity.this, MainActivity.class);
            startActivity(i);
        }else if(date != null){
            Intent i = new Intent(AddcodiActivity.this, FifthActivity.class);
            startActivity(i);
        }else{
            Intent i = new Intent(AddcodiActivity.this, AddcodiActivity.class);
            startActivity(i);
        }

    }

    private void updateDailyDB(CodiData codiData) {

        String numberStr = null;
        String seasonStr = null;
        String typeStr = null;
        String topStr = "";
        String pantsStr = "";
        String shoesStr = "";
        String outerStr = "";
        String bagStr = "";
        String accessoriesStr = "";

        seasonStr = type0;
        Log.d("saea", "here"+type0+type1+type2);
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
        String typeStr_en = null;
        String type2_en = null;

        if(type2 != null){
            if(type2.equals("결혼식")){
                type2_en = "wedding";
            }else if(type2.equals("운동복")){
                type2_en = "sportsware";
            }else if(type2.equals("소풍")){
                type2_en = "picnic";
            }
            typeStr_en = type1_en+"&"+type2_en;
        }else{
            typeStr_en = type1_en;
        }

        String seasonStr_en= null;
        if( seasonStr.equals("계절무관")){
            seasonStr_en = "all";
        }else if( seasonStr.equals("봄,가을")){
            seasonStr_en = "s,f";
        }else if( seasonStr.equals("여름")){
            seasonStr_en = "summer";
        }else if( seasonStr.equals("겨울")){
            seasonStr_en = "winter";
        }

        if(iv.getTag() != null){
            topStr = iv.getTag().toString();
        }
        if(iv2.getTag() != null){
            pantsStr = iv2.getTag().toString();
        }
        if(iv3.getTag() != null){
            shoesStr = iv3.getTag().toString();
        }
        if(iv4.getTag() != null){
            outerStr = iv4.getTag().toString();
        }
        if(iv5.getTag() != null){
            bagStr = iv5.getTag().toString();
        }
        if(iv6.getTag() != null){
            accessoriesStr = iv6.getTag().toString();
        }


        String tagStr = codiData.getTag();
        CodiData cData;
        if(date != null){
            cData = new CodiData(uid, date, seasonStr_en, typeStr_en, topStr, pantsStr, shoesStr, outerStr, bagStr, accessoriesStr, tagStr);
        }else{
            cData = new CodiData(uid, codiupdate_date, seasonStr_en, typeStr_en, topStr, pantsStr, shoesStr, outerStr, bagStr, accessoriesStr, tagStr);
        }

        Log.d("saea", "here2"+seasonStr_en+typeStr_en);

        updateDaily(AddcodiActivity.this, uid, cData);

    }

    public void selectCloth(final Context context, final String uid, final ClothData cData, ArrayList<ClothData> mAppItem) {

        Log.i("saea", "Starting Upload...");
        String season = cData.getSeason();
        String type = cData.getType();
        String detail1 = cData.getDetail1();
        String detail2 = cData.getDetail2();
        selectCloth_Connect(context, uid, season, type, detail1, detail2, mAppItem);

    }


    public void selectCloth_Connect(final Context context, final String uid, final String season, final String type, final String detail1, final String detail2, final ArrayList<ClothData> mAppItem) {
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
                            if(clothItems.size() == 1){
                                clothItems.add(new ClothData("", "", "", "", "", ""));
                            }

                            adapter = new MyAdapter(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    R.layout.addcodi_gallery_layout,
                                    clothItems);

                            Gallery g = (Gallery) findViewById(R.id.gallery1);
                            g.setAdapter(adapter);

                            iv = (ImageView) findViewById(R.id.imageView1);
                            iv2 = (ImageView) findViewById(R.id.imageView2);
                            iv3 = (ImageView) findViewById(R.id.imageView3);
                            iv4 = (ImageView) findViewById(R.id.imageView4);
                            iv5 = (ImageView) findViewById(R.id.imageView5);
                            iv6 = (ImageView) findViewById(R.id.imageView6);

                            if(top != null){
                                if(top.contains("dress")){
                                    iv2.setImageBitmap(null);
                                    iv2.setTag("");
                                    int height2  = (int) (300 * scale);
                                    iv.getLayoutParams().height = height2;
                                    iv2.getLayoutParams().height = 0;
                                }
                            }

                            // view에서 값 선택시에 이벤트
                            g.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view,
                                                           int position, long id) { // 선택되었을 때 콜백메서드
                                    ClothData gridItem = (ClothData) parent.getAdapter().getItem(position);
                                    position_items = position;
                                    Log.d("saea", gridItem.getInfo());
                                    // 옷을 고르면 옷이 iv에 따라 나오고 Tag에 저장된다
                                    if (set2.equals("상의")) {
                                        iv.setImageBitmap(null);
                                        int height  = (int) (150 * scale);
                                        iv.getLayoutParams().height = height;
                                        iv2.getLayoutParams().height = height;


                                            new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());
                                            //   Glide.with(AddcodiActivity.this).load(bitmap).into(iv);
                                            String path = gridItem.getInfo();
                                            iv.setTag(path);

                                            if(codiupdate != null && codiupdatefirst.equals("yes")){
                                                info = (CodiData) getIntent().getSerializableExtra("info");

                                                Log.d("saea", info.getNumber());
                                                if(info.getTop().contains("dress")){
                                                    iv2.setImageBitmap(null);
                                                    iv2.setTag("");
                                                    int height2  = (int) (300 * scale);
                                                    iv.getLayoutParams().height = height2;
                                                    iv2.getLayoutParams().height = 0;
                                                }
                                                new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getTop());
                                                iv.setTag(info.getTop());

                                                codiupdatefirst = "no";
                                            }


                                    } else if (set2.equals("하의")) {
                                        String ivtag = "";
                                        if(iv.getTag() != null){
                                            ivtag = iv.getTag().toString();
                                            Log.d("saea", ivtag);
                                        }
                                        if(ivtag.contains("dress")){
                                            iv.setImageBitmap(null);
                                            iv.setTag("");
                                        }

                                        iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.maketop_height);
                                        iv2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.maketop_height);


                                        new DownloadImageTask(iv2).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());

                                        String path = gridItem.getInfo();
                                        iv2.setTag(path);
                                    } else if (set2.equals("신발")) {
                                        new DownloadImageTask(iv3).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());
                                        String path = gridItem.getInfo();
                                        iv3.setTag(path);

                                    }  else if (set2.equals("아우터")) {
                                        new DownloadImageTask(iv4).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());
                                        String path = gridItem.getInfo();
                                        iv4.setTag(path);
                                    }else if (set2.equals("가방")) {
                                        new DownloadImageTask(iv5).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());
                                        String path = gridItem.getInfo();
                                        iv5.setTag(path);
                                    } else if (set2.equals("악세서리")) {
                                        new DownloadImageTask(iv6).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());
                                        String path = gridItem.getInfo();
                                        iv6.setTag(path);
                                    }else if(set2.equals("원피스")){
                                        iv2.setImageBitmap(null);
                                        iv2.setTag("");
                                        iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.makedress_height);
                                        iv2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.makedress_iv2height);
                                        new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+gridItem.getInfo());
                                        String path = gridItem.getInfo();
                                        iv.setTag(path);
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });



                        }else{
                            adapter = new MyAdapter(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    R.layout.addcodi_gallery_layout,
                                    clothItems);

                            Gallery g = (Gallery) findViewById(R.id.gallery1);
                            g.setAdapter(adapter);

                            iv = (ImageView) findViewById(R.id.imageView1);
                            iv2 = (ImageView) findViewById(R.id.imageView2);
                            iv3 = (ImageView) findViewById(R.id.imageView3);
                            iv4 = (ImageView) findViewById(R.id.imageView4);
                            iv5 = (ImageView) findViewById(R.id.imageView5);
                            iv6 = (ImageView) findViewById(R.id.imageView6);

                            if(top != null){
                                if(top.contains("원피스")){
                                    iv2.setImageBitmap(null);
                                    iv2.setTag("");
                                    int height2  = (int) (300 * scale);
                                    iv.getLayoutParams().height = height2 ;
                                    iv2.getLayoutParams().height = 0;

                                }
                            }

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
                Log.e("saea", "select: " + uid+season+type+detail1+detail2);
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

                        DBManager dbMgr = new DBManager(AddcodiActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.updateCodiData(CodiDBSqlData.SQL_DB_UPDATE_DATA, new String[]{codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                        codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag(), codiData.getUid(), codiData.getNumber()});
                        dbMgr.dbClose();
                        Intent i = new Intent(AddcodiActivity.this, CodidetailActivity.class);
                        i.putExtra("info", codiData );
                        startActivity(i);

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


                        updateDailyDB(codiData);


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        insertDailyDB();
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
                Log.e("saea", "insert22: " + uid+date);
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

                        DBManager dbMgr = new DBManager(AddcodiActivity.this);
                        dbMgr.dbOpen();
                        dbMgr.updateDailyData(DailyDBSqlData.SQL_DB_UPDATE_DATE, new String[]{codiData.getSeason(), codiData.getType(), codiData.getTop(), codiData.getBottom(),
                                codiData.getShoes(), codiData.getOuter(), codiData.getBag(), codiData.getAccessories(), codiData.getTag(), codiData.getUid(), codiData.getNumber()});
                        dbMgr.dbClose();

                        if(frommain != null){
                            Intent i = new Intent(AddcodiActivity.this, MainActivity.class);
                            startActivity(i);

                        }else if(date != null){
                            Intent i = new Intent(AddcodiActivity.this, FifthActivity.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(AddcodiActivity.this, CodidetailActivity.class);
                            i.putExtra("info", codiData);
                            i.putExtra("date", codiData.getNumber());
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
                Log.d("saea", codiData.getNumber()+codiData.getTop()+codiData.getUid());
                params.put("status", "update");
                params.put("uid", codiData.getUid());
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



