package com.yursky.loraconfig;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        EventBus.getDefault().register(this);

        textTerminal = findViewById(R.id.textTerminal);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        textTerminal.setMovementMethod(new ScrollingMovementMethod());
        textTerminal.setText(MainActivity.terminal);
        btnSend.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.terminal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnClear:
                MainActivity.terminal = "";
                textTerminal.setText("");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                MainActivity.bluetooth.write((editMessage.getText() + "\n").getBytes());

                if (MainActivity.bluetooth.getStatus() == Bluetooth.CONNECTED) {
                    String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    textTerminal.append("\n\n" + ">" + time + ": " + editMessage.getText());
                    MainActivity.terminal += "\n\n" + ">" + time + ": " + editMessage.getText();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBluetoothEvent(Bluetooth.BluetoothEvent event) {
        switch (event.getAction()) {
            case Bluetooth.MESSAGE_RECEIVED:
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                textTerminal.append("\n\n" + time + ": " + event.getData());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
