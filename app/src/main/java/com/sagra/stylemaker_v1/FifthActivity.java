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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.DailyDBSqlData;
import com.sagra.stylemaker_v1.DB.WeatherDBSqlData;
import com.sagra.stylemaker_v1.adapter.CalendarAdapter;
import com.sagra.stylemaker_v1.adapter.CodiAdapter;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.data.DayInfo;
import com.sagra.stylemaker_v1.data.WeatherData;
import com.sagra.stylemaker_v1.etc.BottomNavigationViewHelper;
import com.sagra.stylemaker_v1.etc.Fonttype;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

// 마지막 tab 날짜별 코디
/**
 * 그리드뷰를 이용한 달력 예제
 *
 * @blog http://croute.me
 * @link http://croute.me/335
 *
 * @author croute
 * @since 2011.03.08
 */
public class FifthActivity extends AppCompatActivity implements OnItemClickListener, OnClickListener {

  String uid;
    private TextView mTvCalendarTitle;
    private GridView mGvCalendar;

    private ArrayList<DayInfo> mDayList;
    private CalendarAdapter mCalendarAdapter;

     Calendar mThisMonthCalendar;
    Calendar mCalendar = Calendar.getInstance();
    String first, last;
    TextView date = null;
    String prenext = "";
    String firstday, lastday;
    ImageButton select1, select2;

    private ArrayList<CodiData> mAppItem = null;
    CodiAdapter adapter;
    String dateForDB;
    String date_first;

    int atfirst = 0;
    int getall = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();
        // actionbar setting
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        TextView mytext = (TextView) findViewById(R.id.mytext);
        Fonttype.setFont("Billabong",FifthActivity.this, mytext);

        // calendar 관련된 뷰
        ImageView bLastMonth = (ImageView)findViewById(R.id.gv_calendar_activity_b_last);
        ImageView bNextMonth = (ImageView)findViewById(R.id.gv_calendar_activity_b_next);

        mTvCalendarTitle = (TextView)findViewById(R.id.gv_calendar_activity_tv_title);
        mGvCalendar = (GridView)findViewById(R.id.gv_calendar_activity_gv_calendar);

        bLastMonth.setOnClickListener(this);
        bNextMonth.setOnClickListener(this);
        mGvCalendar.setOnItemClickListener(this);

        mDayList = new ArrayList<DayInfo>();

        date = (TextView)findViewById(R.id.date);

        // 코디 직접하러가기, 코디함 가기 코드
        select1 = (ImageButton)findViewById(R.id.select1);
        select2 = (ImageButton)findViewById(R.id.select2);
        select1.setOnClickListener(this);
        select2.setOnClickListener(this);

        mAppItem =  new ArrayList<CodiData>();

