package com.example.andrewoshodin.fingerprintregister;

import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.BluetoothConnectionInterface;
import com.example.andrewoshodin.fingerprintregister.comm.IOCommunication;
import com.example.andrewoshodin.fingerprintregister.comm.MFingerprintManager;
import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;
import com.example.andrewoshodin.fingerprintregister.models.Student;
import com.example.andrewoshodin.fingerprintregister.models.TemplateIdManager;


/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class CourseDetailActivity extends AppCompatActivity {
    ViewPager courseDetailViewPager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_detail_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar_id);
        toolbar.setTitle(AppState.activeCourse.getCourseCode());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseDetailViewPager = (ViewPager)findViewById(R.id.course_detail_view_pager_id);
        courseDetailViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        courseDetailViewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem addCourseMenu = menu.add(1, 1, 1, "Add new Student");
        addCourseMenu.setIcon(R.drawable.ic_add_black_24dp);
        addCourseMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem deleteMenu = menu.add(2, 2, 2, "Delete Course");

        MenuItem showDevicesMenu = menu.add(3, 3, 3, "Show Devices");
        showDevicesMenu.setIcon(R.drawable.ic_bluetooth_connected_black_24dp);

        MenuItem connectToDeviceMenu = menu.add(4, 4, 4, "Connect");
        connectToDeviceMenu.setIcon(R.drawable.ic_bluetooth_searching_black_24dp);
        connectToDeviceMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:
                startActivityForResult(new Intent(this, AuthenticateActivity.class), 5);
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Confirmation");
                builder.setMessage("Do you want to delete "+AppState.activeCourse.getCourseCode()+
                " permanently and all the registered student permanently?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppState.activeCourse.delete(getBaseContext());
                        for (Student student : AppState.allActiveStudent) {
                            student.delete(getBaseContext());
                            TemplateIdManager.deleteWithMatNo(getBaseContext(), student.getMatNumber());
                        }
                        sendBroadcast(new Intent(Course.COURSE_EDITED_INTENT));
                        Toast.makeText(getBaseContext(), AppState.activeCourse+" deleted successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                break;
            case 3:
                new DeviceListFragment().show(getSupportFragmentManager(), "ShowDevices");
                break;

            case 4:
                String address = AppState.sharedPreferences.getString(AppState.BLUETOOTH_ADDRESS_PROPERTY,
                        "FC:3D:03:F9:B8:80");
                Snackbar.make(courseDetailViewPager, "Connecting...", Snackbar.LENGTH_INDEFINITE).show();
                AppState.bluetoothManager.connectToDeviceAsync(address, new BluetoothConnectionInterface() {
                    @Override
                    public void onConnected(BluetoothSocket bluetoothSocket) {
                        Toast.makeText(getBaseContext(), "Connected to Fingerprint Module", Toast.LENGTH_LONG).show();

                        AppState.bluetoothManager.connectionState = true;
                        AppState.ioCommunication = new IOCommunication(AppState.bluetoothManager.getInputStream(),
                                AppState.bluetoothManager.getOutputStream());
                        AppState.mFingerprintManager = new MFingerprintManager(AppState.ioCommunication);
                        Snackbar.make(courseDetailViewPager, "Connected", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorMessage) {

                        Toast.makeText(getBaseContext(), "Could not connect to Module. Try again", Toast.LENGTH_LONG).show();
                        Snackbar.make(courseDetailViewPager, "error occurred while connecting", Snackbar.LENGTH_LONG).show();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK) {
                AppState.studentEditState = false;
                Intent intent = new Intent(this, Register.class);
                startActivity(intent);
        }
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        String titles[] = {"Details", "Registered Students", "Verify Students"};
        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new CourseDetail();
            } else if (position == 1){
                return new StudentList();
            } else {
                return new VerifyFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
