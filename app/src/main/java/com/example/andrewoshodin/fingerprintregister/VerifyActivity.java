package com.example.andrewoshodin.fingerprintregister;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.BluetoothConnectionInterface;
import com.example.andrewoshodin.fingerprintregister.comm.IOCommunication;
import com.example.andrewoshodin.fingerprintregister.comm.MFingerprintManager;
import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Student;

import java.util.ArrayList;

/**
 * Created by Andrew Oshodin on 8/22/2018.
 */

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener{

    TextView connectionStatusView;
    ProgressBar progressBar;

    Button prepareModule, verify;

    LinearLayout uploadLayoutView;
    LinearLayout verifyLayoutView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_activity);

        getSupportActionBar().setTitle(AppState.activeCourse.getCourseCode()+" "+"Verification");
        getSupportActionBar().setSubtitle(AppState.activeCourse.getCourseTitle());

        uploadLayoutView = (LinearLayout)findViewById(R.id.upload_finger_print_view_layout_id);
        verifyLayoutView = (LinearLayout)findViewById(R.id.verify_view_layout_id);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_id);
        connectionStatusView = (TextView)findViewById(R.id.connection_status_id);

        prepareModule = (Button)findViewById(R.id.prepare_module_id);
        verify = (Button)findViewById(R.id.verify_id);


        prepareModule.setOnClickListener(this);
        connectionStatusView.setOnClickListener(this);

        if (!AppState.bluetoothManager.connectionState) {

        } else {
            connectionStatusView.setText("Connected");
            connectionStatusView.setTextColor(Color.GREEN);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(prepareModule)) {
            if (AppState.bluetoothManager.connectionState) {
                prepareModule();
            } else {
                sendToast("Not connected. \n Tap on Bluetooth symbol above to connect");
            }
        } else if (view.equals(verify)) {

            if (AppState.bluetoothManager.connectionState) {
                verify();
            } else {
                sendToast("Not connected. \n Tap on Bluetooth symbol above to connect");
            }
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

    void prepareModule(){
        int size = AppState.allActiveStudent.size();
        sendToast(size+""); //debug
        if (size < 1) {
            sendToast("No student is registered");
            return;
        }

        ArrayList<String> fingerPrintTemplates = new ArrayList<>();
        for (Student student : AppState.allActiveStudent) {
            fingerPrintTemplates.add(student.getFingerPrintTemplate());
        }

        for (int index=0; index<size; index++){
            AppState.mFingerprintManager.sendTemplateInBulk(fingerPrintTemplates, new MFingerprintManager.OnProgressListener() {
                @Override
                public void onStart() {
                    Snackbar.make(progressBar, "Starting upload...", Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void progress(int frac, int len, String errorMsg) {
                    if (errorMsg == null) {
                        Snackbar.make(progressBar, "upload "+frac+" of "+len, Snackbar.LENGTH_INDEFINITE).show();
                    }
                }

                @Override
                public void onEnd() {
                    Snackbar.make(progressBar, "upload complete successfully", Snackbar.LENGTH_LONG).show();
                    uploadLayoutView.setVisibility(View.GONE);
                    verifyLayoutView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
        }

    }

    void verify() {

    }

    void sendToast(String tst) {
        Toast.makeText(this, tst, Toast.LENGTH_SHORT).show();
    }
}
