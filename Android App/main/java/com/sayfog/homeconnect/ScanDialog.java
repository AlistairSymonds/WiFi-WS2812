package com.sayfog.homeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScanDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_dialog);

        Intent intent = getIntent();
        String message = intent.getStringExtra("debug test");
        System.out.println(message);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        TextView scanText = new TextView(this);
        scanText.setTextSize(40);
        scanText.setText("I like scanning");

        ViewGroup layout = (ViewGroup) findViewById(R.id.content_scan_dialog);
        layout.addView(textView);
        layout.addView(scanText);
    }

}
