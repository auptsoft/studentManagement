package com.example.andrewoshodin.fingerprintregister;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.BluetoothConnectionInterface;
import com.example.andrewoshodin.fingerprintregister.comm.BluetoothManager;
import com.example.andrewoshodin.fingerprintregister.comm.IOCommunication;
import com.example.andrewoshodin.fingerprintregister.models.AppState;

/**
 * Created by Andrew Oshodin on 8/22/2018.
 */

public class TakeFingerprintActivity extends AppCompatActivity {
    public static final String RESULT_CODE = "FINGERPRINT_RESULT_CODE";
    Toolbar toolbar;

    TextView connectionStatusView;
    ProgressBar progressBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_fingerprint_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Take Fingerprint");

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_id);
        connectionStatusView = (TextView)findViewById(R.id.connection_status_id);

        getSupportFragmentManager().beginTransaction().add(R.id.fingerprint_frame_layout_id, new TakeFingerprintFragment())
                .commit();

        if (!AppState.bluetoothManager.connectionState) {

        } else {
            connectionStatusView.setText("Connected");
            connectionStatusView.setTextColor(Color.GREEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem showDevicesMenu = menu.add(1, 1, 1, "Show Devices");
        showDevicesMenu.setIcon(R.drawable.ic_bluetooth_connected_black_24dp);

        MenuItem connectToDeviceMenu = menu.add(2, 2, 2, "Connect");
        connectToDeviceMenu.setIcon(R.drawable.ic_bluetooth_searching_black_24dp);
        connectToDeviceMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                new DeviceListFragment().show(getSupportFragmentManager(), "ShowDevices");
                break;

            case 2:
                progressBar.setVisibility(View.VISIBLE);
                connectionStatusView.setText("Connecting...");
                connectionStatusView.setTextColor(Color.YELLOW);
                String address = AppState.sharedPreferences.getString(AppState.BLUETOOTH_ADDRESS_PROPERTY,
                        "98:D3:61:F5:DC:E5");
                AppState.bluetoothManager.connectToDeviceAsync(address, new BluetoothConnectionInterface() {
                    @Override
                    public void onConnected(BluetoothSocket bluetoothSocket) {
                        Toast.makeText(getBaseContext(), "Connected to Fingerprint Module", Toast.LENGTH_LONG).show();
                        connectionStatusView.setText("Connected");
                        connectionStatusView.setTextColor(Color.GREEN);
                        progressBar.setVisibility(View.GONE);
                        AppState.bluetoothManager.connectionState = true;
                        AppState.ioCommunication = new IOCommunication(AppState.bluetoothManager.getInputStream(),
                                AppState.bluetoothManager.getOutputStream());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getBaseContext(), "Could not connect to Module", Toast.LENGTH_LONG).show();
                        connectionStatusView.setText("Not connected");
                        connectionStatusView.setTextColor(Color.RED);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}