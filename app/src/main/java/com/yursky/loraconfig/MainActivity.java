package com.yursky.loraconfig;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    static Bluetooth bluetooth = new Bluetooth();

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

                break;
        }
    }
}
