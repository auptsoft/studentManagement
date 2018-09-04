package com.example.andrewoshodin.fingerprintregister.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrew Oshodin on 8/20/2018.
 */

public class Student extends Content {
    public final String TABLE_NAME = "student";
    public static final String ALREADY_REGISTERED = "ALREADY_REGISTERED";
    public static final String STUDENT_ADDED_INTENT = "COURSE_ADDED_INTENT";
    public static final String STUDENT_EDITED_INTENT = "STUDENT_EDITED_INTENT";

    private int id;
    private String firstName;
    private String lastName;
    private String sex;
    private String matNumber;
    private String level;
    private String department;
    private String faculty;
    private String phoneNumber;
    private String email;
    private String passportUrl;
    private String fingerPrintTemplate;
    private String courseCode;

    public Student() {
        this(0, "", "", "", "", "", "",
                "", "", "", "", "", "");
    }


    public Student(int id, String firstName, String lastName, String sex, String matNumber, String level,
                   String department, String faculty, String phoneNumber, String email,
                   String passportUrl, String fingerPrintTemplate, String courseCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.matNumber = matNumber;
        this.level = level;
        this.department = department;
        this.faculty = faculty;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passportUrl = passportUrl;
        this.fingerPrintTemplate = fingerPrintTemplate;
        this.courseCode = courseCode;
    }

    public Student(String matNumber, String courseCode) {
        this.matNumber = matNumber;
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
        return update(context, toHashMap(), getColumns()[4] + "=? AND " + getColumns()[12] + "=?",
                new String[]{getMatNumber(), getCourseCode()});
    }

    public int delete(Context context) {
        return delete(context, getColumns()[4] + "=? AND " + getColumns()[12] + "=?",
                new String[]{getMatNumber(), getCourseCode()});
    }

    public Student get(Context context) {
        ArrayList<HashMap<String, String>> hashMapArrayList = get(context,
                getColumns()[4] + "=? AND " + getColumns()[12] + "=?", new String[]{getMatNumber(),
                        getCourseCode()});
        if (hashMapArrayList.size() > 0) {
            return fromHashMap(hashMapArrayList.get(0));
        } else return null;
    }

    public ArrayList<Student> getAll(Context context) {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<HashMap<String, String>> hashMapArrayList = get(context, "", null);
        for (HashMap<String, String> stringHashMap : hashMapArrayList) {
            students.add(fromHashMap(stringHashMap));
        }
        return students;
    }

    public ArrayList<Student> getAll(Context context, String courseCode) {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<HashMap<String, String>> hashMapArrayList = get(context, getColumns()[12] + "=?",
                new String[]{courseCode});
        for (HashMap<String, String> stringHashMap : hashMapArrayList) {
            students.add(fromHashMap(stringHashMap));
        }
        return students;
    }

    @Override
    public String[] getColumns() {
        return new String[]{
                "id",
                "firstName",
                "lastName",
                "sex",
                "matNumber",
                "level",
                "department",
                "faculty",
                "phoneNumber",
                "email",
                "passportUrl",
                "fingerPrintTemplate",
                "courseCode"
        };
    }

    @Override
    protected String[] getColumnsType() {
        return new String[]{
                "integer primary key autoincrement",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text",
                "text"
        };
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    private HashMap<String, String> toHashMap() {
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put(getColumns()[1], getFirstName());
        stringHashMap.put(getColumns()[2], getLastName());
        stringHashMap.put(getColumns()[3], getSex());
        stringHashMap.put(getColumns()[4], getMatNumber());
        stringHashMap.put(getColumns()[5], getLevel());
        stringHashMap.put(getColumns()[6], getDepartment());
        stringHashMap.put(getColumns()[7], getFaculty());
        stringHashMap.put(getColumns()[8], getPhoneNumber());
        stringHashMap.put(getColumns()[9], getEmail());
        stringHashMap.put(getColumns()[10], getPassportUrl());
        stringHashMap.put(getColumns()[11], getFingerPrintTemplate());
        stringHashMap.put(getColumns()[12], getCourseCode());

        return stringHashMap;
    }

    private Student fromHashMap(HashMap<String, String> hashMap) {
        return new Student(Integer.valueOf(hashMap.get(getColumns()[0])),
                hashMap.get(getColumns()[1]),
                hashMap.get(getColumns()[2]),
                hashMap.get(getColumns()[3]),
                hashMap.get(getColumns()[4]),
                hashMap.get(getColumns()[5]),
                hashMap.get(getColumns()[6]),
                hashMap.get(getColumns()[7]),
                hashMap.get(getColumns()[8]),
                hashMap.get(getColumns()[9]),
                hashMap.get(getColumns()[10]),
                hashMap.get(getColumns()[11]),
                hashMap.get(getColumns()[12])
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMatNumber() {
        return matNumber;
    }

    public void setMatNumber(String matNumber) {
        this.matNumber = matNumber;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassportUrl() {
        return passportUrl;
    }

    public void setPassportUrl(String passportUrl) {
        this.passportUrl = passportUrl;
    }

    public String getFingerPrintTemplate() {
        return fingerPrintTemplate;
    }

    public void setFingerPrintTemplate(String fingerPrintTemplate) {
        this.fingerPrintTemplate = fingerPrintTemplate;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}