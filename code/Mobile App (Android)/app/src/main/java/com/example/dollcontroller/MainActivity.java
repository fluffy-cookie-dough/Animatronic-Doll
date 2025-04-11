package com.example.dollcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    String[] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private static boolean app_running_status;

    // GUI Control
    private ListView emotion_list;
    private TextView current_emotion_text, bluetooth_connection_text;
    private Button setup_button;

    // Bluetooth Connection
    private boolean connected = false;
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetooth_adapter;
    private OutputStream output_stream = null;
    private InputStream input_stream = null;

    // Message
    private final String connected_msg = "Connected to your friend! :D";
    private final String connection_failed_msg = "Failed to send emotion to your friend...";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app_running_status = true;

        // Bind UI to parameter
        emotion_list = findViewById(R.id.emotion_list);
        current_emotion_text = findViewById(R.id.current_emotion_text);
        bluetooth_connection_text = findViewById(R.id.bluetooth_connection_text);
        setup_button = findViewById(R.id.setup_button);

        // Initialize bluetooth
        this.Check_And_Get_Permission();
        bluetooth_connection_text.setText(this.Connect_To_Doll_And_Get_Msg());
        // Initialize Emotion List
        this.Initialize_Emotion_List();
        // Initialize Setup Button
        this.Initialize_Setup_button();
    }

    private void Check_And_Get_Permission() {
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, REQUEST_ENABLE_BT);
    }

    private String Connect_To_Doll_And_Get_Msg() {

        int permission_result = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        if (permission_result != PackageManager.PERMISSION_GRANTED)
            return "Bluetooth connection is unavailable";

        try
        {
            bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> devices = bluetooth_adapter.getBondedDevices();

            // When there are no connected devices
            if (devices.size() == 0)
                return "Please connect to doll";

            // Create a dialog to connect to Bluetooth
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Paired Devices list");

            List<String> deviceNameList = new ArrayList<>();
            // Add Devices to list
            for (BluetoothDevice bluetoothDevice : devices)
                deviceNameList.add(bluetoothDevice.getName());
            deviceNameList.add("Cancel");

            final CharSequence[] selectedDeviceName = deviceNameList.toArray(new CharSequence[deviceNameList.size()]);
            deviceNameList.toArray(new CharSequence[deviceNameList.size()]);

            builder.setItems(selectedDeviceName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Connect_Device(bluetooth_adapter, selectedDeviceName[which].toString());
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return "Connecting...";
        }
        catch (Exception e)
        {
            return "Failed to get the list of Bluetooth devices";
        }
    }

    @SuppressLint("MissingPermission")
    public void Connect_Device(BluetoothAdapter bluetoothAdapter, String deviceName) {

        try
        {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice device = null;
            for (BluetoothDevice bluetoothDevice : devices) {
                if (deviceName.equals(bluetoothDevice.getName())) {
                    device = bluetoothDevice;
                    break;
                }
            }

            // UUID 생성
            UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            output_stream = bluetoothSocket.getOutputStream();
            input_stream = bluetoothSocket.getInputStream();
            bluetooth_connection_text.setText(connected_msg);
            connected = true;
        }
        catch (Exception e)
        {
            bluetooth_connection_text.setText(connection_failed_msg);
        }
    }

    private void Initialize_Emotion_List() {
        // Set emotion list
        String emotions[] = EmotionDefinition.Emotions;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emotions);

        // Set emotion list items & behaviour
        emotion_list.setAdapter(arrayAdapter);
        emotion_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
            try {
                if (connected)
                {
                    // Get Emotion Angle Command
                    String selected_emotion = (String) (emotion_list.getItemAtPosition(myItemInt));
                    current_emotion_text.setText(selected_emotion);
                    String text_to_send = EmotionDefinition.Get_Action_Command(selected_emotion) + "\n";

                    // Send Command
                    output_stream.write(text_to_send.getBytes());
                    bluetooth_connection_text.setText(connected_msg);
                }
            }
            catch (Exception e)
            {
                bluetooth_connection_text.setText(connection_failed_msg);
                connected = false;
            }
        }});
    }

    private void Initialize_Setup_button() {
        setup_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SetConfiguration.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void finish() {
        app_running_status = false;
    }
}