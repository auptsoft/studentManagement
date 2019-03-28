package com.example.andrewoshodin.fingerprintregister.models;

import android.content.SharedPreferences;

import com.example.andrewoshodin.fingerprintregister.comm.BluetoothManager;
import com.example.andrewoshodin.fingerprintregister.comm.IOCommunication;
import com.example.andrewoshodin.fingerprintregister.comm.MFingerprintManager;

import java.util.ArrayList;

/**
 * Created by Andrew Oshodin on 8/21/2018.
 */

public class AppState {
    public static Course activeCourse;
    public static Student activeStudent;

    public static boolean courseEditState = false;
    public static boolean studentEditState = false;

    public  static final String PREFERENCE_NAME = "PREFERENCE_NAME";
    public static final String BLUETOOTH_ADDRESS_PROPERTY = "BLUETOOTH_ADDRESS";


    public static BluetoothManager bluetoothManager;
    public static IOCommunication ioCommunication;

    public static MFingerprintManager mFingerprintManager;

    public static SharedPreferences sharedPreferences = null;

    public static ArrayList<Student> allActiveStudent = null;

    public static final String APP_PASSWORD_KEY = "APP_PASSWORD_KEY";
    public static final String APP_PASSWORD_DEFAULT = "abcd";

    public static final int AUTHENTICATE_REQUEST_CODE = 10;
    public static boolean authenticated = false;

}
