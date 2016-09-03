package com.sayfog.homeconnect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alist on 25/08/2016.
 */
public class espDeviceAdapter extends ArrayAdapter<espDevice> {

    Context context;
    int layoutResourceId;
    ArrayList<espDevice> data = null;

    public espDeviceAdapter(Context context, int layoutResourceId, ArrayList<espDevice> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        espDevice device = data.get(position);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        TextView firstLine = (TextView) row.findViewById(R.id.firstLine);
        firstLine.setText(espDevice.bytesToHex(device.getDeviceID()));

        TextView secondLine = (TextView) row.findViewById(R.id.secondLine);
        secondLine.setText(device.toString());



        return row;
    }


}