package com.sagra.stylemaker_v1.server;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sagra.stylemaker_v1.ProfilepicActivity;
import com.sagra.stylemaker_v1.SecondActivity;
import com.sagra.stylemaker_v1.data.ClothData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server_ClothData {

    public static void insertCloth(final Context context, final String uid, final ClothData cData) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");
                String season = cData.getSeason();
                String type = cData.getType();
                String info = cData.getInfo();
                String detail1 = cData.getDetail1();
                String detail2 = cData.getDetail2();
                insertCloth_Connect(context, uid, season, type, info, detail1, detail2);


            }
        });
        t.start();

    }


    public static void selectClothbySeasonAndDetail(final Context context, final String uid, final ClothData cData, ArrayList<ClothData> mAppItem) {

    //    Thread t = new Thread(new Runnable() {
        //    @Override
         //   public void run() {

                Log.i("saea", "Starting Upload...");
                String season = cData.getSeason();
                String type = cData.getType();
                String detail1 = cData.getDetail1();
                String detail2 = cData.getDetail2();
                selectClothbySeasonAndDetail_Connect(context, uid, season, type, detail1, detail2, mAppItem);
                Log.d("js", "fourth"+String.valueOf(mAppItem.size()));
                // status = "select_cloth_season_type_detail";

         //   }
  //      });
      //  t.start();
   //     try {
    //        t.join();
    //        Log.d("js", String.valueOf(mAppItem.size()));
     //   } catch (InterruptedException e) {
     //       e.printStackTrace();
     //   }
    }


    public static void insertCloth_Connect(Context context, final String uid, final String season, final String type, final String info, final String detail1, final String detail2) {
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
                params.put("status", "insert");
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



    public static void selectClothbySeasonAndDetail_Connect(final Context context, final String uid, final String season, final String type, final String detail1, final String detail2, final ArrayList<ClothData> mAppItem) {
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
                    Log.d("saea", error+"saea");


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
                                Log.d("js", "second"+String.valueOf(clothItems.size()));
                            }

                            Log.d("saea", "resultfirst");
                            Intent i = new Intent(context,
                                    SecondActivity.class);
                            i.putExtra("info_season", season);
                            i.putExtra("info_type", type);
                            i.putExtra("info_detail1", detail1);
                            i.putExtra("info_results", clothItems);

                            context.startActivity(i);
                        }else{
                            Intent i = new Intent(context,
                                    SecondActivity.class);
                            i.putExtra("info_season", season);
                            i.putExtra("info_type", type);
                            i.putExtra("info_detail1", detail1);

                            context.startActivity(i);
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
                Log.e("saea", "insert: " + uid+season+type+detail1+detail2);
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
        Log.d("js", "third"+String.valueOf(mAppItem.size()));

    }
}
