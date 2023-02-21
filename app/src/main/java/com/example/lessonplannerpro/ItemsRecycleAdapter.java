package com.example.lessonplannerpro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lessonplannerpro.dialogs.EditLesson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ItemsRecycleAdapter extends RecyclerView.Adapter<ItemsRecycleAdapter.MyViewHolder> {
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = db.getReference().child("Lessons");

    public ArrayList<Lesson> lessonsList;
    Context context;
    AlertDialog dialog;

    public static ItemsRecycleAdapter listener;

    public ItemsRecycleAdapter(Context context, ArrayList<Lesson> lessonsList) {
        this.lessonsList = lessonsList;
        this.context = context;
        listener = this;
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }


    /****************************************** RecyclerView.Adapter ****************************************************/
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (currentUser != null) {
            if (currentUser.getEmail().equals("tareezghandour15@gmail.com")) {
                holder.setLessonForTeacher(lessonsList.get(position));
            } else {
                if (currentUser.getEmail().equals(lessonsList.get(position).getUserName())) {
                    holder.setLessonForStudent(lessonsList.get(position));
                }

            }
        }

    }


    @Override
    public int getItemCount() {
        return lessonsList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView subject, topic, date, time, student;
        Button itemEditButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            topic = itemView.findViewById(R.id.topic);
            student = itemView.findViewById(R.id.student);
            itemEditButton = itemView.findViewById(R.id.itemEditButton);

            /****************************************** edit item ****************************************************/

            itemEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    EditLesson.lessonToEdit = lessonsList.get(pos);
                    EditLesson editLessonDialog = new EditLesson();
                    editLessonDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "editLesson dialog");

                }

            });

            /****************************************** delete item ****************************************************/

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();

                    dialog = new AlertDialog.Builder(context).setTitle("WARNING").setMessage("Are you sure to delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (pos != RecyclerView.NO_POSITION) {
                                String userId = lessonsList.get(pos).getID();
                                mDatabase.child(userId).removeValue();
                                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.cancel();
                        }
                    }).show();


                    return false;
                }
            });
        }


        public void setLessonForStudent(Lesson lesson) {
            subject.setText("Subject: " + lesson.getSubject());
            topic.setText("Topic: " + lesson.getTopic());
            date.setText(lesson.getDate().toString());
            time.setText(lesson.getTime());
        }

        public void setLessonForTeacher(Lesson lesson) {
            subject.setText("Subject: " + lesson.getSubject());
            topic.setText("Topic: " + lesson.getTopic());
            date.setText(lesson.getDate().toString());
            time.setText(lesson.getTime());
            student.setText("Student: " + lesson.getUserName());
        }
    }

    /************************************************ called from receiver **********************************************************/

    public void UpdateLessonList() {
        Log.e( "TAGGGGGGG"," UpdateLessonList");
        ItemsRecycleAdapter.listener.notifyDataSetChanged();
    }


}
