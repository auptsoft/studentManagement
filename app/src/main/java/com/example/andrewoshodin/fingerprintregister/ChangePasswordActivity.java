package com.example.andrewoshodin.fingerprintregister;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;

/**
 * Created by Andrew Oshodin on 10/14/2018.
 */

public class ChangePasswordActivity  extends AppCompatActivity implements TextView.OnEditorActionListener {
    EditText oldPassword, newPassword, confirmNewPassword;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        oldPassword = (EditText)findViewById(R.id.old_password_id);
        newPassword = (EditText)findViewById(R.id.new_password_id);
        confirmNewPassword = (EditText)findViewById(R.id.comfirm_password_id);

        oldPassword.setOnEditorActionListener(this);
        newPassword.setOnEditorActionListener(this);
        confirmNewPassword.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String oldPasswordString = oldPassword.getText().toString();
        String newPasswordString = newPassword.getText().toString();
        String confirmNewPasswordString = confirmNewPassword.getText().toString();

        String savedOldPassword = AppState.sharedPreferences.getString(AppState.APP_PASSWORD_KEY, AppState.APP_PASSWORD_DEFAULT);
        if(oldPasswordString.equals(savedOldPassword)) {
            if(newPasswordString.equals(confirmNewPasswordString)) {
                AppState.sharedPreferences.edit().putString(AppState.APP_PASSWORD_KEY, newPasswordString).apply();
                Toast.makeText(this, "New Password saved successfully", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "New password does not match", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Incorrect old password", Toast.LENGTH_LONG).show();
        }
        return true;
    }
}
