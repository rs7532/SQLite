package com.example.sqlite;

import static com.example.sqlite.Students.ADDRESS;
import static com.example.sqlite.Students.FATHER_NAME;
import static com.example.sqlite.Students.FATHER_PHONE;
import static com.example.sqlite.Students.KEY_ID;
import static com.example.sqlite.Students.MOBILE_PHONE;
import static com.example.sqlite.Students.MOTHER_NAME;
import static com.example.sqlite.Students.MOTHER_PHONE;
import static com.example.sqlite.Students.STATUS;
import static com.example.sqlite.Students.STUDENT_NAME;
import static com.example.sqlite.Students.TABLE_STUDENTS;
import static com.example.sqlite.Students.TELEPHONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class edit_status extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    /**
     * @author Roey Schwartz rs7532@bs.amalnet.k12.il
     * @version 1
     * @since 20.12.2023
     */
    Spinner Students_spinner;
    Button active_btn;
    HelperDB hlp;
    SQLiteDatabase db;
    ArrayList<String> studentsList;
    ArrayList<Integer> idList;
    Cursor crsr;
    static int selected_student;
    static String detail;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *  the onCreate will create also an arraylist with all the not active students and will show them in list view.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        active_btn = findViewById(R.id.active_btn);
        Students_spinner = findViewById(R.id.unactiveStudents);

        hlp = new HelperDB(this);
        studentsList = new ArrayList<>();
        idList = new ArrayList<>();
        studentsList.add("choose a student");
        String selection = STATUS+"=?";
        String[] selectionArgs = {"0"};
        String orderBy = KEY_ID;
        db = hlp.getReadableDatabase();
        crsr = db.query(TABLE_STUDENTS, null, selection, selectionArgs, null, null, orderBy);

        int Name_col = crsr.getColumnIndex(STUDENT_NAME);
        int id_col = crsr.getColumnIndex(KEY_ID);

        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            int id = crsr.getInt(id_col);
            String name = crsr.getString(Name_col);
            studentsList.add(name);
            idList.add(id);
            crsr.moveToNext();
        }
        crsr.close();
        db.close();

        Students_spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, studentsList);
        Students_spinner.setAdapter(adp);

    }

    /**
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     *
     *  the function will put the id of the student in an global variable
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0){
            selected_student = idList.get(position - 1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * the function is creating the new detail string of the student that the user is making active.
     */
    public void create_newData(){
        String selection = KEY_ID+"=?";
        String[] selectionArgs = {String.valueOf(selected_student)};
        db = hlp.getReadableDatabase();
        crsr = db.query(TABLE_STUDENTS, null, selection, selectionArgs, null, null, null);

        /**
         *
         * creating arraylist of the students' details
         */
        int key_id = crsr.getColumnIndex(KEY_ID);
        int Name_col = crsr.getColumnIndex(STUDENT_NAME);
        int address_col = crsr.getColumnIndex(ADDRESS);
        int mobile_col = crsr.getColumnIndex(MOBILE_PHONE);
        int telephone_col = crsr.getColumnIndex(TELEPHONE);
        int FatherName_col = crsr.getColumnIndex(FATHER_NAME);
        int MotherName_col = crsr.getColumnIndex(MOTHER_NAME);
        int FatherPhone_col = crsr.getColumnIndex(FATHER_PHONE);
        int MotherPhone_col = crsr.getColumnIndex(MOTHER_PHONE);

        crsr.moveToFirst();
        while(!(crsr.getInt(key_id) == selected_student) && !crsr.isAfterLast()){
            crsr.moveToNext();
        }
        String name = crsr.getString(Name_col);
        String address = crsr.getString(address_col);
        String mobile = crsr.getString(mobile_col);
        String telephone = crsr.getString(telephone_col);
        String fatherName = crsr.getString(FatherName_col);
        String motherName = crsr.getString(MotherName_col);
        String fatherPhone = crsr.getString(FatherPhone_col);
        String motherPhone = crsr.getString(MotherPhone_col);

        detail = " Student name - "+name+
                "\n Address - "+address+
                "\n Mobile - "+mobile+
                "\n Home phone - "+telephone+
                "\n Father's name - "+fatherName+
                "\n Mother's name - "+motherName+
                "\n Father's phone number - "+fatherPhone+
                "\n Mother's phone number - "+motherPhone+
                "\n Status - active"+"\n";
        db.close();
        crsr.close();
    }

    /**
     *
     * @param view
     *
     * the function will update in the database the student's status, delete his id from the list of the
     * deactivate students, will call create_newData function and will return to the all_data activity.
     */
    public void button_pressed(View view) {
        String update_msg = "UPDATE "+ TABLE_STUDENTS+" SET "+ STATUS+" = '1' WHERE "+Students.KEY_ID+" = "+selected_student+";";
        db = hlp.getWritableDatabase();
        db.execSQL(update_msg);
        db.close();
        Toast.makeText(this,"Student is active Successfully", Toast.LENGTH_LONG).show();
        idList.remove(Integer.valueOf(selected_student));
        create_newData();
        finish();
    }
}