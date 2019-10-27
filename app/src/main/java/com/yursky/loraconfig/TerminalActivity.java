package com.yursky.loraconfig;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TerminalActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textTerminal;
    EditText editMessage;
    ImageButton btnSend;

    static String terminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        EventBus.getDefault().register(this);

        textTerminal = findViewById(R.id.textTerminal);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        textTerminal.setMovementMethod(new ScrollingMovementMethod());
        textTerminal.setText(terminal);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                textTerminal.append("\n\n" + time + ">>>: " + editMessage.getText());
                MainActivity.bluetooth.write((editMessage.getText() + "\n").getBytes());
                terminal = textTerminal.getText().toString();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBluetoothEvent(Bluetooth.BluetoothEvent event) {
        switch (event.getAction()) {
            case Bluetooth.MESSAGE_RECEIVED:
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                textTerminal.append("\n\n" + time + ": " + event.getData());
                terminal = textTerminal.getText().toString();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
