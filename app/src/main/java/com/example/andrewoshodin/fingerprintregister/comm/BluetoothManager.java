package com.example.andrewoshodin.fingerprintregister.comm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;

/**
 * Created by Andrew Oshodin on 23/05/2017.
 */

public class BluetoothManager
{
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    ArrayList<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    ArrayList<BluetoothDevice> bondedDeviceList = new ArrayList<>();
    ArrayList<String> bluetoothNameList = new ArrayList<>();
    ArrayList<String> bondedDeviceNameList = new ArrayList<>();
    ArrayAdapter<String> listAdapter;
    BluetoothSocket socket;
    InputStream inputStream;
    OutputStream outputStream;
    Activity appCompatActivity;
    public volatile boolean connectionState = false;
    public volatile boolean writeState = false;
    public volatile String outString = "";

    public static final int BLUETOOTH_REQUEST_CODE = 2;

    UUID uuid = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");

    public BluetoothManager(Activity activity, boolean enable) {
        appCompatActivity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (enable) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                appCompatActivity.startActivity(intent);
            }
        }
    }

    public void activate(Activity activity, boolean enable) {
        appCompatActivity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (enable) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                appCompatActivity.startActivity(intent);
            }
        }
        appCompatActivity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        //appCompatActivity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        listAdapter = new ArrayAdapter<>(appCompatActivity, android.R.layout.simple_list_item_1, bondedDeviceNameList);

        Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : bondedDevice)
        {
            bondedDeviceList.add(device);
            bondedDeviceNameList.add(device.getName() + "\n" +device.getAddress());
            listAdapter.notifyDataSetChanged();
        }
    }

    public void enable(boolean enable)
    {
        if(enable)
        {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                appCompatActivity.startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
            }
            //appCompatActivity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            //appCompatActivity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
        else
        {
            bluetoothAdapter.disable();
        }
    }

    public  void startDiscovery()
    {
        bluetoothAdapter.startDiscovery();
    }
    public  void  cancelDiscovery()
    {
        bluetoothAdapter.cancelDiscovery();
    }

    public void setDiscoverable()
    {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        appCompatActivity.startActivity(intent);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice blueTemp = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDeviceList.add(blueTemp);
                bluetoothNameList.add(blueTemp.getName() + "\n" + blueTemp.getAddress());
                //listAdapter.notifyDataSetChanged();
            }
            else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
            {
                Toast.makeText(appCompatActivity, "Discovery Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void createServerSocket() {
        try {
            final BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("bluetoothServer", uuid);
            Toast.makeText(appCompatActivity, "READY to connect to other device", Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = serverSocket.accept();
                            Log.d("SERVER SOCKET", "Server socket accepted");
                            listenForMessage();
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                   }
                }).start();
        } catch (IOException ioe)
        {
            Toast.makeText(appCompatActivity, "NOT READY to connect to other devices. \n try RESTARTING app", Toast.LENGTH_LONG).show();
            ioe.printStackTrace();
        }
    }

    public BluetoothSocket createClientSocket(final BluetoothDevice device)
    {
        bluetoothDevice = device;
        //new Thread(new Runnable()
        {
            //@Override
            //public void run() {
                try
                {
                    socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    connectionState = true;
                    Log.d("CONNECTED", "Device connected");
                    listenForMessage();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                    ioe.printStackTrace();
                    Log.d("FALLBACK", "Trying fallback");
                    try {
                        socket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);
                        socket.connect();
                        connectionState =true;
                        Log.d("CONNECTED", "Device Connected");
                        listenForMessage();
                    } catch (Exception e) {
                        connectionState = false;
                        e.printStackTrace();
                        Log.e("BLUETOOTH CONNECT", "error connecting");
                    }
                }
            }
        //}).start();
        return socket;
    }

    public void connectToDeviceAsync(final String addr, final BluetoothConnectionInterface connectionInterface){
        final android.os.Handler handler = new android.os.Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice bluetoothDevice = getBluetoothAdapter().getRemoteDevice(addr);
                final BluetoothSocket bluetoothSocket = createClientSocket(bluetoothDevice);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(connectionState){
                            connectionInterface.onConnected(bluetoothSocket);
                        } else {
                            connectionInterface.onError("could not connect");
                        }
                    }
                });
            }
        }).start();
    }

    public void listenForMessage() {
        try
        {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            /*new Thread(new Runnable() {
                @Override
                public void run()
                {
                    byte[] buffer = new byte[1024];
                    try {
                        while(bluetoothAdapter.isEnabled())
                        {
                            int bytes = inputStream.read(buffer);
                            outString += new String(buffer, 0, 1024);
                        }
                    } catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }
            }).start(); */
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void write(char ch)
    {
        if (!(outputStream == null)) {
            try {
                outputStream.write(ch);
                outputStream.flush();
                writeState = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                writeState = false;
            }
        }
    }

    public void write(int in)
    {
        if (!(outputStream == null)) {
            try {
                outputStream.write(in);
                outputStream.flush();
                writeState = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                writeState = false;
            }
        }
    }

    public void write(String str)
    {
        for(int cn=0; cn<str.length(); cn++)
        {
            write(str.charAt(cn));
        }
    }

    public void writeBytes(final String str)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    write('1');
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {

                }
                for (int cn = 0; cn < str.length(); cn++) {
                    if (!(outputStream == null)) {
                        try {
                            outputStream.write(change(str.charAt(cn)));
                            outputStream.flush();
                            writeState = true;
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            writeState = false;
                        }
                    }
                }
            }
        }).start();
    }

    public ArrayList<String> getBluetoothNameList() {
        return bluetoothNameList;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public ArrayList<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public ArrayAdapter<String> getListAdapter() {
        return listAdapter;
    }

    public ArrayList<String> getBondedDeviceNameList() {
        return bondedDeviceNameList;
    }

    public ArrayList<BluetoothDevice> getBondedDeviceList()
    {
        Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
        bondedDeviceList.clear();
        for(BluetoothDevice device : bondedDevice)
        {
            bondedDeviceList.add(device);
            bondedDeviceNameList.add(device.getName() + "\n" +device.getAddress());
            listAdapter.notifyDataSetChanged();
        }
        return bondedDeviceList;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getOutString() {
        return outString;
    }


    public void closeSocket()
    {
        try {
            socket.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    protected byte change(char inputChar)
    {
        switch (inputChar)
        {
            case 'A':
                return 1;
            case 'B':
                return 2;
            case 'C':
                return 3;
            case 'D':
                return 4;
            case 'E':
                return 5;
            case 'F':
                return 6;
            case 'G':
                return 7;
            case 'H':
                return 8;
            case 'I':
                return 9;
            case 'J':
                return 10;
            case 'K':
                return 11;
            case 'L':
                return 12;
            case 'M':
                return 13;
            case 'N':
                return 14;
            case 'O':
                return 15;
            case 'P':
                return 16;
            case 'Q':
                return 17;
            case 'R':
                return 18;
            case 'S':
                return 19;
            case 'T':
                return 20;
            case 'U':
                return 21;
            case 'V':
                return 22;
            case 'W':
                return 23;
            case 'X':
                return 24;
            case 'Y':
                return 25;
            case 'Z':
                return 26;
            case '1':
                return 27;
            case '2':
                return 28;
            case '3':
                return 29;
            case '4':
                return 30;
            case '5':
                return 31;
            case '6':
                return 32;
            case '7':
                return 33;
            case '8':
                return 34;
            case '9':
                return 35;
            case '0':
                return 36;
            case ' ':
                return 37;
            default:
                return 100;
        }
    }
}