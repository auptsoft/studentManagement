package com.example.andrewoshodin.fingerprintregister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;

public class CourseDetail extends Fragment implements View.OnClickListener{
    LayoutInflater layoutInflater;
    LinearLayout detailLayout;
    Button editButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_detail_fragment, container, false);
        layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        detailLayout = (LinearLayout)view.findViewById(R.id.detail_layout_id);
        editButton = (Button)view.findViewById(R.id.course_edit_btn_id);
        editButton.setOnClickListener(this);

        initView();

        getContext().registerReceiver(editedReceiver, new IntentFilter(Course.COURSE_EDITED_INTENT));

        return view;
    }

    @Override
    public void onClick(View view) {
        AppState.courseEditState = true;
        new AddCourse().show(getActivity().getSupportFragmentManager(), "Edit Course");
    }

    private void initView() {
        detailLayout.addView(getDetailItemView("Course Code", AppState.activeCourse.getCourseCode()));
        detailLayout.addView(getDetailItemView("Course Title", AppState.activeCourse.getCourseTitle()));
        detailLayout.addView(getDetailItemView("Lecturer(s)", AppState.activeCourse.getDescription()));
    }

    private View getDetailItemView(String property, String value) {
        LinearLayout detailItem = (LinearLayout)layoutInflater.inflate(R.layout.detail_item, null, false);
        TextView propertyView = (TextView)detailItem.findViewById(R.id.item_property_id);
        TextView valueView = (TextView)detailItem.findViewById(R.id.item_value_id);

        propertyView.setText(property);
        valueView.setText(value);

        return detailItem;
    }

    BroadcastReceiver editedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            detailLayout.removeAllViews();
            Toast.makeText(getContext(), "edited", Toast.LENGTH_LONG).show();
            initView();
        }
    };
}