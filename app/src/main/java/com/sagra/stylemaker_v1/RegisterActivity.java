package com.sagra.stylemaker_v1;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sagra.stylemaker_v1.etc.HideKeyboard;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.Server_UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    String gender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        inputFullName.setBackgroundResource(R.drawable.edittext_bg);
        inputEmail.setBackgroundResource(R.drawable.edittext_bg);
        inputPassword.setBackgroundResource(R.drawable.edittext_bg);
        btnRegister.setBackgroundResource(R.drawable.button_bg);

        Spinner genderSpinner = (Spinner)findViewById(R.id.gender);
        genderSpinner.setOnItemSelectedListener(this);

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        HideKeyboard.setupUI(ll, RegisterActivity.this);

        // 성별 스피너 설정
        String[] plants = new String[]{
                "성별을 선택해주세요",
                "남자",
                "여자"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
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


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager // 여기서 sharedpreference에 값 생성
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                int place = email.indexOf('@');
                String id = email.substring(0, place);
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()  && !gender.isEmpty()) {
                    String[] info = {name, email, password, gender, id};

                    Server_UserData.registerUser(RegisterActivity.this, session, pDialog,name, email, password, gender, id); // cf : 여기서 등록함

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

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
}