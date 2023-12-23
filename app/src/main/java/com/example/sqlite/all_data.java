package com.example.sqlite;

import static com.example.sqlite.Students.ADDRESS;
import static com.example.sqlite.Students.CLASS_NUMBER;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.TextView;

import java.util.ArrayList;

public class all_data extends AppCompatActivity implements AdapterView.OnItemClickListener{
    /**
     * @author Roey Schwartz rs7532@bs.amalnet.k12.il
     * @version 1
     * @since 27.11.2023
     */
    ListView Data_lv;
    HelperDB hlp;
    ArrayList<String> StudentsDetails_tbl, StudentsGrades_tbl, StudentsNames, id_tbl, profession_tbl, assignment_tbl, quarter_tbl;
    ArrayList<Integer> unactiveStudents;
    SQLiteDatabase db;
    Cursor crsr;
    boolean firstRun;
    ArrayAdapter<String> adp_lv;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * the onCreate will create an String arrayList with the whole data of any student, and with long click on the students
     * it will activate/deactivate students
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data);

        Data_lv = findViewById(R.id.Data_lv);

        hlp = new HelperDB(this);
        StudentsDetails_tbl = new ArrayList<>();
        StudentsGrades_tbl = new ArrayList<>();
        StudentsNames = new ArrayList<>();
        id_tbl = new ArrayList<>();
        profession_tbl = new ArrayList<>();
        assignment_tbl = new ArrayList<>();
        quarter_tbl = new ArrayList<>();
        unactiveStudents = new ArrayList<>();
        firstRun = true;

        db = hlp.getReadableDatabase();
        crsr = db.query(TABLE_STUDENTS, null, null, null, null, null, null);
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
        int ClassNumber_col = crsr.getColumnIndex(CLASS_NUMBER);
        int Status_col = crsr.getColumnIndex(STATUS);
        String student_status;

        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            int id = crsr.getInt(key_id);
            int status = crsr.getInt(Status_col);
            String name = crsr.getString(Name_col);
            String address = crsr.getString(address_col);
            String mobile = crsr.getString(mobile_col);
            String telephone = crsr.getString(telephone_col);
            String fatherName = crsr.getString(FatherName_col);
            String motherName = crsr.getString(MotherName_col);
            String fatherPhone = crsr.getString(FatherPhone_col);
            String motherPhone = crsr.getString(MotherPhone_col);
            int ClassNumber = crsr.getInt(ClassNumber_col);

            if (status == 1){
                student_status = "active";
            }else{
                student_status = "not active";
                unactiveStudents.add(id);
            }

            StudentsDetails_tbl.add(" Student name - "+name+
                    "\n Address - "+address+
                    "\n Mobile - "+mobile+
                    "\n Home phone - "+telephone+
                    "\n Father's name - "+fatherName+
                    "\n Mother's name - "+motherName+
                    "\n Father's phone number - "+fatherPhone+
                    "\n Mother's phone number - "+motherPhone+
                    "\n Class number - "+ClassNumber+
                    "\n Status - "+student_status+"\n");
            StudentsNames.add(name);
            crsr.moveToNext();
        }
        db.close();
        crsr.close();


        /**
         *
         * creating arraylist of the grades of any student
         */
        String orderBy = Grades.KEY_ID;
        db = hlp.getReadableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, null, null, null, null, null, orderBy);

        int student_id = crsr.getColumnIndex(KEY_ID);
        int grades_col = crsr.getColumnIndex(Grades.STUDENT_GRADE);
        int profession_col = crsr.getColumnIndex(Grades.PROFESSION);
        int assignment_col = crsr.getColumnIndex(Grades.ASSIGNMENT_TYPE);
        int quarter_col = crsr.getColumnIndex(Grades.QUARTER);

        crsr.moveToFirst();
        while(!crsr.isAfterLast()){
            int id = crsr.getInt(student_id);
            String grade = crsr.getString(grades_col);
            String profession = crsr.getString(profession_col);
            String assignment = crsr.getString(assignment_col);
            int quarter = crsr.getInt(quarter_col);

            id_tbl.add(String.valueOf(id));
            StudentsGrades_tbl.add(grade);
            profession_tbl.add(profession);
            assignment_tbl.add(assignment);
            quarter_tbl.add(String.valueOf(quarter));
            crsr.moveToNext();
        }
        crsr.close();
        db.close();

        /**
         *
         * creating the long click listener of the listView
         */
        Data_lv.setOnItemClickListener(this);
        Data_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adp_lv = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, StudentsNames);
        Data_lv.setAdapter(adp_lv);

        Data_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint({"SetTextI18n", "UnsafeIntentLaunch"})
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                boolean active_flag = false;
                for (int i = 0; i < unactiveStudents.size(); i++){
                    if (unactiveStudents.get(i) == position + 1){
                        active_flag = true;
                        unactiveStudents.remove(unactiveStudents.get(i));
                    }
                }
                if (!active_flag) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(all_data.this);
                    adb.setTitle("Alert!");
                    final TextView tv = new TextView(all_data.this);
                    adb.setView(tv);

                    tv.setText("Are you sure that you want to deactivate this student?");
                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db = hlp.getWritableDatabase();
                            String updateMsg = "UPDATE "+ TABLE_STUDENTS+" SET "+ STATUS+" = '0' WHERE "+Students.KEY_ID+" = "+(position + 1)+";";
                            db.execSQL(updateMsg);
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog ad = adb.create();
                    ad.show();
                } else{
                    Intent si = new Intent(all_data.this, edit_status.class);
                    startActivity(si);
                }
                return true;
            }
        });
    }

    /**
     *
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     *            will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     *
     *  the function will check the list of the students' grades and will make a String with all the grades of the selected student,
     *  and also show in simple alertDialog the whole data of the selected student.
     */
    @SuppressLint({"SetTextI18n", "Range"})
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView tv = new TextView(this);
        adb.setView(tv);
        tv.setText(StudentsDetails_tbl.get(position));

        StringBuilder Student_grades = new StringBuilder();
        for(int i = 0; i < id_tbl.size(); i++){
            if (Integer.parseInt(id_tbl.get(i)) == position + 1){
                String tmp = " " + StudentsGrades_tbl.get(i) + " in " + profession_tbl.get(i) + " as "
                        + assignment_tbl.get(i) + " in quarter " + quarter_tbl.get(i) + "\n";
                Student_grades.append(tmp);
            }
            if (Integer.parseInt(id_tbl.get(i)) > position + 1){
                break;
            }
        }
        tv.append("\n" + "Grades:" + "\n" + Student_grades);

        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * the function will update the status of the student that the user consider to make him active student.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (firstRun){
            firstRun = false;
        }
        else{
            String detail = edit_status.detail;
            StudentsDetails_tbl.set(edit_status.selected_student - 1, detail);
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
            Intent si = new Intent(all_data.this, AddGrades.class);
            startActivity(si);
        }
        else if(st.equals("Add student")){
            Intent si = new Intent(all_data.this, AddStudent_MainScreen.class);
            startActivity(si);
        }
        else if(st.equals("Filter Data")){
            Intent si = new Intent(all_data.this, Filtering_Activity.class);
            startActivity(si);
        }
        else{
            closeOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }
}