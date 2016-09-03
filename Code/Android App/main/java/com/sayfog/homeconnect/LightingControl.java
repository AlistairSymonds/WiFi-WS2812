package com.sayfog.homeconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LightingControl extends AppCompatActivity {
    espDevice device;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_control);
        device = networkScanner.deviceList.get(0);
        input = (EditText) findViewById(R.id.raw_serial_in);
    }

    public void sendMessage(View view){
        device.sendMessage(input.getText().toString());
    }

    public void sendRainbowCmd(View view){
        device.sendMessage("0 3 0 7");
    }

    public void sendAudioCmd(View view){
        device.sendMessage("0 2 11");
    }
}
