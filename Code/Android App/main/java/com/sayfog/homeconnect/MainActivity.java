package com.sayfog.homeconnect;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.sayfog.HomeConnect.MESSAGE";
    public static LinearLayout ll;
    public static ListView lv;
    public static ArrayList<espDevice> devices = new ArrayList<espDevice>();
    public static SwipeRefreshLayout swiper;
    ArrayAdapter mAdapter;
    public static espDeviceAdapter adapter;
    networkScanner scan = new networkScanner();
    public final static String DEVICE_ID = "com.sayfog.homeconnect.DEVICE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefinitionLibrary.createLibrary();
        setContentView(R.layout.activity_main);
        scan.start();
        swiper = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshList();
            }
        });
        lv = (ListView) findViewById(R.id.activity_main_listview);
    }

    private void refreshList(){
        scan.addCmd("SCAN_AND_ADD");
        System.out.println("redoing adapter");


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        redrawlv();
    }

    public void redrawlv(){
        mAdapter = new espDeviceAdapter(MainActivity.this, R.layout.esp_list_item, networkScanner.deviceList);
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), espDeviceView.class);
                System.out.println(position);
                String index = Integer.toString(position);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, index.toString());
                startActivity(intent);

            }
        });

        swiper.setRefreshing(false);
    }

}
