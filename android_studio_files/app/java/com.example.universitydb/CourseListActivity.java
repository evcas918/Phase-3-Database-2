package com.example.universitydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptyCourses;
    private List<Course> courseList;
    private RequestQueue requestQueue;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Available Courses");
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewCourses);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyCourses = findViewById(R.id.tvEmptyCourses);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList, this);
        recyclerView.setAdapter(courseAdapter);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get student ID from intent
        studentId = getIntent().getStringExtra("student_id");

        // Load available courses
        loadAvailableCourses();
    }

    private void loadAvailableCourses() {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.api_base_url) + "get_available_courses.php";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        
                        try {
                            boolean success = response.getBoolean("success");
                            
                            if (success) {
                                JSONArray coursesArray = response.getJSONArray("courses");
                                courseList.clear();
                                
                                for (int i = 0; i < coursesArray.length(); i++) {
                                    JSONObject courseObject = coursesArray.getJSONObject(i);
                                    
                                    Course course = new Course(
                                            courseObject.getString("course_id"),
                                            courseObject.getString("course_name"),
                                            courseObject.getString("section_id"),
                                            courseObject.getInt("credits"),
                                            courseObject.getString("instructor_name"),
                                            courseObject.getString("day"),
                                            courseObject.getString("start_time"),
                                            courseObject.getString("end_time"),
                                            courseObject.getString("building") + " " + courseObject.getString("room_number"),
                                            courseObject.getInt("enrolled_students"),
                                            courseObject.getInt("available_seats"),
                                            courseObject.getBoolean("is_full")
                                    );
                                    
                                    courseList.add(course);
                                }
                                
                                courseAdapter.notifyDataSetChanged();
                                
                                if (courseList.isEmpty()) {
                                    tvEmptyCourses.setVisibility(View.VISIBLE);
                                } else {
                                    tvEmptyCourses.setVisibility(View.GONE);
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(CourseListActivity.this, message, Toast.LENGTH_LONG).show();
                                tvEmptyCourses.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseListActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                            tvEmptyCourses.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CourseListActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        tvEmptyCourses.setVisibility(View.VISIBLE);
                    }
                });

        // Add request to queue
        requestQueue.add(jsonRequest);
    }

    @Override
    public void onCourseClick(int position) {
        Course selectedCourse = courseList.get(position);
        
        if (selectedCourse.isFull()) {
            Toast.makeText(this, "This course section is full!", Toast.LENGTH_SHORT).show();
        } else {
            // Open course registration confirmation
            Intent intent = new Intent(CourseListActivity.this, CourseRegistrationActivity.class);
            intent.putExtra("student_id", studentId);
            intent.putExtra("course_id", selectedCourse.getCourseId());
            intent.putExtra("course_name", selectedCourse.getCourseName());
            intent.putExtra("section_id", selectedCourse.getSectionId());
            intent.putExtra("instructor", selectedCourse.getInstructor());
            intent.putExtra("schedule", selectedCourse.getDay() + " " + selectedCourse.getStartTime() + "-" + selectedCourse.getEndTime());
            intent.putExtra("location", selectedCourse.getLocation());
            intent.putExtra("credits", selectedCourse.getCredits());
            intent.putExtra("available_seats", selectedCourse.getAvailableSeats());
            startActivity(intent);
        }
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