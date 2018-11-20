package com.sagra.stylemaker_v1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagra.stylemaker_v1.R;
import com.sagra.stylemaker_v1.data.Cameraspinner;

import java.util.ArrayList;


public class CameraspinnerAdapter extends ArrayAdapter<Cameraspinner> {
    ArrayList<Cameraspinner> spinner, tempCustomer, suggestions;

    public CameraspinnerAdapter(Context context, ArrayList<Cameraspinner> objects) {
        super(context, R.layout.cameraspinner_row, R.id.tvCamera, objects);
        this.spinner = objects;
        this.tempCustomer = new ArrayList<Cameraspinner>(objects);
        this.suggestions = new ArrayList<Cameraspinner>(objects);

    }
    @Override
    public Cameraspinner getItem(int position)
    {
        // TODO Auto-generated method stub
        return spinner.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, null);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        Cameraspinner spinner = getItem(position);
        if (convertView == null) {
            if (parent == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cameraspinner_row, null);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cameraspinner_row, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.tvCamera);
        ImageView ivCustomerImage = (ImageView) convertView.findViewById(R.id.ivCameraImage);
        if (txtCustomer != null)
            txtCustomer.setText(spinner.getText());


        if (ivCustomerImage != null)
            ivCustomerImage.setImageResource(spinner.getPic());

        // Now assign alternate color for rows
        if (position % 2 == 0)
            convertView.setBackgroundColor(getContext().getColor(R.color.even));
        else
            convertView.setBackgroundColor(getContext().getColor(R.color.even));
        ivCustomerImage.setImageResource(spinner.getPic());

        return convertView;
    }
}
