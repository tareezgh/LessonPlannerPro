package com.example.lessonplannerpro;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.lessonplannerpro.dialogs.AddLesson;
import com.example.lessonplannerpro.dialogs.EditLesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class LessonDetails extends AppCompatActivity implements AddLesson.AddLessonDialogListener, EditLesson.EditLessonDialogListener {

    /****************************************** Firebase define ****************************************************/
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference mDatabase = db.getReference().child("Lessons");
    String currentUsername;

    public static boolean addedNewItem = false; // when add new lesson notify the teacher



    String chosenDate; // for menu option 2

    Context lessonDetails;
    RecyclerView recyclerView;
    ItemsRecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private final ArrayList<Lesson> lessonsList = new ArrayList<>();
    public static ArrayList<Lesson> existLesson = new ArrayList<>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        /****************************************** Check user and display layout ****************************************************/

        if (currentUser != null) {
            if (currentUser.getEmail().equals("tareezghandour15@gmail.com")) {
                setContentView(R.layout.activity_lesson_details_teacher);
            } else {
                setContentView(R.layout.activity_lesson_details_students);
                currentUsername = currentUser.getEmail();
            }
        }

        /****************************************** define Preferences ****************************************************/
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        lessonDetails = this; //context
    }


    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.listOfLessons);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(lessonDetails);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemsRecycleAdapter(this, lessonsList);
        recyclerView.setAdapter(adapter);

        getLessonsFromDB(); // get lessons from database

    }

    /****************************************** Open dialog ****************************************************/


    // open dialog
    public void OpenAddLesson(View view) {
        AddLesson addLessonDialog = new AddLesson();
        addLessonDialog.show(getSupportFragmentManager(), "addLesson dialog");
    }


    /****************************************** GET lesson from firebase ****************************************************/


    public void getLessonsFromDB() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lessonsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Lesson lesson = dataSnapshot.getValue(Lesson.class);
                    lessonsList.add(lesson);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        existLesson = lessonsList;
    }

    /****************************************** ADD lesson to firebase ****************************************************/


    @Override
    public void addLesson(String subject, String topic, String date, String time) {
        int flag = 1;
        getLessonsFromDB();

        //Check if date and time is busy
        for (Lesson lessonToCheck : existLesson) {
            if (lessonToCheck.getDate().equals(date) && lessonToCheck.getTime().equals(time))
                flag = 0;
        }//for

        /****************************************** call nearest lessons function ****************************************************/
        if (flag == 0) {
            //  Log.i(tag, " date exist");
            // Check empty lessons
            String id = mDatabase.push().getKey();
            Lesson lesson = new Lesson(subject, topic, date, time, id, currentUsername);
            nearestLessons(lesson, date, time);
        } else {
            /****************************************** confirm Lessons ****************************************************/
            new AlertDialog.Builder(lessonDetails).setTitle("Lesson Confirmation").setMessage("Lesson details:\nDate: " + date + "\nTime: " + time).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String id = mDatabase.push().getKey();
                    Lesson lesson = new Lesson(subject, topic, date, time, id, currentUsername); // create lesson
                    mDatabase.child(id).setValue(lesson).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(LessonDetails.this, "Added successfully", Toast.LENGTH_SHORT).show();
                            addedNewItem = true;
                        }
                    });
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).show();
        }
    }


    /****************************************** nearest Lessons ****************************************************/

    public void nearestLessons(Lesson lesson, String date, String time) {
        String[] timeSplit = time.split(":");
        int hours = Integer.parseInt(timeSplit[0]); /// takes the hours
        int minutes = Integer.parseInt(timeSplit[1]); /// takes the minutes

        ArrayList<Lesson> lessonsInSpecificDate = new ArrayList<>();
        ArrayList<String> timesInSpecificDate = new ArrayList<>();

        for (Lesson l : lessonsList) {
            if (l.getDate().equals(date)) {
                lessonsInSpecificDate.add(l); // collect all lessons in same date
                timesInSpecificDate.add(l.getTime());
            }
        }//for


        String nearestTime = ":";

        for (String str : timesInSpecificDate) {
            int i = 1;
            String[] maybeNearestTime = str.split(":");
            if (hours + i != Integer.parseInt(maybeNearestTime[0]) && hours + i + 1 != Integer.parseInt(maybeNearestTime[0]) && hours + i < 21) {
                nearestTime = hours + i + ":" + minutes;
                Log.i("TAG-nearestTime", "))) " + " , " + nearestTime);
            } else i++;

        }//for


        String finalNearestTime = nearestTime;
        new AlertDialog.Builder(lessonDetails).setTitle("Lesson Delay").setMessage("The nearest hour to your choice is : " + nearestTime).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                lesson.setTime(finalNearestTime);
                mDatabase.push().setValue(lesson).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LessonDetails.this, "Added successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();


    }

    /****************************************** Edit Lesson ****************************************************/


    @Override
    public void editLesson(String subject, String topic, String date, String time, String id) {
        Lesson lesson = new Lesson(subject, topic, date, time, id, currentUsername); // create lesson
        mDatabase.child(id).setValue(lesson).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(LessonDetails.this, "Updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }





    /****************************************** to choose special date from Menu ****************************************************/
    public void getLessonsDate() {
        ArrayList<Lesson> lessonsInSpecificDate = new ArrayList<>();
        LocalDate lt = LocalDate.now();
        int year = lt.getYear();
        int month = lt.getMonthValue() - 1;
        int day = lt.getDayOfMonth();


        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);

                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

                chosenDate = currentDateString;
//                Log.i("TAG chosenDate", chosenDate);

                for (Lesson lessonToCheck : lessonsList) {
                    // collect all lessons in same date
                    if (lessonToCheck.getDate().equals(chosenDate)) {
//                        Log.i("TAG 1", lessonToCheck.getDate());
                        lessonsInSpecificDate.add(lessonToCheck);
                    }
                }//for

                // set new adapter for dates
                adapter = new ItemsRecycleAdapter(lessonDetails, lessonsInSpecificDate);
                recyclerView.setAdapter(adapter);
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(lessonDetails, android.R.style.Theme_Holo_Dialog_MinWidth, onDateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }


    /****************************************** define Menu ****************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferences()).addToBackStack(null).commit();
                break;

            case R.id.lessonsDate:
                getLessonsDate();
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    /************************************************ define Preferences  **********************************************************/

    public static class MyPreferences extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference, rootKey);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            view.setBackgroundColor(Color.WHITE);
            super.onViewCreated(view, savedInstanceState);
        }
    }


}