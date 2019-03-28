package com.example.andrewoshodin.fingerprintregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.MFingerprintManager;
import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Student;
import com.example.andrewoshodin.fingerprintregister.models.TemplateIdManager;

/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class VerifyFragment extends Fragment implements View.OnClickListener {
    Button start;
    ProgressBar verifyProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verify_fragment, container, false);

        start = (Button) view.findViewById(R.id.start_btn_id);
        start.setOnClickListener(this);

        verifyProgressBar = (ProgressBar)view.findViewById(R.id.verify_progress_id);
        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.equals(start)) {

            if (AppState.mFingerprintManager != null) {
                //startActivity(new Intent(getContext(), VerifyActivity.class));
                verifyProgressBar.setVisibility(View.VISIBLE);
                start.setEnabled(false);
                Snackbar.make(verifyProgressBar, "Verification in progress", Snackbar.LENGTH_INDEFINITE).show();

                AppState.mFingerprintManager.sendData("V",
                        10000, new MFingerprintManager.OnAcknowledgementListener() {
                            @Override
                            public void onAcknowledgement(MFingerprintManager.Ack ack, String ackString) {
                                switch (ack) {
                                    case NOT_SENT:
                                        sendToast("could not send");
                                        start.setEnabled(true);
                                        verifyProgressBar.setVisibility(View.GONE);
                                        Snackbar.make(verifyProgressBar, "Error in connection", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case SENT:
                                        //sendToast("sent"); //debug
                                        start.setEnabled(false);
                                        verifyProgressBar.setVisibility(View.GONE);
                                        break;
                                    case TIMEOUT:
                                        sendToast("timeout. Check circuit connection of the module.");
                                        start.setEnabled(true);
                                        verifyProgressBar.setVisibility(View.GONE);
                                        Snackbar.make(verifyProgressBar, "Module not responding", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case READ_ERROR:
                                        sendToast("could not read");
                                        start.setEnabled(true);
                                        verifyProgressBar.setVisibility(View.GONE);
                                        Snackbar.make(verifyProgressBar, "Error in received packet", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case ACKNOWLEDGED:
                                        //sendToast(ackString==null?"null received":ackString);
                                        if (ackString != null) {
                                            if (ackString.charAt(0) == 'E') {
                                                char errorCode = ackString.charAt(1);
                                                if (errorCode == 'S') {
                                                    sendToast("Student with fingerprint not found or error occurred. TRY AGAIN." +
                                                            "If error persists, the student might not be registered.");
                                                    Snackbar.make(verifyProgressBar, "Student not found!", Snackbar.LENGTH_LONG).show();
                                                } else {
                                                    sendToast("Error occurred while taking fingerprint. " +
                                                            "\n Make sure the finger is placed on the module before tapping on START");
                                                    Snackbar.make(verifyProgressBar, "Invalid fingerprint", Snackbar.LENGTH_LONG).show();
                                                }
                                                start.setEnabled(true);
                                            } else {
                                                String matNo = TemplateIdManager.getMatNo(getContext(), ackString);
                                                /*for (TemplateIdManager.TemplateId templateId : TemplateIdManager.getAllTemplate(getContext())) {
                                                    sendToast(templateId.getTemplateId());
                                                }*/
                                                sendToast(ackString);
                                                if (matNo != null) {
                                                    Student student = new Student(matNo, AppState.activeCourse.getCourseCode()).get(getContext());
                                                    sendToast(matNo);
                                                    if (student != null) {
                                                        Snackbar.make(verifyProgressBar, "Verified successfully", Snackbar.LENGTH_LONG).show();
                                                        AppState.activeStudent = student;
                                                        new BriefStudentDetailFragment().show(getActivity().getSupportFragmentManager(), "briefStudentDetails");
                                                    } else {
                                                        Snackbar.make(verifyProgressBar, "Student not registered in this course list", Snackbar.LENGTH_LONG).show();
                                                        sendToast("Student not found in the course list. ");
                                                    }
                                                } else {
                                                    Snackbar.make(verifyProgressBar, "Student not found", Snackbar.LENGTH_LONG).show();
                                                    sendToast("Student not registered for this course.");
                                                }
                                                start.setEnabled(true);
                                                verifyProgressBar.setVisibility(View.GONE);

                                            }
                                        }
                                        break;
                                }


                            }
                        });
            }else {
                sendToast("Not connected");
            }

        }
    }

    void sendToast(String tst) {
        Toast.makeText(getContext(), tst, Toast.LENGTH_SHORT).show();
    }
}
