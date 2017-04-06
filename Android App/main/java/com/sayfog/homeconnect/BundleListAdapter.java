package com.sayfog.homeconnect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alist on 27/08/2016.
 */
public class BundleListAdapter extends ArrayAdapter<CmdBundle> {
    Context context;
    int layoutResourceId;
    ArrayList<CmdBundle> data = null;

    public BundleListAdapter(Context context, int layoutResourceId, ArrayList<CmdBundle> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        CmdBundle bundle = data.get(position);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        TextView firstLine = (TextView) row.findViewById(R.id.func_first_line);




        firstLine.setText(bundle.getName());






        return row;
    }

}
