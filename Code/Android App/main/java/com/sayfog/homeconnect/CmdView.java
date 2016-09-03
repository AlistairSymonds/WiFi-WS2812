package com.sayfog.homeconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CmdView extends AppCompatActivity {
    LinearLayout ll;
    String bundleName;
    int deviceIndex;
    espDevice device;
    byte funcId;
    CmdBundle bundle;
    ArrayList<LinearLayout> cmdUIs = new ArrayList<LinearLayout>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmd_view);

        Intent intent = getIntent();
        bundleName = intent.getStringExtra("BUNDLE_NAME");
        deviceIndex = intent.getIntExtra("DEVICE_INDEX", 0);
        device = networkScanner.deviceList.get(deviceIndex);
        funcId = intent.getByteExtra("INTENT_FUNC_BYTE_ID", (byte)0);
        this.bundle = DefinitionLibrary.allFuncs.get(new Integer(funcId)).getBundle(bundleName);
        ll = (LinearLayout) this.findViewById(R.id.cmd_view_ll);





        for (int i = 0; i < bundle.getAllCmds().size(); i++){
            LinearLayout ui = new LinearLayout(this);
            ui.setOrientation(LinearLayout.VERTICAL);

            TextView desc = new TextView(this);
            desc.setText(bundle.getAllCmds().get(i).getName());
            ui.addView(desc);

            final EditText input = new EditText(this);
            input.setText("nop");
            if(bundle.getAllCmds().get(i).getNumberOfBytesAdded() != 0){

                ui.addView(input);
            }


            Button send = new Button(this);
            send.setEnabled(true);
            final String messageBase = bundle.getAllCmds().get(i).getMessageOutBase();
            send.setText(bundle.getAllCmds().get(i).getName());
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String extraBytes = "";
                    System.out.println("Button");
                    if(input.getText().toString().contains("nop")){
                        networkScanner.deviceList.get(deviceIndex).sendMessage(messageBase);
                    } else {
                        networkScanner.deviceList.get(deviceIndex).sendMessage(messageBase + " " + input.getText());
                    }

                }
            });
            ui.addView(send);

            cmdUIs.add(ui);
        }

        for (int i = 0; i < cmdUIs.size(); i++){
            ll.addView(cmdUIs.get(i));
        }


    }
}
