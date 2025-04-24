package com.example.skillswap;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ScheduleClass extends AppCompatActivity {

    private EditText editTextDate, editTextTime, editTextMeetingLink, editTextSkill;
    private LinearLayout topicContainer;
    private Button btnSchedule;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userEmail;
    private int topicCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_class);

        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextMeetingLink = findViewById(R.id.editTextMeetingLink);
        editTextSkill = findViewById(R.id.editTextSkill);
        btnSchedule = findViewById(R.id.btnSchedule);
        topicContainer = findViewById(R.id.topicContainer);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("ScheduledClasses");

        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());
        btnSchedule.setOnClickListener(v -> scheduleClass());

        addTopicField(); // Add the first topic field initially
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) ->
                editTextDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, selectedHour, selectedMinute) ->
                editTextTime.setText(selectedHour + ":" + selectedMinute),
                hour, minute, true).show();
    }

    private void addTopicField() {
        EditText newTopic = new EditText(this);
        newTopic.setHint("Enter Topic " + (topicCount + 1));
        newTopic.setId(View.generateViewId());
        newTopic.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        topicContainer.addView(newTopic);
        topicCount++;

        newTopic.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                EditText current = (EditText) v;
                String text = current.getText().toString().trim();
                if (!text.isEmpty() && topicCount == topicContainer.getChildCount()) {
                    addTopicField(); // Add new field when last field is filled
                }
            }
        });
    }

    private void scheduleClass() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String meetingLink = editTextMeetingLink.getText().toString().trim();
        String skill = editTextSkill.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || meetingLink.isEmpty() || skill.isEmpty()) {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!meetingLink.startsWith("http://") && !meetingLink.startsWith("https://")) {
            meetingLink = "https://" + meetingLink;
        }

        String classId = databaseReference.push().getKey();
        Map<String, String> classDetails = new HashMap<>();
        classDetails.put("email", userEmail);
        classDetails.put("skill", skill);
        classDetails.put("date", date);
        classDetails.put("time", time);
        classDetails.put("meetingLink", meetingLink);

        StringBuilder topicsBuilder = new StringBuilder();
        for (int i = 0; i < topicContainer.getChildCount(); i++) {
            EditText topicField = (EditText) topicContainer.getChildAt(i);
            String topic = topicField.getText().toString().trim();
            if (!topic.isEmpty()) {
                classDetails.put("topic" + (i + 1), topic);
                topicsBuilder.append("- ").append(topic).append("\n");
            }
        }

        databaseReference.child(classId).setValue(classDetails);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink));
        startActivity(browserIntent);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("date", date);
        resultIntent.putExtra("time", time);
        resultIntent.putExtra("meetingLink", meetingLink);
        resultIntent.putExtra("skill", skill);
        resultIntent.putExtra("topics", topicsBuilder.toString()); //  added topics
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
