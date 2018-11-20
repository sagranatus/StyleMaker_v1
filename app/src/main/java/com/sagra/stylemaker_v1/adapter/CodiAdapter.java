package com.sagra.stylemaker_v1.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.CodiData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CodiAdapter extends ArrayAdapter<CodiData> {
    class ViewHolder{
        public ImageView imageView = null;
        public ImageView imageView2 = null;
        public ImageView imageView3 = null;
        public ImageView imageView4 = null;
        public ImageView imageView5 = null;
        public ImageView imageView6 = null;
        public TextView textView = null;
    }
    private LayoutInflater mInflater = null;
    private Context mContext;
    boolean calendar;
    public CodiAdapter(@NonNull Context context, int resource, ArrayList<CodiData> aGridItem, boolean mCalendar) {
        super(context, resource, aGridItem);
        mInflater = LayoutInflater.from(context);
        calendar = mCalendar;
        mContext = context;
    }

    @Override
    public int getCount(){
        return super.getCount();
    }

    @Override
    public CodiData getItem(int position){
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position){
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        ViewHolder viewHolder = new ViewHolder();
        if(null == view){
            view = mInflater.inflate(R.layout.codi_item_layout, null);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.appicon);
            viewHolder.imageView2 = (ImageView)view.findViewById(R.id.appicon2);
            viewHolder.imageView3 = (ImageView)view.findViewById(R.id.appicon3);
            viewHolder.imageView4 = (ImageView)view.findViewById(R.id.appicon4);
            viewHolder.imageView5 = (ImageView)view.findViewById(R.id.appicon5);
            viewHolder.imageView6 = (ImageView)view.findViewById(R.id.appicon6);
            viewHolder.textView = (TextView)view.findViewById(R.id.appdate);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        //일단 비우고~!
        viewHolder.imageView.setImageBitmap(null);
        viewHolder.imageView2.setImageBitmap(null);
        viewHolder.imageView3.setImageBitmap(null);
        viewHolder.imageView4.setImageBitmap(null);
        viewHolder.imageView5.setImageBitmap(null);
        viewHolder.imageView6.setImageBitmap(null);

        CodiData gridItem = getItem(position);
        String path = gridItem.getTop();


        if(path.contains("dress")){
            Log.d("saea", "원피스!!");
            viewHolder.imageView.getLayoutParams().height = (int) getContext().getResources().getDimension(R.dimen.dress_height);
            viewHolder.imageView2.getLayoutParams().height = (int) getContext().getResources().getDimension(R.dimen.makedress_iv2height);
        }else{
            viewHolder.imageView.getLayoutParams().height = (int) getContext().getResources().getDimension(R.dimen.top_height);
            viewHolder.imageView2.getLayoutParams().height = (int) getContext().getResources().getDimension(R.dimen.top_height);
        }
        ContextWrapper cw = new ContextWrapper(mContext);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+gridItem.getUid(), Context.MODE_PRIVATE);

        if(!gridItem.getTop().isEmpty()){

           // new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getTop());
            try {
                File f=new File(directory, gridItem.getTop());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
              //  viewHolder.imageView.setImageBitmap(b);
                Glide.with(mContext).load(f).into(viewHolder.imageView);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!gridItem.getBottom().isEmpty()) {
           // new DownloadImageTask(viewHolder.imageView2).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getBottom());
            try {
                File f=new File(directory, gridItem.getBottom());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // viewHolder.imageView2.setImageBitmap(b);
                Glide.with(mContext).load(f).into(viewHolder.imageView2);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!gridItem.getShoes().isEmpty()) {
         //   new DownloadImageTask(viewHolder.imageView3).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getShoes());
            try {
                File f=new File(directory, gridItem.getShoes());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // viewHolder.imageView3.setImageBitmap(b);
                Glide.with(mContext).load(f).into(viewHolder.imageView3);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!gridItem.getOuter().isEmpty()) {
         //   new DownloadImageTask(viewHolder.imageView4).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getOuter());
            try {
                File f=new File(directory, gridItem.getOuter());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
               // viewHolder.imageView4.setImageBitmap(b);
                Glide.with(mContext).load(f).into(viewHolder.imageView4);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!gridItem.getBag().isEmpty()) {
         //   new DownloadImageTask(viewHolder.imageView5).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getBag());
            try {
                File f=new File(directory, gridItem.getBag());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
              //  viewHolder.imageView5.setImageBitmap(b);
                Glide.with(mContext).load(f).into(viewHolder.imageView5);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(!gridItem.getAccessories().isEmpty()) {
         //   new DownloadImageTask(viewHolder.imageView6).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getAccessories());
            try {
                File f=new File(directory, gridItem.getAccessories());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
              //  viewHolder.imageView6.setImageBitmap(b);
                Glide.with(mContext).load(f).into(viewHolder.imageView6);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if(calendar == true){
            viewHolder.textView.setText(gridItem.getNumber());
        }

        return view;
    }

}
