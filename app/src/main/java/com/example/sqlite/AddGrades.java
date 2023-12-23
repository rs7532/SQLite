package com.example.sqlite;

import static com.example.sqlite.Students.CLASS_NUMBER;
import static com.example.sqlite.Students.STATUS;
import static com.example.sqlite.Students.TABLE_STUDENTS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddGrades extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /**
     * @author Roey Schwartz rs7532@bs.amalnet.k12.il
     * @version 1
     * @since 27.11.2023
     */
    Spinner Students_id;
    HelperDB hlp;
    SQLiteDatabase db;
    Cursor crsr;
    ArrayList<String> tbl;
    ArrayList<Integer> class_tbl;
    ArrayList<String> status_tbl;
    EditText Student_Grade, Profession, Assignment_Type, Quarter;
    int selected_studentID;
    String Student_name;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * here the code makes the list of the list of updated student in the database.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grades);

        Students_id = findViewById(R.id.students_id);
        Student_Grade = findViewById(R.id.GradeET);
        Profession = findViewById(R.id.ProfessionET);
        Assignment_Type = findViewById(R.id.AssignmentET);
        Quarter = findViewById(R.id.QuarterET);

        hlp = new HelperDB(this);
        tbl = new ArrayList<>();
        class_tbl = new ArrayList<>();
        status_tbl = new ArrayList<>();
        tbl.add("choose a student");
        db = hlp.getReadableDatabase();
        crsr = db.query(TABLE_STUDENTS, new String[]{Students.STUDENT_NAME, Students.STATUS, CLASS_NUMBER}, null, null, null, null, null);

        int Name_col = crsr.getColumnIndex(Students.STUDENT_NAME);
        int status_col = crsr.getColumnIndex(STATUS);
        int class_col = crsr.getColumnIndex(CLASS_NUMBER);

        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            String name = crsr.getString(Name_col);
            int status = crsr.getInt(status_col);
            int class_num = crsr.getInt(class_col);
            class_tbl.add(class_num);
            tbl.add(name);
            status_tbl.add(String.valueOf(status));
            crsr.moveToNext();
        }
        crsr.close();
        db.close();

        Students_id.setOnItemSelectedListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, tbl);
        Students_id.setAdapter(adp);
    }

    /**
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     *
     *  this code will save the name and the key id of selected student from the spinner in global variables
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_studentID = position;
        Student_name = Students_id.getItemAtPosition(position).toString();
    }

    /**
     *
     * @param parent The AdapterView that now contains no selected item.
     *
     * if nothing selected in the spinner so the id will be 0 for validity
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selected_studentID = 0;
    }

    /**
     *
     * @param view
     * the function checks that the user chose a student and checks also that the input is correct and if yes
     *  and the student is active, it will enter the student's grade to the database with the quarter, assignment type and profession.
     */
    public void Save_Pressed(View view) {
        boolean ok_flag = true;
        if (selected_studentID == 0){
            Toast.makeText(this,"Choose a student!", Toast.LENGTH_LONG).show();
        }
        else{
            if (Integer.parseInt(Student_Grade.getText().toString()) > 100){
                Toast.makeText(this,"Grade cannot be over 100!", Toast.LENGTH_LONG).show();
                Student_Grade.setText("");
                ok_flag = false;
            }
            if (Integer.parseInt(Quarter.getText().toString()) > 4 || Integer.parseInt(Quarter.getText().toString()) < 1){
                Toast.makeText(this,"Incorrect quarter!", Toast.LENGTH_LONG).show();
                Quarter.setText("");
                ok_flag = false;
            }
            if (status_tbl.get(selected_studentID - 1).equals("1") && ok_flag){
                ContentValues cv = new ContentValues();
                cv.put(Grades.KEY_ID, String.valueOf(selected_studentID));
                cv.put(Grades.NAME, Student_name);
                cv.put(Grades.STUDENT_GRADE, Student_Grade.getText().toString());
                cv.put(Grades.PROFESSION, Profession.getText().toString());
                cv.put(Grades.ASSIGNMENT_TYPE, Assignment_Type.getText().toString());
                cv.put(Grades.QUARTER, Quarter.getText().toString());
                cv.put(Grades.CLASS, class_tbl.get(selected_studentID - 1));

                db = hlp.getWritableDatabase();
                db.insert(Grades.TABLE_GRADES, null, cv);
                db.close();

                Student_Grade.setText("");
                Profession.setText("");
                Assignment_Type.setText("");
                Quarter.setText("");
                Toast.makeText(this,"Grade added successfully!", Toast.LENGTH_LONG).show();
            }
            else{
                if (ok_flag){
                    Toast.makeText(this,"you can't add grades to unactivated student!", Toast.LENGTH_LONG).show();
                }
            }
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
        if(st.equals("Add student")){
            Intent si = new Intent(AddGrades.this, AddStudent_MainScreen.class);
            startActivity(si);
        }
        else if(st.equals("See all students' details")){
            Intent si = new Intent(AddGrades.this, all_data.class);
            startActivity(si);
        }
        else if(st.equals("Filter Data")){
            Intent si = new Intent(AddGrades.this, Filtering_Activity.class);
            startActivity(si);
        }
        else{
            closeOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }
}