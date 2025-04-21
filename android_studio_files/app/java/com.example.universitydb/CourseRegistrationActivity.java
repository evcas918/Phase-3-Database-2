package com.example.universitydb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseRegistrationActivity extends AppCompatActivity {

    private TextView tvCourseId, tvCourseName, tvSection, tvCredits, tvInstructor, 
                     tvSchedule, tvLocation, tvAvailability;
    private Button btnRegister;
    private ProgressBar progressBar;
    
    private String studentId, courseId, sectionId;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Course Registration");
        }

        // Initialize UI components
        tvCourseId = findViewById(R.id.tvCourseId);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvSection = findViewById(R.id.tvSection);
        tvCredits = findViewById(R.id.tvCredits);
        tvInstructor = findViewById(R.id.tvInstructor);
        tvSchedule = findViewById(R.id.tvSchedule);
        tvLocation = findViewById(R.id.tvLocation);
        tvAvailability = findViewById(R.id.tvAvailability);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get data from intent
        studentId = getIntent().getStringExtra("student_id");
        courseId = getIntent().getStringExtra("course_id");
        String courseName = getIntent().getStringExtra("course_name");
        sectionId = getIntent().getStringExtra("section_id");
        String instructor = getIntent().getStringExtra("instructor");
        String schedule = getIntent().getStringExtra("schedule");
        String location = getIntent().getStringExtra("location");
        int credits = getIntent().getIntExtra("credits", 0);
        int availableSeats = getIntent().getIntExtra("available_seats", 0);

        // Set text views with data
        tvCourseId.setText(courseId);
        tvCourseName.setText(courseName);
        tvSection.setText("Section: " + sectionId);
        tvCredits.setText(credits + " credits");
        tvInstructor.setText("Instructor: " + instructor);
        tvSchedule.setText("Schedule: " + schedule);
        tvLocation.setText("Location: " + location);
        tvAvailability.setText("Available Seats: " + availableSeats);

        // Set register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Registration")
                .setMessage("Are you sure you want to register for " + courseId + " (" + tvCourseName.getText() + ")?")
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registerForCourse();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void registerForCourse() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        
        String url = getString(R.string.api_base_url) + "register_for_course.php";

        // Create parameters
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", studentId);
            params.put("course_id", courseId);
            params.put("section_id", sectionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            
                            if (success) {
                                // Show success dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(CourseRegistrationActivity.this);
                                builder.setTitle("Registration Successful")
                                        .setMessage(message)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish(); // Close this activity and go back to course list
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            } else {
                                // Show error
                                Toast.makeText(CourseRegistrationActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseRegistrationActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(CourseRegistrationActivity.this, 
                                "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add request to queue
        requestQueue.add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}