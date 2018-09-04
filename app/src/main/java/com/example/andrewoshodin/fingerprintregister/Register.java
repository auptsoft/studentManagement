package com.example.andrewoshodin.fingerprintregister;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Student;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    static final int EXTERNAL_STORAGE_PERMISSION_CODE = 10;
    static final int PASSPORT_CAPTURE_CODE = 11;
    static final int TAKE_FINGERPRINT_CODE = 12;
    private AutoCompleteTextView mFirstNameView, mLastNameView, mSex,
            mMatNumberView, mLevel, mDeptView, mFacultyView, mPhoneNumberView,
            mEmailView;

    private ImageView mPassportView;

    private Button mTakePassportView, mFingerPrintView, mSaveView;

    private android.support.v7.widget.Toolbar toolbar;

    boolean passportTaken = false;

    String fingerprintTemplate = "";

    Uri fileUri;

    File storageFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_id);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");

        mFirstNameView = (AutoCompleteTextView) findViewById(R.id.first_name_edit_id);
        mLastNameView = (AutoCompleteTextView) findViewById(R.id.last_name_edit_id);
        mSex = (AutoCompleteTextView) findViewById(R.id.sex_edit_id);
        mMatNumberView = (AutoCompleteTextView) findViewById(R.id.mat_number_edit_id);
        mLevel = (AutoCompleteTextView) findViewById(R.id.level_edit_id);
        mDeptView = (AutoCompleteTextView) findViewById(R.id.dept_edit_id);
        mFacultyView = (AutoCompleteTextView) findViewById(R.id.faculty_edit_id);
        mPhoneNumberView = (AutoCompleteTextView) findViewById(R.id.phone_number_edit_id);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_edit_id);

        mPassportView = (ImageView) findViewById(R.id.passport_view_id);
        mTakePassportView = (Button) findViewById(R.id.take_passport_view_id);
        mFingerPrintView = (Button) findViewById(R.id.take_fingerprint_view_id);
        mSaveView = (Button) findViewById(R.id.save_button_id);

        mTakePassportView.setOnClickListener(this);
        mFingerPrintView.setOnClickListener(this);
        mSaveView.setOnClickListener(this);

        initialize();
        initAutoComplete();

        mMatNumberView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //Toast.makeText(getBaseContext(), "focus change", Toast.LENGTH_LONG).show();
                if (checkPasswordUrl(getPassportFile(mMatNumberView.getText().toString()).getPath())) {
                    //Toast.makeText(getBaseContext(), "match found", Toast.LENGTH_LONG).show();
                    mPassportView.setImageResource(R.drawable.ic_person_outline_black_24dp);
                    mPassportView.setImageURI(Uri.fromFile(getPassportFile(mMatNumberView.getText().toString())));
                    mTakePassportView.setText("RETAKE PASSPORT");
                }
            }
        });
        initializeViewToEdit();
    }


    @Override
    public void onClick(View view) {
        if (view.equals(mTakePassportView)) {
            String passportUrlString = mMatNumberView.getText().toString();
            if (validateLength(passportUrlString, "Matriculation Number", 7)) {
                File passportFile = getPassportFile(passportUrlString);
                Uri passportUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", passportFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, passportUri);

                startActivityForResult(intent, PASSPORT_CAPTURE_CODE);
            }
        } else if (view.equals(mFingerPrintView)) {
            Intent intent = new Intent(this, TakeFingerprintActivity.class);
            startActivityForResult(intent, TAKE_FINGERPRINT_CODE);
        } else if (view.equals(mSaveView)) {
            String firstName = mFirstNameView.getText().toString();
            String lastName = mLastNameView.getText().toString();
            String sex = mSex.getText().toString();
            String matNumber = mMatNumberView.getText().toString();
            String level = mLevel.getText().toString();
            String department = mDeptView.getText().toString();
            String faculty = mFacultyView.getText().toString();
            String phoneNumber = mPhoneNumberView.getText().toString();
            String email = mEmailView.getText().toString();
            String passportUrl = getPassportFile(mMatNumberView.getText().toString()).getPath();
            String fingerPrintTemplate = this.fingerprintTemplate;

            if (!validateLength(firstName, "First Name", 2)) return;
            if (!validateLength(lastName, "Last Name", 2)) return;

            String courseCode = AppState.activeCourse.getCourseCode();

            Student newStudent = new Student(0, firstName, lastName, sex, matNumber, level, department,
                    faculty, phoneNumber, email, passportUrl, fingerPrintTemplate, courseCode);

            if (!AppState.studentEditState) {
                String resp = newStudent.insert(this);
                //Toast.makeText(this, newStudent.getCourseCode(), Toast.LENGTH_LONG).show(); //debug
                if (resp == null) {
                    Toast.makeText(this, "Student registered successfully", Toast.LENGTH_LONG).show();
                    sendBroadcast(new Intent(Student.STUDENT_ADDED_INTENT));
                    finish();
                } else if (resp.equals(Student.ALREADY_REGISTERED)) {
                    Toast.makeText(this, "Student is already registered for the course", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, resp, Toast.LENGTH_LONG).show();
                }
            } else {
                long resp = newStudent.update(this);
                if (resp > 0) {
                    Toast.makeText(this, "Updated Successfully", Toast.LENGTH_LONG).show();
                    sendBroadcast(new Intent(Student.STUDENT_EDITED_INTENT));
                    AppState.activeStudent = new Student(matNumber, courseCode).get(this);
                    finish();
                } else {
                    Toast.makeText(this, "Could not update", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @TargetApi(23)
    private void initialize() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, EXTERNAL_STORAGE_PERMISSION_CODE);
        } else {
            setupStorageFolder();
        }

    }

    private void setupStorageFolder() {
        File exFile = Environment.getExternalStorageDirectory();
        File folder = new File(exFile, "Passports");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                Toast.makeText(this, folder.getAbsolutePath(), Toast.LENGTH_LONG).show(); //debug
            } else {
                Toast.makeText(this, "Error 1", Toast.LENGTH_LONG).show(); //debug
            }
        } else {
            //Toast.makeText(this, "exists", Toast.LENGTH_LONG).show(); //debug
        }
        storageFolder = folder;
    }

    private File getPassportFile(String id) {
        File file = new File(storageFolder.getPath() + File.separator + id + ".jpg");
        //Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupStorageFolder();
        } else {
            Toast.makeText(this, "Permission required for app to work", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, resultCode + "", Toast.LENGTH_LONG).show(); //debug
        if (requestCode == PASSPORT_CAPTURE_CODE && resultCode == RESULT_OK) {
            //Toast.makeText(this, "return", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
            mPassportView.setImageResource(R.drawable.ic_person_outline_black_24dp);
            mPassportView.setImageURI(Uri.fromFile(getPassportFile(mMatNumberView.getText().toString())));
            passportTaken = true;
        } else if (requestCode == TAKE_FINGERPRINT_CODE ) {
            if (resultCode == RESULT_OK) {
                fingerprintTemplate = data.getStringExtra(TakeFingerprintFragment.FINGERPRINT_KEY);
                //Toast.makeText(this, fingerprintTemplate, Toast.LENGTH_LONG).show(); //debug
            } else {
                Toast.makeText(this, "Fingerprint not taken", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validateLength(String vStr, String propStr, int minLen) {
        if (vStr.length() < minLen) {
            Toast.makeText(this, propStr + " is too short", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPasswordUrl(String passportUrl) {
        File file = new File(passportUrl);
        if (file.exists()) {
            return true;
        } else return false;
    }

    private void initializeViewToEdit() {
        if (AppState.studentEditState) {
            getSupportActionBar().setTitle("Edit Student Details");

            mFirstNameView.setText(AppState.activeStudent.getFirstName());
            mLastNameView.setText(AppState.activeStudent.getLastName());
            mSex.setText(AppState.activeStudent.getSex());
            mMatNumberView.setText(AppState.activeStudent.getMatNumber());
            mMatNumberView.setEnabled(false);
            mLevel.setText(AppState.activeStudent.getLevel());
            mDeptView.setText(AppState.activeStudent.getDepartment());
            mFacultyView.setText(AppState.activeStudent.getFaculty());
            mPhoneNumberView.setText(AppState.activeStudent.getPhoneNumber());
            mEmailView.setText(AppState.activeStudent.getEmail());

            mPassportView.setImageURI(Uri.parse(AppState.activeStudent.getPassportUrl()));
        }
    }

    void initAutoComplete() {
        mSex.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"Male", "Female"}));
        mSex.setThreshold(1);

        mLevel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"100", "200", "300", "400", "500", "600", "700", "800"}));
        mLevel.setThreshold(1);

        mDeptView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{
                        "Electrical and Electronic Engineering",
                        "Pharmacy"
                }));
        mDeptView.setThreshold(1);

        mFacultyView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{
                        "Agriculture",
                        "Arts",
                        "Education",
                        "Engineering",
                        "Environmental Sciences",
                        "Life Sciences",
                        "Management Sciences",
                        "Medicine",
                        "Pharmacy",
                        "Physical Sciences",
                        "Social Science"
                }));
        mFacultyView.setThreshold(1);

    }
}