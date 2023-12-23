package com.example.sqlite;

import static com.example.sqlite.Students.*;
import static com.example.sqlite.Grades.*;
import static com.example.sqlite.Students.KEY_ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Mission10.db";
    private static final int DATABASE_VERSION = 1;
    String strCreate, strDelete;
    public HelperDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        strCreate = "CREATE TABLE "+TABLE_STUDENTS;
        strCreate += " ("+KEY_ID+" INTEGER PRIMARY KEY,";
        strCreate += " "+STUDENT_NAME+" TEXT,";
        strCreate += " "+STATUS+" INTEGER,";
        strCreate += " "+ADDRESS+" TEXT,";
        strCreate += " "+MOBILE_PHONE+" TEXT,";
        strCreate += " "+TELEPHONE+" TEXT,";
        strCreate += " "+FATHER_NAME+" TEXT,";
        strCreate += " "+MOTHER_NAME+" TEXT,";
        strCreate += " "+FATHER_PHONE+" TEXT,";
        strCreate += " "+MOTHER_PHONE+" TEXT, ";
        strCreate += " "+CLASS_NUMBER+" INTEGER";
        strCreate += ");";
        db.execSQL(strCreate);

        strCreate = "CREATE TABLE "+TABLE_GRADES;
        strCreate += " ("+KEY_ID+" INTEGER,";
        strCreate += " "+NAME+" TEXT, ";
        strCreate += " "+STUDENT_GRADE+" INTEGER,";
        strCreate += " "+PROFESSION+" TEXT,";
        strCreate += " "+ASSIGNMENT_TYPE+" TEXT,";
        strCreate += " "+QUARTER+" INTEGER, ";
        strCreate += " "+CLASS+" INTEGER";
        strCreate += ");";
        db.execSQL(strCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        strDelete = "DROP TABLE IF EXISTS "+TABLE_STUDENTS;
        db.execSQL(strDelete);
        strDelete = "DROP TABLE IF EXISTS "+TABLE_GRADES;
        db.execSQL(strDelete);

        onCreate(db);
    }
}
