package com.sagra.stylemaker_v1.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.ClothData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter<ClothData> {
    Context context;
    int layout;
    ArrayList<ClothData> mAppItem;
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, ArrayList<ClothData> mAppItem ) {
        super(context, layout, mAppItem);
        this.context = context;
        this.layout = layout;
        this.mAppItem = mAppItem;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return super.getCount();
    }

    @Override
    public ClothData getItem(int position){
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position){
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 보여줄 해당행의 row xml 파일의 데이터를 셋팅해서 뷰를 완성하는 작업
        if (convertView == null) {
            convertView = inf.inflate(layout, null);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);
        ClothData gridItem = getItem(position);
      //  new DownloadImageTask(iv).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getInfo());
        ContextWrapper cw = new ContextWrapper(context);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+gridItem.getUid(), Context.MODE_PRIVATE);
        try {
            File f=new File(directory, gridItem.getInfo());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
          //  iv.setImageBitmap(b);
            Glide.with(context).load(f).into(iv);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return convertView;
    }
}


