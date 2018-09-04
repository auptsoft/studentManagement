package com.example.andrewoshodin.fingerprintregister.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrew Oshodin on 6/6/2018.
 */

public abstract class Content {
    public final String KEY_ID = "_id";

    public SQLiteDatabase getWritableDatabase(Context context) {
        SqliteHelper sqliteHelper = new SqliteHelper(context, SqliteHelper.DATABASE_NAME,
                null, SqliteHelper.VERSION);
        return sqliteHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase(Context context) {
        SqliteHelper sqliteHelper = new SqliteHelper(context, SqliteHelper.DATABASE_NAME,
                null, SqliteHelper.VERSION);
        return sqliteHelper.getReadableDatabase();
    }

    public abstract String getTableName();

    public abstract String[] getColumns();

    protected abstract String[] getColumnsType();

    public String getCreateTableString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table ");
        stringBuilder.append(getTableName());
        stringBuilder.append(" (");
        for (int index = 0; index < getColumns().length; index++) {
            stringBuilder.append(getColumns()[index]);
            stringBuilder.append(" ");
            stringBuilder.append(getColumnsType()[index]);
            if (index < getColumns().length - 1) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append(" );");
            }
        }
        return stringBuilder.toString();
    }

    public final String insert(Context context, ArrayList<HashMap<String, String>> hashMaps) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase(context);
        for (HashMap<String, String> stringHashMap : hashMaps) {
            ContentValues contentValues = new ContentValues();
            for (String key : getColumns()) {
                contentValues.put(key, stringHashMap.get(key));
            }
            try {
                sqLiteDatabase.insertOrThrow(getTableName(), null, contentValues);
            } catch (SQLException se) {
                se.printStackTrace();
                return se.getLocalizedMessage();
            }
        }
        return null;
    }

    public final int update(Context context, HashMap<String, String> hashMap, String where,
                      String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase(context);
        ContentValues contentValues = new ContentValues();
        for (String key : getColumns()) {
            if (key.equals("id")) continue;
            contentValues.put(key, hashMap.get(key));
        }
        return sqLiteDatabase.update(getTableName(), contentValues, where, whereArgs);
    }


    public final ArrayList<HashMap<String, String>> get(Context context, String where, String[]
            whereArgs) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase(context);
        Cursor cursor = sqLiteDatabase.query(getTableName(), getColumns(), where, whereArgs, null, null, null);

        ArrayList<HashMap<String, String>> arrayListOfHashMap = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            {
                do {
                    HashMap<String, String> stringHashMap = new HashMap<>();
                    for (String column : getColumns()) {
                        String value = cursor.getString(cursor.getColumnIndex(column));
                        stringHashMap.put(column, value);
                    }
                    arrayListOfHashMap.add(stringHashMap);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return arrayListOfHashMap;
    }

    public final int delete(Context context, String where, String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase(context);
        return sqLiteDatabase.delete(getTableName(), where, whereArgs);
    }

    public ArrayList<HashMap<String, String>> getFromJsonString(Context con, String jsonString) {
        ArrayList<HashMap<String, String>> hashMapArrayList = new ArrayList<>();
        try {
            JSONObject fullJson = new JSONObject(jsonString);
            JSONArray dataArray = fullJson.getJSONArray("data");
            for (int index = 0; index < dataArray.length(); index++) {
                HashMap<String, String> itemHashMap = new HashMap<>();
                JSONObject jsonObject = dataArray.getJSONObject(index);
                for (String itemString : getColumns()) {
                    if (itemString.equals("others")) {
                        itemHashMap.put("others", "other");
                        continue;
                    }
                    itemHashMap.put(itemString, jsonObject.getString(itemString));
                }
                hashMapArrayList.add(itemHashMap);
            }
        } catch (JSONException je) {
            Toast.makeText(con, je.getMessage(), Toast.LENGTH_LONG).show();
            je.printStackTrace();
        }
        return hashMapArrayList;
    }

    public HashMap<String, String> getSingleHashMapFromJson(String jsonString) {
        HashMap<String, String> itemHashMap = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            for (String itemString : getColumns()) {
                if (itemString.equals("others")) {
                    itemHashMap.put("others", "other");
                    continue;
                }
                itemHashMap.put(itemString, jsonObject.getString(itemString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return itemHashMap;
    }
}