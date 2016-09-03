package com.sayfog.homeconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class espDeviceView extends AppCompatActivity {
    ListView functionsList;
    espDevice device;
    int deviceIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esp_device_view);
        Intent i = this.getIntent();

        String indexStr = "";
        indexStr = i.getStringExtra(MainActivity.DEVICE_ID);

        indexStr = "0";

        for (int x = 0; x < networkScanner.deviceList.size(); x++){
            System.out.println(networkScanner.deviceList.get(x));
        }

        System.out.println("indexstr is " +indexStr);
        deviceIndex = Integer.parseInt(indexStr);
        System.out.println("index is " +deviceIndex);
        device = networkScanner.deviceList.get(deviceIndex);


        TextView humanName = (TextView) findViewById(R.id.human_name);
        humanName.setText(device.getHumanName());

        DeviceFunctionDefinition DEBUG = new DeviceFunctionDefinition((byte)-1, "DEBUG", BundleView.class);
        CmdBundle SERIAL_CMDS = new CmdBundle("SERIAL_CMDS",SerialCmds.class);
        DEBUG.addBundle(SERIAL_CMDS);
        device.getDefinedFuncs().add(DEBUG);

        TextView deviceID = (TextView) findViewById(R.id.device_id);
        deviceID.setText(espDevice.bytesToHex(device.getDeviceID()));

        functionsList = (ListView) findViewById(R.id.funcs_list);
        ArrayAdapter funcListAdapter = new FunctionsListAdapter(this, R.layout.func_list_item, device.getDefinedFuncs());
        functionsList.setAdapter(funcListAdapter);

        functionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (device.getDefinedFuncs().get(position).getViewClass() == null &&
                        device.getDefinedFuncs().get(position).getByteID() == 0){
                    System.out.println("Unknown func AND no view class");
                } else if(device.getDefinedFuncs().get(position).getViewClass() == null){
                    System.out.println("No view class defined");
                } else if(device.getDefinedFuncs().get(position).getViewClass() != null){
                    //do stuff and make the view
                    Intent intent = new Intent(getApplicationContext(), device.getDefinedFuncs().get(position).getViewClass());
                    intent.putExtra("INTENT_FUNC_BYTE_ID",device.getDefinedFuncs().get(position).getByteID());
                    intent.putExtra("INTENT_DEVICE_INDEX", deviceIndex);
                    startActivity(intent);
                }





            }
        });




    }

    public void lights(View view){
        Intent intent = new Intent(getApplicationContext(), LightingControl.class);
        startActivity(intent);
    }
}
