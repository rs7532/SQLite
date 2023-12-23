package com.example.sqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddStudent_MainScreen extends AppCompatActivity {
    /**
     * @author Roey Schwartz rs7532@bs.amalnet.k12.il
     * @version 1
     * @since 27.11.2023
     * this code will help our headteacher of the grade to manage his students' details and their grades through the year
     */
    SQLiteDatabase db;
    HelperDB hlp;
    EditText studentET, AddressET, MobileET, HomePhoneET, FatherET, MotherET, FatherNumberET, MotherNumberET, ClassNumberET;
    ToggleButton Status;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentET = findViewById(R.id.studentET);
        AddressET = findViewById(R.id.AddressET);
        MobileET = findViewById(R.id.MobileET);
        HomePhoneET = findViewById(R.id.HomePhoneET);
        FatherET = findViewById(R.id.FatherET);
        MotherET = findViewById(R.id.MotherET);
        FatherNumberET = findViewById(R.id.FatherNumberET);
        MotherNumberET = findViewById(R.id.MotherNumberET);
        ClassNumberET = findViewById(R.id.ClassNumber);
        Status = findViewById(R.id.Status);

        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        db.close();

    }

    /**
     *
     * @param view
     * this code is checking the validity of user input and making sure with the user that the his input is
     what he wanted to write down to the database, and transfer the user to the receiving grades activity.
     */
    @SuppressLint("SetTextI18n")
    public void Next_pressed(View view) {
        boolean ok_flag = true;
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Alert!");
        final TextView tv = new TextView(this);
        adb.setView(tv);
        if (MobileET.getText().toString().charAt(0) != '0'){
            Toast.makeText(this,"Incorrect phone pattern!", Toast.LENGTH_LONG).show();
            MobileET.setText("");
            ok_flag = false;
        }
        else if (HomePhoneET.getText().toString().charAt(0) != '0'){
            Toast.makeText(this,"Incorrect phone pattern!", Toast.LENGTH_LONG).show();
            HomePhoneET.setText("");
            ok_flag = false;
        }
        else if (FatherNumberET.getText().toString().charAt(0) != '0'){
            Toast.makeText(this,"Incorrect phone pattern!", Toast.LENGTH_LONG).show();
            FatherNumberET.setText("");
            ok_flag = false;
        }
        else if (MotherNumberET.getText().toString().charAt(0) != '0'){
            Toast.makeText(this,"Incorrect phone pattern!", Toast.LENGTH_LONG).show();
            MotherNumberET.setText("");
            ok_flag = false;
        }
        else if (ClassNumberET.getText().toString().charAt(0) == '0'){
            Toast.makeText(this,"Class number cannot be 0!", Toast.LENGTH_LONG).show();
            ClassNumberET.setText("");
            ok_flag = false;
        }

        tv.setText("Are you sure you wrote the right details? \n"+
                    " Student name - "+studentET.getText().toString()+
                    "\n Address - "+AddressET.getText().toString()+
                    "\n Mobile - "+MobileET.getText().toString()+
                    "\n Home phone - "+HomePhoneET.getText().toString()+
                    "\n Father's name - "+FatherET.getText().toString()+
                    "\n Mother's name - "+MotherET.getText().toString()+
                    "\n Father's phone number - "+FatherNumberET.getText().toString()+
                    "\n Mother's phone number - "+MotherNumberET.getText().toString()+
                    "\n Class number - "+ClassNumberET.getText().toString()+
                    "\n Status - "+Status.getText().toString());


        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentValues cv = new ContentValues();
                cv.put(Students.STUDENT_NAME, studentET.getText().toString());
                cv.put(Students.ADDRESS, AddressET.getText().toString());
                cv.put(Students.MOBILE_PHONE, MobileET.getText().toString());
                cv.put(Students.TELEPHONE, HomePhoneET.getText().toString());
                cv.put(Students.FATHER_NAME, FatherET.getText().toString());
                cv.put(Students.MOTHER_NAME, MotherET.getText().toString());
                cv.put(Students.FATHER_PHONE, FatherNumberET.getText().toString());
                cv.put(Students.MOTHER_PHONE, MotherNumberET.getText().toString());
                cv.put(Students.CLASS_NUMBER, ClassNumberET.getText().toString());

                if (Status.isChecked()){
                    cv.put(Students.STATUS, String.valueOf(1));
                }
                else{
                    cv.put(Students.STATUS, String.valueOf(0));
                }

                db = hlp.getWritableDatabase();
                db.insert(Students.TABLE_STUDENTS, null, cv);
                db.close();

                studentET.setText("");
                AddressET.setText("");
                MobileET.setText("");
                HomePhoneET.setText("");
                FatherET.setText("");
                MotherET.setText("");
                FatherNumberET.setText("");
                MotherNumberET.setText("");
                ClassNumberET.setText("");

                Intent si = new Intent(AddStudent_MainScreen.this, AddGrades.class);
                startActivity(si);
            }
        });

        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (ok_flag){
            AlertDialog ad = adb.create();
            ad.show();
        }
    }

    /**
     * <p>
     *     the function get a variable of View type,
     *     the function will transfer the user to the credits screen
     * </>
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * <p
     *      the function get a variable of MenuItem type
     * </>
     * @return the function will as the user choice will move to the credits screen or close the menu
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        String st = item.getTitle().toString();
        if(st.equals("Add grade")){
            Intent si = new Intent(AddStudent_MainScreen.this, AddGrades.class);
            startActivity(si);
        }
        else if(st.equals("See all students' details")){
            Intent si = new Intent(AddStudent_MainScreen.this, all_data.class);
            startActivity(si);
        }
        else if(st.equals("Filter Data")){
            Intent si = new Intent(AddStudent_MainScreen.this, Filtering_Activity.class);
            startActivity(si);
        }
        else{
            closeOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }
}