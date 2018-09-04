package com.example.andrewoshodin.fingerprintregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.comm.MFingerprintManager;
import com.example.andrewoshodin.fingerprintregister.models.AppState;

/**
 * Created by Andrew Oshodin on 8/22/2018.
 */

public class TakeFingerprintFragment extends Fragment implements View.OnClickListener{
    public static final String FINGERPRINT_KEY = "FINGERPRINT_KEY";

    ImageView fingerPrintImageView;
    TextView infoView;
    Button start;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.take_fingerprint_fragment, container, false);

        fingerPrintImageView = (ImageView)view.findViewById(R.id.fingerprint_img_view_id);
        infoView = (TextView)view.findViewById(R.id.info_view_id);
        start = (Button)view.findViewById(R.id.start_take_fingerprint_id);
        start.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(start)) {
            if (AppState.ioCommunication != null) {
                AppState.mFingerprintManager = new MFingerprintManager(AppState.ioCommunication);
                /*AppState.mFingerprintManager.getTemplate(new MFingerprintManager.OnReceiveTemplateListener() {
                    @Override
                    public void onReceiveAck1(MFingerprintManager.Ack ack, String ackString) {
                        Toast.makeText(getContext(), ackString, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReceiveAck2(MFingerprintManager.Ack ack, String ackString) {
                        Toast.makeText(getContext(), ackString, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReceiveAck3(MFingerprintManager.Ack ack, String ackString) {
                        Toast.makeText(getContext(), ackString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReceiveTemplate(MFingerprintManager.Ack ack, String fingerPrintTemplate) {
                        Toast.makeText(getContext(), fingerPrintTemplate==null? "null template":fingerPrintTemplate, Toast.LENGTH_LONG).show();
                    }
                }); */


                start.setEnabled(false);
                AppState.mFingerprintManager.sendData(MFingerprintManager.GET_FINGERPRINT,10000, new MFingerprintManager.OnAcknowledgementListener() {
                    @Override
                    public void onAcknowledgement(MFingerprintManager.Ack ack, String ackString) {
                        switch (ack) {
                            case NOT_SENT:
                                sendToast("could not send");
                                start.setEnabled(true);
                                break;
                            case SENT:
                                sendToast("sent");
                                start.setEnabled(false);
                                break;
                            case TIMEOUT:
                                sendToast("timeout");
                                start.setEnabled(true);
                                break;
                            case READ_ERROR:
                                sendToast("could not read");
                                start.setEnabled(true);
                                break;
                            case ACKNOWLEDGED:
                                //sendToast(ackString==null?"null received":ackString);
                                if (ackString != null) {
                                    if (ackString.equals("E")) {
                                        sendToast("Error occurred while taking fingerprint. " +
                                                "\n Place your finger and tap Start to try again");
                                        start.setEnabled(true);
                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra(FINGERPRINT_KEY, ackString);
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        sendToast("Fingerprint taken successfully");
                                        getActivity().finish();
                                    }
                                }
                                break;
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(),"not connected. Tap on the Bluetooth symbol to Connect", Toast.LENGTH_LONG).show();
            }
        }
    }

    void sendToast(String tst) {
        Toast.makeText(getContext(), tst, Toast.LENGTH_SHORT).show();
    }
}
