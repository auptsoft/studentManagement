package com.example.andrewoshodin.fingerprintregister;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Student;

/**
 * Created by Andrew Oshodin on 8/22/2018.
 */

public class BriefStudentDetailFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private static final String KEY = "KEY";

    private int index;

    ImageView passportView;
    TextView nameView, matNoView;

    LinearLayout wholeLayout;
    Student aStudent;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //index = savedInstanceState.getInt(KEY);
        //aStudent = AppState.allActiveStudent.get(index);

        aStudent = AppState.activeStudent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.brief_student_detail_fragment, container, false);

        passportView = (ImageView)view.findViewById(R.id.passport_view_id);
        nameView = (TextView)view.findViewById(R.id.name_view_id);
        matNoView = (TextView)view.findViewById(R.id.mat_number_view_id);

        updateView();

        wholeLayout = (LinearLayout)view.findViewById(R.id.brief_layout_id);
        wholeLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        AppState.activeStudent = aStudent;
        getContext().startActivity(new Intent(getContext(), StudentDetailActivity.class));
    }

    void updateView() {
        passportView.setImageURI(Uri.parse(aStudent.getPassportUrl()));
        nameView.setText(aStudent.getFirstName()+" "+aStudent.getLastName());
        matNoView.setText(aStudent.getMatNumber());
    }

    public static BriefStudentDetailFragment newInstance(int num) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY, num);
        BriefStudentDetailFragment newInstance = new BriefStudentDetailFragment();
        newInstance.setArguments(bundle);

        return newInstance;
    }
}