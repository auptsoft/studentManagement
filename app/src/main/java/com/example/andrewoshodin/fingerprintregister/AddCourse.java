package com.example.andrewoshodin.fingerprintregister;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;

/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class AddCourse extends BottomSheetDialogFragment implements View.OnClickListener {
    TextView addCourseTitleView;
    EditText courseCodeEdit, courseTitleEdit, descriptionEdit;
    Button addButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_course_fragment, container, false);

        addCourseTitleView = (TextView)view.findViewById(R.id.add_course_title_id);
        courseCodeEdit = (EditText)view.findViewById(R.id.course_code_id);
        courseTitleEdit = (EditText)view.findViewById(R.id.course_title_id);
        descriptionEdit = (EditText)view.findViewById(R.id.description_id);

        addButton = (Button)view.findViewById(R.id.add_btn);
        addButton.setOnClickListener(this);

        fillViews();

        return view;
    }


    @Override
    public void onClick(View view) {
        String courseCode = courseCodeEdit.getText().toString();
        String courseTitle = courseTitleEdit.getText().toString();
        String description = descriptionEdit.getText().toString();

        if(!validateLength(courseCode, "Course Code", 6)) return;
        if(!validateLength(courseTitle, "Course Title", 3)) return;
        if(!validateLength(description, "Lecturer(s) name", 3))return;

        if (AppState.courseEditState) {
            long affectedRows = new Course(0, courseCode, courseTitle, description).update(getContext());
            //Toast.makeText(getContext(), affectedRows+"", Toast.LENGTH_LONG).show(); //debug
            if (affectedRows < 1) {
                Toast.makeText(getContext(), "Operation not Successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Course updated successfully", Toast.LENGTH_LONG).show();
                getContext().sendBroadcast(new Intent(Course.COURSE_EDITED_INTENT));
                AppState.activeCourse = new Course(AppState.activeCourse.getCourseCode()).get(getContext());
                dismiss();
            }
        } else {
            Course course = new Course(0, courseCode, courseTitle, description);
            String resp = course.insert(getContext());
            if (resp == null) {
                Toast.makeText(getContext(), "Course registered successfully", Toast.LENGTH_LONG).show();
                getContext().sendBroadcast(new Intent(Course.COURSE_ADDED_INTENT));
                dismiss();
            } else if (resp.equals(Course.ALREADY_REGISTERED)) {
                Toast.makeText(getContext(), "Course Already registered", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), resp, Toast.LENGTH_LONG).show();
            }
        }
    }

    void fillViews() {
        if (AppState.courseEditState) {
            addCourseTitleView.setText("Edit Course");
            courseCodeEdit.setText(AppState.activeCourse.getCourseCode());
            courseCodeEdit.setEnabled(false);
            courseTitleEdit.setText(AppState.activeCourse.getCourseTitle());
            descriptionEdit.setText(AppState.activeCourse.getDescription());
            addButton.setText("Save");
            //addButton.setCompoundDrawablesRelative(null,
              //      null, null, null);
        }
    }

    private boolean validateLength(String vStr, String propStr, int minLen) {
        if (vStr.length() < minLen) {
            Toast.makeText(getContext(), propStr + " is too short", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateCourseCode(String courseCode) {
        if(validateLength(courseCode, "Course Code", 6)) {
            String dept = courseCode.substring(0, 2);
            String code = courseCode.substring(3);
        }
        return  false;
    }
}