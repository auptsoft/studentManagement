package com.example.andrewoshodin.fingerprintregister;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.BluetoothManager;
import com.example.andrewoshodin.fingerprintregister.comm.MFingerprintManager;
import com.example.andrewoshodin.fingerprintregister.models.AppState;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager mainViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppState.authenticated = false;
        startActivityForResult(new Intent(this, AuthenticateActivity.class), 5);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar)findViewById(R.id.main_toolbar_id);
        //toolbar.setTitle("");
        setSupportActionBar(toolbar);

        AppState.sharedPreferences = getSharedPreferences(AppState.PREFERENCE_NAME, Context.MODE_PRIVATE);
        AppState.bluetoothManager = new BluetoothManager(this, true);

        mainViewPager = (ViewPager)findViewById(R.id.main_view_pager_id);

        mainViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem addCourseMenu = menu.add(1, 1, 1, "Add new course");
        addCourseMenu.setIcon(R.drawable.ic_add_black_24dp);
        addCourseMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem changePasswordMenu = menu.add(2,2,2, "Change password");
        changePasswordMenu.setIcon(R.drawable.ic_vpn_key_black_24dp);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, AuthenticateActivity.class);
                startActivityForResult(intent, AppState.AUTHENTICATE_REQUEST_CODE);
                break;
            case 2:
                startActivity(new Intent(this, ChangePasswordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppState.AUTHENTICATE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                AppState.courseEditState = false;
                new AddCourse().show(getSupportFragmentManager(), "Add course");
            }
        } else if (requestCode == 5) {
            if(resultCode == RESULT_OK) {
                return;
            } else {
                finish();
            }
        }
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        String titles[] = {"Courses", "Verify"};
        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            //if (position == 0) {
                return new CoursesList();
            //} else {
                //return new Verify();
            //}
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
