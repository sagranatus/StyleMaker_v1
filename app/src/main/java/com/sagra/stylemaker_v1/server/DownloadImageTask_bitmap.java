package com.sagra.stylemaker_v1.server;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.sagra.stylemaker_v1.data.ClothData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

// 서버에서 이미지를 가져 온다.
public class DownloadImageTask_bitmap extends AsyncTask<String, Bitmap, Bitmap> {
    Bitmap mIcon11;
    Context context;
    String uid;
    ClothData clothdata;
    String profileimg;

    public DownloadImageTask_bitmap(Context context, String uid, ClothData clothdata, Bitmap bitmap) {
        this.mIcon11 = bitmap;
        this.context = context;
        this.uid = uid;
        this.clothdata = clothdata;
    }

    public DownloadImageTask_bitmap(Context context, String uid, String profileimg, Bitmap bitmap) {
        this.mIcon11 = bitmap;
        this.context = context;
        this.uid = uid;
        this.profileimg = profileimg;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "saea");
            e.printStackTrace();
        }
        ContextWrapper cw = new ContextWrapper(context);
        File root = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
        root.mkdirs();
        File fileName = null;
        if(profileimg == null){
           fileName = new File(root,clothdata.getInfo());
        }else{
            fileName = new File(root,profileimg);
        }


        try {
            FileOutputStream fOut = new FileOutputStream(fileName);
            Log.d("saea2", "this" + mIcon11.toString());
            mIcon11.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("saea2", "error");
            Log.d("saea2", "Error occured. Please try again later.");
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

    }
}
