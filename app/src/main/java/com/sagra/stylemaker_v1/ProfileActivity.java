package com.sagra.stylemaker_v1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.DB.UserDBSqlData;
import com.sagra.stylemaker_v1.data.UserData;
import com.sagra.stylemaker_v1.etc.HideKeyboard;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.DownloadImageTask;
import com.sagra.stylemaker_v1.server.Server_UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "saea";
    private Button btnLogout;
    public ImageView profileImg;
    private SessionManager session;

    Context mContext;
    EditText name_et, userId_et, email_et;
    String uid;
    String title;

    String name, user_id, email, gender, password, profile_pic;
    Button changeImg;
    Spinner genderSpinner;
    ArrayAdapter<String> spinnerArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // actionbar setting
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("프로필 수정");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        name_et = (EditText) findViewById(R.id.name);
        userId_et = (EditText) findViewById(R.id.userid);
        email_et = (EditText) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        profileImg = (ImageView) findViewById(R.id.image);


        int color = Color.parseColor("#6E6E6E");
        name_et.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        userId_et.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        email_et.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        changeImg = (Button) findViewById(R.id.changeImg);
        changeImg.setOnClickListener(this);
        genderSpinner = (Spinner)findViewById(R.id.gender);
        genderSpinner.setOnItemSelectedListener(this);

        // Initializing a String Array
        String[] plants = new String[]{
                "성별을 선택해주세요",
                "남자",
                "여자"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,plantsList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(spinnerArrayAdapter);

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        HideKeyboard.setupUI(ll, ProfileActivity.this);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        session = new SessionManager(getApplicationContext());

        uid = session.getUid();
        Log.d("saea", uid);
       // getUser(ProfileActivity.this, profileImg, uid);
        ContextWrapper cw = new ContextWrapper(ProfileActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
        ArrayList<UserData> userdata = new ArrayList<UserData>();
        DBManager dbMgr = new DBManager(ProfileActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectUserData(UserDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
        dbMgr.dbClose();
        if(userdata.get(0) != null) {
            UserData udata = userdata.get(0);
            try {
                File f = new File(directory, udata.getProfile());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // profileImg.setImageBitmap(b);
                Glide.with(ProfileActivity.this).load(f).into(profileImg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            profile_pic = udata.getProfile();
            name = udata.getName();
            user_id = udata.getUserid();
            email = udata.getEmail();
            gender = udata.getGender();
            name_et.setText(udata.getName(), TextView.BufferType.EDITABLE);
            userId_et.setText(udata.getUserid());
            email_et.setText(udata.getEmail(), TextView.BufferType.EDITABLE);
            genderSpinner.setSelection(((ArrayAdapter<String>)spinnerArrayAdapter).getPosition(udata.getGender()));
        }



        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        }); // 로그아웃 클릭

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_one:

                name = name_et.getText().toString();
                email = email_et.getText().toString();
                user_id = userId_et.getText().toString();
                Log.d("saea", name+email+gender+user_id+"d"+uid);

                Server_UserData.updateUser_profile(ProfileActivity.this, uid, name, email, password, gender, user_id, profile_pic);
                return true;
            default:
                finish();
              return true;
        }
    }



    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
   // 로그아웃할때
    private void logoutUser() {

        session.setLogin(false, "");

        // Launching the login activity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gender:

                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    gender =  selectedItemText;
                    //  Toast.makeText(getApplicationContext(), "Selected : " + gender, Toast.LENGTH_SHORT).show();

                }

                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




    @Override
    public void onClick(View v) {
        Log.d("saea", uid+name+email+password+gender+user_id);
        Intent i = new Intent(ProfileActivity.this, ProfilepicActivity.class);
        String[] info = {name, email, password,  gender, user_id};
        i.putExtra("uid", uid);
        i.putExtra("imgpath", profile_pic);
        i.putExtra("info", info);
        startActivity(i);
    }



    public void getUser(final Context context, final ImageView imageview, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        final ArrayList<UserData>  userData =  new ArrayList<UserData>();

        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        name = user.getString("name");
                        user_id = user.getString("user_id");
                        email = user.getString("email");
                        gender = user.getString("gender");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다
                        profile_pic = user.getString("profile_pic");
                        Log.d("saea", uid);


                        // Displaying the user details on the screen
                        name_et.setText(name, TextView.BufferType.EDITABLE);
                        userId_et.setText(user_id);
                        email_et.setText(email, TextView.BufferType.EDITABLE);
                        genderSpinner.setSelection(((ArrayAdapter<String>)spinnerArrayAdapter).getPosition(gender));
                            new DownloadImageTask(imageview).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+profile_pic);

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg+"1111", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage()+"2222", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "get");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}