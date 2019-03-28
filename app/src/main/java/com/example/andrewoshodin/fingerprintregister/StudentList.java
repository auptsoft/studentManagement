package com.example.andrewoshodin.fingerprintregister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;
import com.example.andrewoshodin.fingerprintregister.models.Student;

import java.util.ArrayList;

/**
 * Created by Andrew Oshodin on 8/21/2018.
 */

public class StudentList extends Fragment  implements AdapterView.OnItemClickListener{
    LinearLayout noStudent;
    ListView studentList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_list_fragment, container, false);

        noStudent = (LinearLayout)view.findViewById(R.id.no_student_view_id);
        studentList = (ListView)view.findViewById(R.id.students_list_id);
        studentList.setOnItemClickListener(this);

        updateView();
        getContext().registerReceiver(addedBroadcast, new IntentFilter(Student.STUDENT_ADDED_INTENT));
        getContext().registerReceiver(addedBroadcast, new IntentFilter(Student.STUDENT_EDITED_INTENT));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //getContext().unregisterReceiver(addedBroadcast);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppState.activeStudent = (Student)adapterView.getItemAtPosition(i);
        Intent intent = new Intent(getContext(), StudentDetailActivity.class);
        //Toast.makeText(getContext(), AppState.activeStudent.getCourseCode(), Toast.LENGTH_LONG).show(); //debug
        startActivity(intent);
    }

    BroadcastReceiver addedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getContext(), "student list updated", Toast.LENGTH_LONG).show();
            updateView();
        }
    };

    public void updateView() {
        ArrayList<Student> students = new Student().getAll(getContext(), AppState.activeCourse.getCourseCode());
        AppState.allActiveStudent = students;
        studentList.setAdapter(new StudentsListAdapter(getContext(), R.layout.student_item_view, students));
        if (students.size()>0){
            noStudent.setVisibility(View.GONE);
        } else {
            noStudent.setVisibility(View.VISIBLE);
        }
    }

    private class StudentsListAdapter extends ArrayAdapter {
        ArrayList<Student> students;
        int layoutRes;
        StudentsListAdapter(Context context, int layoutRes, ArrayList<Student> students) {
            super(context, layoutRes, students);
            this.students = students;
            this.layoutRes = layoutRes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout itemView = (LinearLayout)layoutInflater.inflate(layoutRes, null);

            TextView itemTitle = (TextView)itemView.findViewById(R.id.title_view);
            TextView itemSubtitle = (TextView)itemView.findViewById(R.id.subtitle_view);
            TextView itemTextIcon = (TextView) itemView.findViewById(R.id.item_text_icon_id);

            itemTitle.setText(students.get(position).getLastName()+" "+students.get(position).getFirstName());
            itemSubtitle.setText(students.get(position).getMatNumber());


            itemTextIcon.setText(students.get(position).getLastName().charAt(0)+"");

            //itemImage.setImageURI(Uri.parse(students.get(position).getPassportUrl()));

            return itemView;
        }
    }
}
