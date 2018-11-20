package com.sagra.stylemaker_v1.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.data.CodiData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Server_DailyData {

    public static void insertDaily(final Context context, final String uid, final CodiData cData) {
       // Thread t = new Thread(new Runnable() {
        //    @Override
       //     public void run() {

                Log.i("saea", "Starting Upload...");
                insertDaily_Connect(context, uid, cData);


        //    }
    //    });
    //    t.start();

    }

    public static void insertDaily_Connect(Context context, final String uid, final CodiData codiData) {
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
                Log.d("saea","here2"+uid+codiData.getNumber());
                params.put("status", "insert");
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

