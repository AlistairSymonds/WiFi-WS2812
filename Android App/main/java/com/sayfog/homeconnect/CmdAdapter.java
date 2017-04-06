
package com.sayfog.homeconnect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alist on 27/08/2016.
 */
/*
public class CmdAdapter extends ArrayAdapter<CmdDefinition> {

    Context context;
    int layoutResourceId;
    ArrayList<CmdDefinition> data = null;
    int deviceIndex;
    espDevice device;

    public CmdAdapter(Context context, int layoutResourceId, ArrayList<CmdDefinition> data, espDevice device){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.device = device;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;



        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        System.out.println("YO DAWGPLS DONT BREK");
        System.out.println("Hellow World!");
        System.out.println(data.get(position).getName());
        System.out.println("wer iz mah nam");

        TextView cmdNameText = (TextView) row.findViewById(R.id.command_name);
        cmdNameText.setText(data.get(position).getName());

        final ArrayList<EditText> bytesToSend = new ArrayList<EditText>();
        if (data.get(position).getNumberOfBytesAdded() > 0){
            EditText et = new EditText(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.BELOW);

            et.setLayoutParams(params);
            bytesToSend.add(et);
        }

        Button sendBtn = (Button) row.findViewById(R.id.cmd_send_btn);
        sendBtn.setText("Set " + data.get(position).getName());

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String extraBytes = "";
                for (int i = 0; i < bytesToSend.size(); i++){
                    extraBytes = bytesToSend.get(i) + " ";
                }
                networkScanner.deviceList.get(deviceIndex).sendMessage(data.get(position).getMessageOutBase() + " " + bytesToSend);
            }
        });
        return row;
    }
}
*/