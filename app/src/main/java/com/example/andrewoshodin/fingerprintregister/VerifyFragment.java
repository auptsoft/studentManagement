package com.example.andrewoshodin.fingerprintregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class VerifyFragment extends Fragment implements View.OnClickListener{
    Button start;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verify_fragment, container, false);

        start = (Button)view.findViewById(R.id.start_btn_id);
        start.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(view)){
            startActivity(new Intent(getContext(), VerifyActivity.class));
        }
    }
}
