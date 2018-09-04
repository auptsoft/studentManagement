package com.example.andrewoshodin.fingerprintregister;

import android.content.Context;
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
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.main_toolbar_id);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AppState.courseEditState = false;
                new AddCourse().show(getSupportFragmentManager(), "Add course");
                break;
        }
        return super.onOptionsItemSelected(item);
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
