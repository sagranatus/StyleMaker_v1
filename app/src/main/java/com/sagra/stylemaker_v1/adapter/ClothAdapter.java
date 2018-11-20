package com.sagra.stylemaker_v1.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.ClothData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ClothAdapter extends ArrayAdapter<ClothData>{
    class ViewHolder{
        public ImageView imageView = null;
        public TextView textView = null;

    }
    private LayoutInflater mInflater = null;
    private Context mContext = null;

    public ClothAdapter(@NonNull Context context, int resource, ArrayList<ClothData> aGridItem) {
        super(context, resource, aGridItem);
        mInflater = LayoutInflater.from(context);
        mContext  = context;
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
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        ViewHolder viewHolder = new ViewHolder();
        if(null == view){
            view = mInflater.inflate(R.layout.cloth_item_layout, null);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.eachcloth);
          //  viewHolder.textView = (TextView)view.findViewById(R.id.appname);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        ClothData gridItem = getItem(position);
        String type = gridItem.getType();
        // 신발인 경우에 height를 반으로 줄인다 (왜냐하면 이미지 저장시에 height가 반이므로)
        if(type.equals("신발")){
            viewHolder.imageView.getLayoutParams().height = (int) getContext().getResources().getDimension(R.dimen.shoes_height);
        }else{
            viewHolder.imageView.getLayoutParams().height = (int) getContext().getResources().getDimension(R.dimen.else_height);
        }
        // path to bitmap 정보 가져오기

      //  new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user"+gridItem.getUid()+"/"+gridItem.getInfo()); // 가져와서 이미지 보이기?
        ContextWrapper cw = new ContextWrapper(mContext);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir_user"+gridItem.getUid(), Context.MODE_PRIVATE);
        try {
            File f=new File(directory, gridItem.getInfo());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
          //  viewHolder.imageView.setImageBitmap(b);
            Glide.with(mContext).load(f).into(viewHolder.imageView);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return view;
    }

}
