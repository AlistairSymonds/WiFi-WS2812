package com.sayfog.homeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class BundleView extends AppCompatActivity {
    int deviceIndex;
    espDevice device;
    HashMap<String, CmdBundle> bundleMap;
    byte funcId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_dev_command_view);
        final Intent intent = getIntent();

        funcId = intent.getByteExtra("INTENT_FUNC_BYTE_ID", (byte)0);
        deviceIndex = intent.getIntExtra("INTENT_DEVICE_INDEX", 0);
        device = networkScanner.deviceList.get(deviceIndex);


        ListView lv = (ListView) findViewById(R.id.generic_cmd_lv);
        bundleMap = DefinitionLibrary.getDevFunc(new Integer(funcId)).getAllBundles();
        Set<String> keySet = bundleMap.keySet();

        final ArrayList<CmdBundle> bundlesList = new ArrayList<CmdBundle>();
        for(String s : keySet){
            bundlesList.add(bundleMap.get(s));
        }

        final ArrayAdapter bundleAdapter = new BundleListAdapter(this, R.layout.func_list_item, bundlesList);

        lv.setAdapter(bundleAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intentOut = new Intent(getApplicationContext(), bundleMap.get(bundlesList.get(position).getName()).getViewClass());
                intentOut.putExtra("BUNDLE_NAME", bundlesList.get(position).getName());
                intentOut.putExtra("DEVICE_INDEX", deviceIndex);
                intentOut.putExtra("INTENT_FUNC_BYTE_ID", funcId);
                startActivity(intentOut);

            }
        });


    }

}
