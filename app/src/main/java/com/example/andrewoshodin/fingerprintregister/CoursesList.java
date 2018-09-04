package com.example.andrewoshodin.fingerprintregister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;

import java.util.ArrayList;

/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class CoursesList extends Fragment implements AdapterView.OnItemClickListener{
    LinearLayout noCourseView;
    ListView coursesList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.courses_list_fragment, container, false);

        noCourseView = (LinearLayout)view.findViewById(R.id.no_course_view_id);
        coursesList = (ListView)view.findViewById(R.id.courses_list_id);
        coursesList.setOnItemClickListener(this);
        //getActivity().registerForContextMenu(coursesList);

        updateView();

        getContext().registerReceiver(addedBroadcast, new IntentFilter(Course.COURSE_ADDED_INTENT));
        getContext().registerReceiver(addedBroadcast, new IntentFilter(Course.COURSE_EDITED_INTENT));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
        AppState.activeCourse = (Course)adapterView.getItemAtPosition(i);
        Intent intent = new Intent(getContext(), CourseDetailActivity.class);

        startActivity(intent);
    }

    public void updateView() {
        ArrayList<Course> courses = new Course().getAll(getContext());
        coursesList.setAdapter(new CoursesListAdapter(getContext(), R.layout.item_view,
                courses));
        if (courses.size()>0) {
            noCourseView.setVisibility(View.GONE);
        } else {
            noCourseView.setVisibility(View.VISIBLE);
        }
    }

    BroadcastReceiver addedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getContext(), "list updated", Toast.LENGTH_SHORT).show(); //debug
            updateView();
        }
    };

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        Adapter adapter = coursesList.getAdapter();
        Object item = adapter.getItem(info.position);
        menu.setHeaderTitle("Choose");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(getContext(), "deleted option", Toast.LENGTH_SHORT ).show(); //debug
                break;
            case 2:

                break;
        }
        return super.onContextItemSelected(item);
    } */


    private class CoursesListAdapter extends ArrayAdapter {
        ArrayList<Course> courses;
        int layoutRes;
        CoursesListAdapter(Context context, int layoutRes, ArrayList<Course> courses) {
            super(context, layoutRes, courses);
            this.courses = courses;
            this.layoutRes = layoutRes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout itemView = (LinearLayout)layoutInflater.inflate(layoutRes, null);

            TextView itemTitle = (TextView)itemView.findViewById(R.id.title_view);
            TextView itemSubtitle = (TextView)itemView.findViewById(R.id.subtitle_view);

            itemTitle.setText(courses.get(position).getCourseCode());
            itemSubtitle.setText(courses.get(position).getCourseTitle());

            //itemView.setLongClickable(true);

            return itemView;
        }
    }

}
