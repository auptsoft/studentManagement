package com.example.andrewoshodin.fingerprintregister.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrew Oshodin on 6/6/2018.
 */

public class SqliteHelper extends SQLiteOpenHelper {
    public final static int VERSION = 1;
    public final static String DATABASE_NAME = "databaseName";


    Student student = new Student();
    Course course = new Course();
    TemplateIdManager.TemplateId templateId = new TemplateIdManager.TemplateId();

    private final String CREATE_STUDENT_TABLE = student.getCreateTableString();
    private final String CREATE_COURSE_TABLE = course.getCreateTableString();
    private final String CREATE_TEMPLATE_ID_TABLE = templateId.getCreateTableString();

    public SqliteHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory cursorFactory, int version){
        super(context, databaseName, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_STUDENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TEMPLATE_ID_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + student.getTableName());
        sqLiteDatabase.execSQL("drop table if exists " + course.getTableName());
        sqLiteDatabase.execSQL("drop table if exists " + templateId.getTableName());
    }
}
