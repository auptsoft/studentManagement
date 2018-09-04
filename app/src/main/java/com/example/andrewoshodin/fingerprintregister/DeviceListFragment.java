package com.example.andrewoshodin.fingerprintregister;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.BluetoothManager;
import com.example.andrewoshodin.fingerprintregister.models.AppState;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceListFragment extends BottomSheetDialogFragment {
    ListView deviceListView;
    BluetoothManager bluetoothManager;
    ArrayList<BluetoothDevice> availableDevices = new ArrayList<>();
    DeviceArrayAdapter availableDeviceArrayAdapter;


    public DeviceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothManager = new BluetoothManager(getActivity(), false);
        bluetoothManager.activate(getActivity(), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(!bluetoothManager.getBluetoothAdapter().isEnabled()) {
            Toast.makeText(getContext(), "App requires bluetooth. \n Please turn bluetooth ON and try again",Toast.LENGTH_LONG).show();
            //getActivity().finish();
        }
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);
        deviceListView = (ListView)view.findViewById(R.id.device_list_id);
        availableDeviceArrayAdapter = new DeviceArrayAdapter(getContext(), R.layout.device_list_item, availableDevices);
            final ArrayList<BluetoothDevice> bondedDevices = bluetoothManager.getBondedDeviceList();
            final DeviceArrayAdapter deviceArrayAdapter = new DeviceArrayAdapter(getContext(), R.layout.device_list_item, bondedDevices);
            deviceListView.setAdapter(deviceArrayAdapter);
            deviceListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            deviceListView.setItemsCanFocus(true);
            deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getContext(), bondedDevices.get(i).getName()+" is selected \nSlide down and Click Bluetooth icon in Actionbar to Connect", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = AppState.sharedPreferences.edit();
                    editor.putString(AppState.BLUETOOTH_ADDRESS_PROPERTY, bondedDevices.get(i).getAddress());
                    editor.commit();

                    Toast.makeText(getContext(), bondedDevices.get(i).getName()+" is selected", Toast.LENGTH_LONG).show();
                    dismiss();
                }
            });

        return view;
    }

    private class DeviceArrayAdapter extends ArrayAdapter {
        View view;
        int layoutRes;
        ArrayList<BluetoothDevice> modelArrayList;
        public DeviceArrayAdapter(Context context, int resource, ArrayList<BluetoothDevice> mList) {
            super(context, resource, mList);
            layoutRes = resource;
            modelArrayList = mList;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutRes, null);

            TextView parishNameView = (TextView)view.findViewById(R.id.device_name_id);
            TextView parishAddressView = (TextView)view.findViewById(R.id.device_address_id);

            parishNameView.setText(modelArrayList.get(position).getName());
            parishAddressView.setText(modelArrayList.get(position).getAddress());

            return view;
        }
    }
}
