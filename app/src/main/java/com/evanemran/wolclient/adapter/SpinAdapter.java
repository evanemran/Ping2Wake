package com.evanemran.wolclient.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.evanemran.wolclient.Device;

import java.util.List;

public class SpinAdapter extends ArrayAdapter<Device> {

    private Context context;
    private List<Device> list;

    public SpinAdapter(Context context, int textViewResourceId,
                       List<Device> list) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Device getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.WHITE);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(list.get(position).getDeviceName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(list.get(position).getDeviceName());

        return label;
    }
}
