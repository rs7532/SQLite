package com.example.sqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Filtering_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    /**
     * @author Roey Schwartz rs7532@bs.amalnet.k12.il
     * @version 1
     * @since 23.12.2023
     */
    HelperDB hlp;
    SQLiteDatabase db;
    Cursor crsr;
    ArrayList<String> filters_tbl, unfiltered_students, per_class, assignment_list, quarter1, quarter2, quarter3, quarter4, quarterNumber;
    Spinner filters_spinner, selectQuarter_spinner;
    ListView filtered_lv;
    ArrayAdapter<String> filtered_lv_adp;
    TextView chooseQuarter_tv;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * the onCreate will also create the arraylists with the students' grades according to the filters
     * (quarter, assignment type, class), and an unfiltered arraylist with the students' grades
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        filtered_lv = findViewById(R.id.filtered_lv);
        filters_spinner = findViewById(R.id.selectFilter_spinner);
        selectQuarter_spinner = findViewById(R.id.selectQuarter_spinner);
        chooseQuarter_tv = findViewById(R.id.chooseQuarter_tv);

        quarter1 = new ArrayList<>();
        quarter2 = new ArrayList<>();
        quarter3 = new ArrayList<>();
        quarter4 = new ArrayList<>();
        quarterNumber = new ArrayList<>();
        assignment_list = new ArrayList<>();
        per_class = new ArrayList<>();
        unfiltered_students = new ArrayList<>();
        filters_tbl = new ArrayList<>();
        filters_tbl.add("choose a filter");
        filters_tbl.add("by a quarter");
        filters_tbl.add("by a class");
        filters_tbl.add("by an assignment");

        quarterNumber.add("1");
        quarterNumber.add("2");
        quarterNumber.add("3");
        quarterNumber.add("4");
        hlp = new HelperDB(this);

        /**
         * creating unfiltered arraylist of students
         */
        db = hlp.getReadableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, null, null, null, null, null, null);

        int name = crsr.getColumnIndex(Grades.NAME);
        int grade = crsr.getColumnIndex(Grades.STUDENT_GRADE);
        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            String StudentName = crsr.getString(name);
            String StudentGrade = crsr.getString(grade);
            unfiltered_students.add(StudentName+": "+StudentGrade);
            crsr.moveToNext();
        }
        db.close();
        crsr.close();

        /**
         * creating arraylist of filtered students by a quarter
         */
        String orderBy = Grades.QUARTER;
        db = hlp.getReadableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, null, null, null, null, null, orderBy);

        int quarter_col = crsr.getColumnIndex(Grades.QUARTER);
        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            String StudentGrade = crsr.getString(grade);
            String StudentName = crsr.getString(name);
            int quarter = crsr.getInt(quarter_col);
            if (quarter == 1){
                quarter1.add(StudentName+": "+StudentGrade);
            }
            else if (quarter == 2){
                quarter2.add(StudentName+": "+StudentGrade);

            }
            else if (quarter == 3){
                quarter3.add(StudentName+": "+StudentGrade);

            }
            else if (quarter == 4){
                quarter4.add(StudentName+": "+StudentGrade);

            }
            crsr.moveToNext();
        }
        db.close();
        crsr.close();

        /**
         * creating arraylist of filtered students per class
         */
        orderBy = Grades.CLASS;
        db = hlp.getReadableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, null, null, null, null, null, orderBy);

        int class_col = crsr.getColumnIndex(Grades.CLASS);
        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            String StudentGrade = crsr.getString(grade);
            String StudentName = crsr.getString(name);
            int class_num = crsr.getInt(class_col);
            per_class.add(StudentName+": "+StudentGrade+" from class "+class_num);
            crsr.moveToNext();
        }
        db.close();
        crsr.close();


        /**
         * creating arraylist of filtered students by assignment type
         */
        orderBy = Grades.ASSIGNMENT_TYPE;
        db = hlp.getReadableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, null, null, null, null, null, orderBy);

        int assignment_col = crsr.getColumnIndex(Grades.ASSIGNMENT_TYPE);
        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            String StudentGrade = crsr.getString(grade);
            String StudentName = crsr.getString(name);
            String assignment = crsr.getString(assignment_col);
            assignment_list.add(StudentName+": "+StudentGrade+" in assignment "+assignment);
            crsr.moveToNext();
        }
        db.close();
        crsr.close();

        filters_spinner.setOnItemSelectedListener(this);
        selectQuarter_spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> filtersSpinner_adp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, filters_tbl);
        filters_spinner.setAdapter(filtersSpinner_adp);

        ArrayAdapter<String> selectQuarter_spinner_adp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, quarterNumber);
        selectQuarter_spinner.setAdapter(selectQuarter_spinner_adp);

        filtered_lv.setOnItemClickListener(this);
        filtered_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        filtered_lv_adp = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, unfiltered_students);
        filtered_lv.setAdapter(filtered_lv_adp);
    }

    /**
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     *
     *  the function will show the students' grades by the filter according to the user choice
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == filters_spinner) {
            if (position == 1){
                selectQuarter_spinner.setVisibility(View.VISIBLE);
                chooseQuarter_tv.setVisibility(View.VISIBLE);
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, quarter1);
                filtered_lv.setAdapter(filtered_lv_adp);
            }
            else if(position == 2){
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, per_class);
                filtered_lv.setAdapter(filtered_lv_adp);
                selectQuarter_spinner.setVisibility(View.INVISIBLE);
                chooseQuarter_tv.setVisibility(View.INVISIBLE);
            }
            else if (position == 3){
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, assignment_list);
                filtered_lv.setAdapter(filtered_lv_adp);
                selectQuarter_spinner.setVisibility(View.INVISIBLE);
                chooseQuarter_tv.setVisibility(View.INVISIBLE);
            }
        }
        else{
            if (position == 0){
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, quarter1);
                filtered_lv.setAdapter(filtered_lv_adp);
            }
            if (position == 1){
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, quarter2);
                filtered_lv.setAdapter(filtered_lv_adp);
            }
            if (position == 2){
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, quarter3);
                filtered_lv.setAdapter(filtered_lv_adp);
            }
            if (position == 3){
                filtered_lv_adp = new ArrayAdapter<>(this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, quarter4);
                filtered_lv.setAdapter(filtered_lv_adp);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}


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
            Intent si = new Intent(Filtering_Activity.this, AddStudent_MainScreen.class);
            startActivity(si);
        }
        else if(st.equals("See all students' details")){
            Intent si = new Intent(Filtering_Activity.this, all_data.class);
            startActivity(si);
        }
        else if(st.equals("Add grade")){
            Intent si = new Intent(Filtering_Activity.this, AddGrades.class);
            startActivity(si);
        }
        else{
            closeOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }
}