package com.sagra.stylemaker_v1.server;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// 서버에서 이미지를 가져 온다.
public class DownloadImageAllTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    String uid;
    Context context;

    public DownloadImageAllTask(Context context, String uid) {
        this.bmImage = bmImage;
        this.context = context;
        this.uid = uid;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;


       Document doc = null;
        try {
            doc = Jsoup.connect(urldisplay).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Element file : doc.select("a")) {
            Log.d("saea", file.attr("href")+file.text());
            String fileurl = file.attr("href");
            String filename = file.text();
            try {
                InputStream in = new java.net.URL(filename).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                ContextWrapper cw = new ContextWrapper(context);
                File root = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
                root.mkdirs();
                File fileName = new File(root,filename);

                try {
                    FileOutputStream fOut = new FileOutputStream(fileName);
                    Log.d("saea", "this" + mIcon11.toString());
                    mIcon11.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("saea", "error");
                    Log.d("saea", "Error occured. Please try again later.");
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage()+"saea");
                e.printStackTrace();
            }
        }
    /*
       try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

            ContextWrapper cw = new ContextWrapper(context);
            File root = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
            root.mkdirs();
            File fileName = new File(root,imgname);

            try {
                FileOutputStream fOut = new FileOutputStream(fileName);
                Log.d("saea", "this" + mIcon11.toString());
                mIcon11.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("saea", "error");
                Log.d("saea", "Error occured. Please try again later.");
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "saea");
            e.printStackTrace();
        } */
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
      //  bmImage.setImageBitmap(result);

    }
}
