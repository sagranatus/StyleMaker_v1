package com.sagra.stylemaker_v1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.StyleData;
import com.sagra.stylemaker_v1.server.DownloadImageTask;

import java.io.File;
import java.util.ArrayList;

public class StyleAdapter extends ArrayAdapter<StyleData> {
    class ViewHolder{
        public ImageView imageView = null;
        public TextView textView = null;
    }
    private LayoutInflater mInflater = null;

    public StyleAdapter(@NonNull Context context, int resource, ArrayList<StyleData> aGridItem) {
        super(context, resource, aGridItem);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return super.getCount();
    }

    @Override
    public StyleData getItem(int position){
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
            view = mInflater.inflate(R.layout.style_item_layout, null);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.eachstyle);
              viewHolder.textView = (TextView)view.findViewById(R.id.appname);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        StyleData gridItem = getItem(position);
        String type = gridItem.getNumber();
        // 신발인 경우에 height를 반으로 줄인다 (왜냐하면 이미지 저장시에 height가 반이므로)

        // path to bitmap 정보 가져오기

        String filePlace = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker" + File.separator + gridItem.getNumber()+".png";
        Bitmap bitmap = BitmapFactory.decodeFile(filePlace);
        Log.d("saea", filePlace);

        new DownloadImageTask(viewHolder.imageView).execute("https://ssagranatus.cafe24.com/files/user" + gridItem.getUid() + "/" + gridItem.getNumber()+".png");
        return view;
    }

}
