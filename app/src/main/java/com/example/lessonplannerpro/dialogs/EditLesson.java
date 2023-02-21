package com.example.lessonplannerpro.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.lessonplannerpro.R;
import com.example.lessonplannerpro.Lesson;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class EditLesson extends AppCompatDialogFragment {
    EditText subject, topic;
    Button pickDate, pickTime;
    TextView dateText, timeText;

    public static EditLessonDialogListener listener;

    public static Lesson lessonToEdit;

    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;


    public interface EditLessonDialogListener {
        void editLesson(String subject, String topic, String date, String time, String id);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_lesson, null);

        subject = view.findViewById(R.id.subject);
        topic = view.findViewById(R.id.topic);
        pickDate = view.findViewById(R.id.pickDate); // button
        dateText = view.findViewById(R.id.textViewDate); // text

        pickTime = view.findViewById(R.id.pickTime); // button
        timeText = view.findViewById(R.id.textviewTime); // text

        // set information for specific lesson to edit
        subject.setText(lessonToEdit.getSubject());
        topic.setText(lessonToEdit.getTopic());
        dateText.setText(lessonToEdit.getDate());
        timeText.setText(lessonToEdit.getTime());


        builder.setView(view).setTitle("Edit Lesson").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String subjectString = subject.getText().toString();
                String topicString = topic.getText().toString();
                String dateString = dateText.getText().toString();
                String timeString = timeText.getText().toString();
                if (checkValid(subjectString, topicString, dateString, timeString)) {
                    listener.editLesson(subjectString, topicString, dateString, timeString, lessonToEdit.getID());
                }
            }
        });

/****************************************** date picker ****************************************************/

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate lt = LocalDate.now();
                int year = lt.getYear();
                int month = lt.getMonthValue() - 1;
                int day = lt.getDayOfMonth();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);

                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
                dateText.setText(currentDateString);
            }
        };

/****************************************** time picker ****************************************************/

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZonedDateTime tt = ZonedDateTime.now();
                ZonedDateTime IsraelDateTime = tt.withZoneSameInstant(ZoneId.of("Asia/Jerusalem"));
                int hour = IsraelDateTime.getHour();
                int minute = IsraelDateTime.getMinute();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, onTimeSetListener, hour, minute, true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });


        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (minute < 10) timeText.setText(hour + ":0" + minute);
                else timeText.setText(hour + ":" + minute);
            }
        };

        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EditLessonDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");

        }
    }

    private boolean checkValid(String subjectString, String topicString, String dateString, String timeString) {
        // checking if fields are empty
        if (TextUtils.isEmpty(subjectString) || TextUtils.isEmpty(topicString) || TextUtils.isEmpty(dateString) || TextUtils.isEmpty(timeString)) {
            Toast.makeText(EditLesson.this.getContext(), "Fill all fields please", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


}
