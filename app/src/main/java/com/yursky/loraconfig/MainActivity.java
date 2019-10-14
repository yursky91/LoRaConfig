package com.yursky.loraconfig;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static Bluetooth bluetooth = new Bluetooth();

    Button btnBluetooth;
    Button btnTerminal;
    Button btnReadParam;
    Button btnWriteParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN};

        ActivityCompat.requestPermissions(this, permissions, 0);

        EventBus.getDefault().register(this);
        bluetooth.registerReceiver(this);

        btnBluetooth = findViewById(R.id.btnBluetooth);
        btnTerminal = findViewById(R.id.btnTerminal);
        btnReadParam = findViewById(R.id.btnReadParam);
        btnWriteParam = findViewById(R.id.btnWriteParam);

        btnBluetooth.setOnClickListener(this);
        btnTerminal.setOnClickListener(this);
        btnReadParam.setOnClickListener(this);
        btnWriteParam.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btnBluetooth:
                intent = new Intent(this, BluetoothActivity.class);
                startActivity(intent);
                break;

            case R.id.btnTerminal:
                intent = new Intent(this, TerminalActivity.class);
                startActivity(intent);
                break;

            case R.id.btnReadParam:
                byte[] command = {(byte) 0xC1, (byte) 0xC1, (byte) 0xC1};
                bluetooth.write(command);
                break;

            case R.id.btnWriteParam:

                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBluetoothEvent(Bluetooth.BluetoothEvent event) {
        switch (event.getAction()) {
            case Bluetooth.DISABLED:
                Toast.makeText(this, getString(R.string.blOff), Toast.LENGTH_SHORT).show();
                break;

            case Bluetooth.ENABLED:
                Toast.makeText(this, getString(R.string.blOn), Toast.LENGTH_SHORT).show();
                break;

            case Bluetooth.DISCONNECTED:
                Toast.makeText(this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                break;

            case Bluetooth.CONNECTING:
                Toast.makeText(this, getString(R.string.connectingTo) + event.getData() + "...", Toast.LENGTH_SHORT).show();
                break;

            case Bluetooth.CONNECTED:
                Toast.makeText(this, getString(R.string.connectedTo) + event.getData(), Toast.LENGTH_SHORT).show();
                break;

            case Bluetooth.MESSAGE_RECEIVED:
//                Toast.makeText(this, "" + event.getData(), Toast.LENGTH_SHORT).show();

//                System.out.println(new BigInteger(1, ((String) event.getData()).getBytes()).toString(16));
//
//                StringBuilder sb = new StringBuilder();
//                for (byte b : ((String) event.getData()).getBytes()) {
//                    sb.append(String.format("%02X ", b));
//                }
//
//                System.out.println(sb.toString());
                break;
        }
    }
}
