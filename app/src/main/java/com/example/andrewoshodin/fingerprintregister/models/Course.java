package com.example.andrewoshodin.fingerprintregister.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class Course extends Content {
    public final String TABLE_NAME = "courses";
    public static final String ALREADY_REGISTERED = "ALREADY_REGISTERED";
    public static final String COURSE_ADDED_INTENT = "COURSE_ADDED_INTENT";
    public static final String COURSE_EDITED_INTENT = "COURSE_EDITED_INTENT";

    private int id;
    private String courseCode;
    private String courseTitle;
    private String description;

    public Course(){
        this(0, "", "", "");
    }

    public Course(int id, String courseCode, String courseTitle, String description) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.description = description;
    }

    public Course(String courseCode) {
        this.courseCode = courseCode;
    }

    public String insert(Context context) {
        if (get(context) == null) {
            ArrayList<HashMap<String, String>> hashMapArrayList = new ArrayList<>();
            hashMapArrayList.add(toHashMap());
            return insert(context, hashMapArrayList);
        } else {
            return ALREADY_REGISTERED;
        }
    }

    public int update(Context context) {
        return update(context, toHashMap(), getColumns()[1]+"=?",
                new String[]{getCourseCode()});
    }

    public int delete(Context context) {
        return delete(context, getColumns()[1]+"=?",
                new String[]{getCourseCode()});
    }

    public Course get(Context context) {
        ArrayList<HashMap<String, String>> hashMapArrayList = get(context,
                getColumns()[1]+"=?",
                new String[]{getCourseCode()});
        if (hashMapArrayList.size() > 0) {
            return fromHashMap(hashMapArrayList.get(0));
        } else return null;
    }

    public ArrayList<Course> getAll(Context context) {
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<HashMap<String, String>> hashMapArrayList = get(context, "", null);
        for (HashMap<String, String> stringHashMap : hashMapArrayList) {
            courses.add(fromHashMap(stringHashMap));
        }
        return courses;
    }

    @Override
    public String[] getColumns() {
        return new String[]{
                "id",
                "courseCode",
                "courseTitle",
                "description"
        };
    }

    @Override
    protected String[] getColumnsType() {
        return new String[] {
                "integer primary key autoincrement",
                "text",
                "text",
                "text",
        };
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    private HashMap<String, String> toHashMap() {
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put(getColumns()[1], getCourseCode());
        stringHashMap.put(getColumns()[2], getCourseTitle());
        stringHashMap.put(getColumns()[3], getDescription());

        return stringHashMap;
    }

    private Course fromHashMap(HashMap<String, String> hashMap) {
        return new Course(0,
                hashMap.get(getColumns()[1]),
                hashMap.get(getColumns()[2]),
                hashMap.get(getColumns()[3]));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}