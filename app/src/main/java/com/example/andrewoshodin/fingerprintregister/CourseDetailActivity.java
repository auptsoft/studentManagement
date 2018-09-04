package com.example.andrewoshodin.fingerprintregister;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;


/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class CourseDetailActivity extends AppCompatActivity {
    ViewPager courseDetailViewPager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_detail_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar_id);
        toolbar.setTitle(AppState.activeCourse.getCourseCode());
        setSupportActionBar(toolbar);

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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AppState.studentEditState = false;
                Intent intent = new Intent(this, Register.class);
                startActivity(intent);
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Confirmation");
                builder.setMessage("Do you want to delete "+AppState.activeCourse.getCourseCode()+
                " permanently?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppState.activeCourse.delete(getBaseContext());
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

        }
        return super.onOptionsItemSelected(item);
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
