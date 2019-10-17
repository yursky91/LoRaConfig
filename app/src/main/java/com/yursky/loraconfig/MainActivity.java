package com.yursky.loraconfig;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static Bluetooth bluetooth = new Bluetooth();

    Button btnBluetooth;
    Button btnTerminal;
    Button btnReadParam;
    Button btnWriteParam;

    LinearLayout layoutParam;
    CheckBox chkSaveHard;
    EditText editAddress;
    Spinner spinParity;
    Spinner spinUartRate;
    Spinner spinAirRate;
    EditText editChannel;
    TextView textFreq;
    Spinner spinFixed;
    Spinner spinIo;
    Spinner spinWor;
    Spinner spinFec;
    Spinner spinPower;

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

        layoutParam = findViewById(R.id.layoutParam);
        chkSaveHard = findViewById(R.id.chkSaveHard);
        editAddress = findViewById(R.id.editAddress);
        spinParity = findViewById(R.id.spinParity);
        spinUartRate = findViewById(R.id.spinUartRate);
        spinAirRate = findViewById(R.id.spinAirRate);
        editChannel = findViewById(R.id.editChannel);
        textFreq = findViewById(R.id.textFreq);
        spinFixed = findViewById(R.id.spinFixed);
        spinIo = findViewById(R.id.spinIo);
        spinWor = findViewById(R.id.spinWor);
        spinFec = findViewById(R.id.spinFec);
        spinPower = findViewById(R.id.spinPower);

        TextView.OnEditorActionListener editorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.equals(editChannel))
                        textFreq.setText(410 + Integer.valueOf(v.getText().toString()) + " MHz");

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                    return true;

                } else
                    return false; // pass on to other listeners.
            }
        };

        editAddress.setOnEditorActionListener(editorActionListener);
        editChannel.setOnEditorActionListener(editorActionListener);
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
                bluetooth.write("mode_3".getBytes());
                SystemClock.sleep(1500);

                byte[] command = {(byte) 0xC1, (byte) 0xC1, (byte) 0xC1};
                bluetooth.write(command);
                break;

            case R.id.btnWriteParam:
                bluetooth.write("mode_3".getBytes());
                SystemClock.sleep(1500);

                bluetooth.write(getLayoutData().getBytes());
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
                String message = (String) event.getData();
                System.out.println(message);

                if (message.contains("hex")) {
                    LoraParam loraParam = null;
                    try {
                        loraParam = new LoraParam(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    setLayoutData(loraParam);
                    bluetooth.write("mode_0".getBytes());
                }
                break;
        }
    }

    void setLayoutData(LoraParam loraParam) {
        chkSaveHard.setChecked(loraParam.saveHard);
        editAddress.setText("" + loraParam.address);
        spinParity.setSelection(Integer.parseInt(loraParam.parity, 2));
        spinUartRate.setSelection(Integer.parseInt(loraParam.uartRate, 2));
        spinAirRate.setSelection(Integer.parseInt(loraParam.airRate, 2));
        editChannel.setText("" + loraParam.channel);
        textFreq.setText(410 + loraParam.channel + " MHz");
        spinFixed.setSelection(Integer.parseInt(loraParam.fixedMode, 2));
        spinIo.setSelection(Integer.parseInt(loraParam.ioMode, 2));
        spinWor.setSelection(Integer.parseInt(loraParam.worTiming, 2));
        spinFec.setSelection(Integer.parseInt(loraParam.fec, 2));
        spinPower.setSelection(3 - Integer.parseInt(loraParam.power, 2));

        Toast.makeText(this, "Параметры обновлены", Toast.LENGTH_SHORT).show();

        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(100);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        layoutParam.startAnimation(anim);
    }

    LoraParam getLayoutData() {
        LoraParam loraParam = new LoraParam();

        loraParam.saveHard = chkSaveHard.isChecked();
        loraParam.address = Integer.valueOf(editAddress.getText().toString());
        loraParam.parity = Integer.toBinaryString(0x4 | spinParity.getSelectedItemPosition()).substring(1);
        loraParam.uartRate = Integer.toBinaryString(0x8 | spinUartRate.getSelectedItemPosition()).substring(1);
        loraParam.airRate = Integer.toBinaryString(0x8 | spinAirRate.getSelectedItemPosition()).substring(1);
        loraParam.channel = Integer.valueOf(editChannel.getText().toString());
        loraParam.fixedMode = Integer.toBinaryString(0x2 | spinFixed.getSelectedItemPosition()).substring(1);
        loraParam.ioMode = Integer.toBinaryString(0x2 | spinIo.getSelectedItemPosition()).substring(1);
        loraParam.worTiming = Integer.toBinaryString(0x8 | spinWor.getSelectedItemPosition()).substring(1);
        loraParam.fec = Integer.toBinaryString(0x2 | spinFec.getSelectedItemPosition()).substring(1);
        loraParam.power = Integer.toBinaryString(0x4 | (3 - spinPower.getSelectedItemPosition())).substring(1);

        return loraParam;
    }
}