        // bottomnavigation 뷰 설정
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        BottomNavigationViewHelper.disableShiftMode2(bottomNavigationView);
        // Ensure correct menu item is selected (where the magic happens)
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

        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_one:
                        Intent i = new Intent(FifthActivity.this, MainActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_two:
                        Intent i2 = new Intent(FifthActivity.this, SecondActivity.class);
                        startActivity(i2);
                        return true;
                    case R.id.action_three:
                        Intent i3 = new Intent(FifthActivity.this, ThirdActivity.class);
                        startActivity(i3);
                        return true;
                    case R.id.action_four:
                        Intent i4 = new Intent(FifthActivity.this, FourthActivity.class);
                        startActivity(i4);
                        break;
                    case R.id.action_fifth:
                        Intent i5 = new Intent(FifthActivity.this, FifthActivity.class);
                        startActivity(i5);
                        break;
                }
                return false;
            }

        });

    }

    // 코디클릭시(확인하는데 필요)
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long l_position) {
            // parent는 AdapterView의 속성의 모두 사용 할 수 있다.
            CodiData codiData = (CodiData) parent.getAdapter().getItem(position);
            Intent i = new Intent(FifthActivity.this, CodidetailActivity.class);
            i.putExtra("info", codiData);
            i.putExtra("date", codiData.getNumber());
            startActivity(i);

        }
    };


    // 일주일치 저장된 코디 가져오기
    private void getItemListbyDate(ArrayList<CodiData> aCDataList) {
        getall = 0;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cal.setTime(sdf.parse(dateForDB));// all done

        } catch (ParseException e) {
            e.printStackTrace();
        }


        String date_minus0, date_minus1, date_minus2, date_minus3, date_minus4, date_minus5, date_minus6;
        date_minus0 = sdf.format(cal.getTime());
        date_first = date_minus0;

        cal.add(cal.DATE, -1);
        date_minus1 = sdf.format(cal.getTime());
        cal.add(cal.DATE, -1);
        date_minus2 = sdf.format(cal.getTime());
        cal.add(cal.DATE, -1);
        date_minus3 = sdf.format(cal.getTime());
        cal.add(cal.DATE, -1);
        date_minus4 = sdf.format(cal.getTime());
        cal.add(cal.DATE, -1);
        date_minus5 = sdf.format(cal.getTime());
        cal.add(cal.DATE, -1);
        date_minus6 = sdf.format(cal.getTime());
        //   Log.e("saea", date_minus0+date_minus1+date_minus2+date_minus3+date_minus4+date_minus5);
        // 한주에 가장 첫날이 date_minus6
       /* selectDailybyInfo(FifthActivity.this, uid, date_minus6, aCDataList);
        selectDailybyInfo(FifthActivity.this, uid, date_minus5, aCDataList);
        selectDailybyInfo(FifthActivity.this, uid, date_minus4, aCDataList);
        selectDailybyInfo(FifthActivity.this, uid, date_minus3, aCDataList);
        selectDailybyInfo(FifthActivity.this, uid, date_minus2, aCDataList);
        selectDailybyInfo(FifthActivity.this, uid, date_minus1, aCDataList);
        selectDailybyInfo(FifthActivity.this, uid, date_minus0, aCDataList);
        */
        DBManager dbMgr = new DBManager(FifthActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus6, aCDataList);
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus5, aCDataList);
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus4, aCDataList);
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus3, aCDataList);
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus2, aCDataList);
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus1, aCDataList);
        dbMgr.selectDailyData(DailyDBSqlData.SQL_DB_SELECT_DATE, uid, date_minus0, aCDataList);
        dbMgr.dbClose();
        if(!aCDataList.isEmpty()){
            Log.d("saea", aCDataList.size()+"개");
            mAppItem = aCDataList;
            GridView gridView = (GridView) findViewById(R.id.grid_codi);
            adapter = new CodiAdapter(FifthActivity.this, R.layout.codi_item_layout, mAppItem, true);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(mItemClickListener);
        }


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 캘린더 가져오기

        // 이번달 의 캘린더 인스턴스를 생성한다.
        mThisMonthCalendar = Calendar.getInstance();
        //   mThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mThisMonthCalendar);
        Log.e("saea", first);
        getWeather(first);

        // 코디 가져오기
        if(mAppItem != null){
            mAppItem.clear();
            getItemListbyDate(mAppItem);
        }

    }

    // 첫날로 부터 일주일치 날씨 가져오기
    private void getWeather(String first) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        try {
            cal.setTime(sdf.parse(first));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = format1.format( cal.getTime());
        Log.d("saea", date);

        // 월요일날짜가 있으면 날씨 정보가 있는 경우다

        for(int order=1; order<8; order++) {
            ArrayList<WeatherData> aWDataList =  new ArrayList<WeatherData>();
            DBManager dbMgr = new DBManager(this);
            dbMgr.dbOpen();
            dbMgr.selectWeatherbyDate(WeatherDBSqlData.SQL_DB_SELECT_DATE ,date, aWDataList);
            dbMgr.dbClose();
            int imgresId = getResources().getIdentifier("weather" + order, "id", getPackageName());
            ImageView weather1 = (ImageView)findViewById(imgresId);
            int txtresId = getResources().getIdentifier("weathertxt" + order, "id", getPackageName());
            TextView weathertxt1 = (TextView)findViewById(txtresId);
            if (!aWDataList.isEmpty()) {
                WeatherData data = aWDataList.get(0);
                weather1.setVisibility(View.VISIBLE);
                weathertxt1.setVisibility(View.VISIBLE);
                int weatherIconImageResource = getResources().getIdentifier("icon_" + data.getCode(), "drawable", getPackageName());
                weather1.setImageResource(weatherIconImageResource);
                weathertxt1.setText(data.getMin() + "/" + data.getMax() + "℃");
            }else{
                weather1.setVisibility(View.GONE);
                weathertxt1.setVisibility(View.GONE);
            }

            cal.add(Calendar.DATE, 1);
            date = format1.format(cal.getTime());
        }

    }

    /**
     * 달력을 셋팅한다.
     *
     * @param calendar 달력에 보여지는 이번달의 Calendar 객체
     */
    private void getCalendar(Calendar calendar)
    {

        int lastMonthStartDay;

        if(mDayList != null) {
            mDayList.clear();
        }

        DayInfo day;

        java.text.SimpleDateFormat formatter_month = new java.text.SimpleDateFormat("MM");
        java.text.SimpleDateFormat formatter_date = new java.text.SimpleDateFormat("dd");
        java.text.SimpleDateFormat formatter_forcal = new java.text.SimpleDateFormat("yyyy.MM.dd");

         // 오늘이 포함된 달과 오늘의 월요일 달이 다른 경우 전달의 값과 이번주값을 같이 가져온다. 아닌 경우 월요일~ 일요일까지

        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        Calendar cal = Calendar.getInstance();  // 현재 날짜/시간 등의 각종 정보 얻기
        int today = cal.get(Calendar.DAY_OF_WEEK);
        //일요일에 다음주가 나오는 것을 막기 위한 코드
        if(today == 1 && atfirst == 0){
            calendar.add(Calendar.DATE, -7);
        }
        firstday = formatter_date.format( calendar.getTime());
        first = formatter_forcal.format( calendar.getTime());
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Log.e("saea", dayOfMonth+"/"+lastMonthStartDay+"");
        //Log.e("saea", calendar.get(Calendar.MONTH)+""+lastMonthStartDay);
        try {
            mCalendar.setTime(formatter_forcal.parse(first));// all done // first day인 월요일을 기준으로 mCalendar를 선정한다.
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        calendar.add( calendar.DATE,7);
        lastday = formatter_date.format( calendar.getTime());
        last = formatter_forcal.format( calendar.getTime());
        Log.e("saea", firstday+"/"+lastday+"");
        try {
            mThisMonthCalendar.setTime(formatter_forcal.parse(last));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if((mThisMonthCalendar.get(Calendar.MONTH) + 1) < 10){
            mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "-0"
                    + (mThisMonthCalendar.get(Calendar.MONTH) + 1));
        }else{
            mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "-"
                    + (mThisMonthCalendar.get(Calendar.MONTH) + 1));
        }
         // last day인 일요일의 기준으로 월을 선정한다.

        if(Integer.parseInt(lastday)<7){
            for(int i=Integer.parseInt(firstday); i<lastMonthStartDay+1; i++)
            {

                day = new DayInfo();
                day.setDay(Integer.toString(i));
                day.setInMonth(false);

                mDayList.add(day);
            }
            for(int i=1; i <= Integer.parseInt(lastday); i++) // 원래 thisMonthLastDay 였다
            {
                day = new DayInfo();
                day.setDay(Integer.toString(i));
                day.setInMonth(true);

                mDayList.add(day);
            }
        }else{
            for(int i=Integer.parseInt(firstday); i <= Integer.parseInt(lastday); i++) // 원래 thisMonthLastDay 였다
            {
                day = new DayInfo();
                day.setDay(Integer.toString(i));
                day.setInMonth(true);

                mDayList.add(day);
            }
        }
        if((mThisMonthCalendar.get(Calendar.MONTH) + 1) < 10){
            dateForDB = mThisMonthCalendar.get(Calendar.YEAR) + "-0"
                    + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "-"+mThisMonthCalendar.get(Calendar.DATE); // 일요일이 저장된다
        }else{
            dateForDB = mThisMonthCalendar.get(Calendar.YEAR) + "-"
                    + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "-"+mThisMonthCalendar.get(Calendar.DATE); // 일요일이 저장된다
        }

        initCalendarAdapter();
    }





    private Calendar getLastWeek(Calendar cal)
    {
        cal.add(Calendar.DATE, -8);
        mTvCalendarTitle.setText(cal.get(Calendar.YEAR) + "-"
                + cal.get(Calendar.MONTH));
        return cal;
    }

    private Calendar getNextWeek(Calendar cal)
    {
        cal.add(Calendar.DATE, 6);
        mTvCalendarTitle.setText(cal.get(Calendar.YEAR) + "-"
                + cal.get(Calendar.MONTH));
        return cal;
    }

    TextView previousSelectedItem = null;
    // 각 날짜 클릭하면 색상변하고 날짜가져오기 이벤트
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3)
    {
        DayInfo dayinfo = (DayInfo) parent.getAdapter().getItem(position);
        if (previousSelectedItem!=null) {
            previousSelectedItem.setTextColor(Color.GRAY);
        }

    //    ll.setBackgroundResource(R.drawable.more);
        // v.setBackgroundResource(R.drawable.circle);
      //  v.setBackgroundColor(Color.rgb(1,171,192));
        TextView tv = v.findViewById(R.id.day_cell_tv_day);
        previousSelectedItem=tv;
        tv.setTextColor(Color.rgb(1,171,192));

        TextView date = (TextView)findViewById(R.id.date);
        String date_str = null;
        if(dayinfo.isInMonth() == true){
            if( Integer.parseInt(dayinfo.getDay()) < 10){
                date_str = "0"+dayinfo.getDay();
            }else{
                date_str = dayinfo.getDay();
            }
            date.setText(mTvCalendarTitle.getText()+"-"+date_str);
        }else{ // 만약 그 달의 값이 아닌 경우 한달을 빼서 값에 설정한다.
            Calendar cal = Calendar.getInstance();
            String upperdate = (String) mTvCalendarTitle.getText();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            try {
                cal.setTime(sdf.parse(upperdate));// all done
                Log.e("saea", cal.get(Calendar.YEAR) + "-"
                        + (cal.get(Calendar.MONTH) + 1) + "-");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.add(Calendar.MONTH, -1);
            String upperdate2;
            if((cal.get(Calendar.MONTH) + 1) < 10){
                upperdate2 = cal.get(Calendar.YEAR) + "-0"
                        + (cal.get(Calendar.MONTH) + 1) + "-";
            }else{
                upperdate2 = cal.get(Calendar.YEAR) + "-"
                        + (cal.get(Calendar.MONTH) + 1) + "-";
            }

            date.setText(upperdate2+date_str);


        }

      }

    // 전주, 다음주로 이동 및 코디 가져오기
    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.gv_calendar_activity_b_last:
                atfirst = 1;
                prenext = "pre";
                mThisMonthCalendar = getLastWeek(mCalendar);
                getCalendar(mThisMonthCalendar);
                Log.e("saea", first);
                getWeather(first);
                if(mAppItem != null){
                    mAppItem.clear();
                    getItemListbyDate(mAppItem);
                }

                break;
            case R.id.gv_calendar_activity_b_next:
                atfirst = 1;
                prenext = "next";
                mThisMonthCalendar = getNextWeek(mCalendar);
                getCalendar(mThisMonthCalendar);
                Log.e("saea", first);
                getWeather(first);
                if(mAppItem != null){
                    mAppItem.clear();
                    getItemListbyDate(mAppItem);
                }

                break;
            case R.id.select1:
                String selectedDate = (String) date.getText();
                if(selectedDate.equals("날짜를 선택하세요")){
                    Toast.makeText(this, "코디할 날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(FifthActivity.this, AddcodiActivity.class);
                    i.putExtra("date", selectedDate);
                    startActivity(i);
                }

                break;
            case R.id.select2:
                String selectedDate2 = (String) date.getText();
                if(selectedDate2.equals("날짜를 선택하세요")){
                    Toast.makeText(this, "코디할 날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i2 = new Intent(FifthActivity.this, ThirdActivity.class);
                    i2.putExtra("date", selectedDate2);
                    startActivity(i2);
                }
                break;
        }
    }

    private void initCalendarAdapter()
    {
        Calendar cal = Calendar.getInstance();
        mCalendarAdapter = new CalendarAdapter(this, R.layout.day, mDayList, cal);
        mGvCalendar.setAdapter(mCalendarAdapter);
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
                        String uid = jObj.getString("uid");
                        JSONObject codi = jObj.getJSONObject("daily");

                        CodiData codiData = new CodiData(codi.getString("uid"), codi.getString("dateval"), codi.getString("season"),
                                codi.getString("type"), codi.getString("top"), codi.getString("bottom"),
                                codi.getString("shoes"), codi.getString("outdoor"),codi.getString("bag"),codi.getString("acc"), codi.getString("tag"));

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
