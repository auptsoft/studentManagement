package com.example.andrewoshodin.fingerprintregister;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewoshodin.fingerprintregister.models.AppState;
import com.example.andrewoshodin.fingerprintregister.models.Course;
import com.example.andrewoshodin.fingerprintregister.models.Student;
import com.example.andrewoshodin.fingerprintregister.models.TemplateIdManager;

/**
 * Created by Andrew Oshodin on 8/21/2018.
 */

public class StudentDetailActivity extends AppCompatActivity {
    Toolbar toolbar;

    ImageView passportView;
    TextView nameView;
    TextView matNumberView;
    LinearLayout detailLayout;
    LayoutInflater layoutInflater;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_detail_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        toolbar = (Toolbar)findViewById(R.id.student_detail_toolbar_id);
        toolbar.setTitle(AppState.activeStudent.getFirstName()+ " "+AppState.activeStudent.getLastName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        passportView = (ImageView)findViewById(R.id.passport_view_id);
        nameView = (TextView)findViewById(R.id.name_view_id);
        matNumberView = (TextView)findViewById(R.id.mat_number_view_id);
        //Toast.makeText(this, "hi", Toast.LENGTH_LONG).show(); //debug
        detailLayout = (LinearLayout) findViewById(R.id.students_detail_layout_id);
        updateView();
        registerReceiver(editedReceiver, new IntentFilter(Student.STUDENT_EDITED_INTENT));
    }

    void updateView() {

        passportView.setImageURI(Uri.parse(AppState.activeStudent.getPassportUrl()));
        nameView.setText(AppState.activeStudent.getLastName()+" "+AppState.activeStudent.getFirstName());
        matNumberView.setText(AppState.activeStudent.getMatNumber());

        detailLayout.removeAllViews();
        detailLayout.addView(getDetailItemView("First Name", AppState.activeStudent.getFirstName()));
        detailLayout.addView(getDetailItemView("LastName", AppState.activeStudent.getLastName()));
        detailLayout.addView(getDetailItemView("Sex", AppState.activeStudent.getSex()));
        detailLayout.addView(getDetailItemView("Matriculation Number", AppState.activeStudent.getMatNumber()));
        detailLayout.addView(getDetailItemView("Department", AppState.activeStudent.getDepartment()));
        detailLayout.addView(getDetailItemView("Faculty", AppState.activeStudent.getFaculty()));
        detailLayout.addView(getDetailItemView("Level", AppState.activeStudent.getLevel()));
        detailLayout.addView(getDetailItemView("Phone number", AppState.activeStudent.getPhoneNumber()));
        detailLayout.addView(getDetailItemView("Email", AppState.activeStudent.getEmail()));
        detailLayout.addView(getDetailItemView("Course code", AppState.activeStudent.getCourseCode()));
        detailLayout.addView(getDetailItemView("Fingerprint state",
                (AppState.activeStudent.getFingerPrintTemplate() == null ||
                AppState.activeStudent.getFingerPrintTemplate().equals(""))?"not taken":"taken"));
    }

    private View getDetailItemView(String property, String value) {
        LinearLayout detailItem = (LinearLayout)layoutInflater.inflate(R.layout.detail_item, null, false);
        TextView propertyView = (TextView)detailItem.findViewById(R.id.item_property_id);
        TextView valueView = (TextView)detailItem.findViewById(R.id.item_value_id);

        propertyView.setText(property);
        valueView.setText(value);

        return detailItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem editMenu = menu.add(1,1,1, "Edit");
        editMenu.setIcon(R.drawable.ic_edit_black_24dp);
        editMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem deleteMenu = menu.add(2, 2, 2, "Delete");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            AppState.studentEditState = true;
            startActivity(new Intent(this, Register.class));
        } else if (requestCode == 6 && requestCode == Activity.RESULT_OK) {
            TemplateIdManager.deleteWithMatNo(getBaseContext(), AppState.activeStudent.getMatNumber());
            sendBroadcast(new Intent(Student.STUDENT_ADDED_INTENT));
            Toast.makeText(getBaseContext(), AppState.activeStudent.getFirstName() +
                    " " + AppState.activeStudent.getLastName() + " removed successfully", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:
                startActivityForResult(new Intent(this, AuthenticateActivity.class), 5);
                break;
            case 2:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Confirmation");
                builder.setMessage("Do you want to remove "+AppState.activeStudent.getFirstName()+
                        " "+AppState.activeStudent.getLastName()+ " permanently from "+
                         AppState.activeStudent.getCourseCode()+"?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (AppState.activeStudent.delete(getBaseContext()) >0) {
                            startActivityForResult(new Intent(getBaseContext(), AuthenticateActivity.class), 5);
                        } else {
                            Toast.makeText(getBaseContext(), "Error occured during operation", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver editedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            passportView.setImageResource(R.drawable.ic_person_outline_black_24dp);
            updateView();
        }
    };
}
