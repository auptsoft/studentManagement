package com.example.andrewoshodin.fingerprintregister;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;

/**
 * Created by Andrew Oshodin on 10/14/2018.
 */

public class AuthenticateActivity extends AppCompatActivity {
    EditText authenticationPassword;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String currentPasswordString = AppState.sharedPreferences.getString(AppState.APP_PASSWORD_KEY, AppState.APP_PASSWORD_DEFAULT);
        if (AppState.authenticated || currentPasswordString.equals("")){
            setResult(RESULT_OK);
            finish();
        }

        authenticationPassword = (EditText)findViewById(R.id.authentication_password_id);

        authenticationPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    String passwordString = authenticationPassword.getText().toString();
                    String currentPasswordString = AppState.sharedPreferences.getString(AppState.APP_PASSWORD_KEY, AppState.APP_PASSWORD_DEFAULT);

                    if (passwordString.equals(currentPasswordString)) {
                        setResult(RESULT_OK);
                        AppState.authenticated = true;
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Wrong password", Toast.LENGTH_LONG).show();
                    }
                return true;
            }
        });
    }
}
