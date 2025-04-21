package com.example.universitydb;

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

public class CourseHistoryActivity extends AppCompatActivity {

    private RecyclerView currentCoursesRecyclerView;
    private RecyclerView pastCoursesRecyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyCurrent;
    private TextView tvEmptyPast;
    private TextView tvGpa;
    private TextView tvTotalCredits;
    
    private List<HistoryCourse> currentCourses;
    private List<HistoryCourse> pastCourses;
    private CourseHistoryAdapter currentCoursesAdapter;
    private CourseHistoryAdapter pastCoursesAdapter;
    
    private RequestQueue requestQueue;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_history);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Course History");
        }

        // Initialize UI components
        currentCoursesRecyclerView = findViewById(R.id.recyclerViewCurrentCourses);
        pastCoursesRecyclerView = findViewById(R.id.recyclerViewPastCourses);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyCurrent = findViewById(R.id.tvEmptyCurrentCourses);
        tvEmptyPast = findViewById(R.id.tvEmptyPastCourses);
        tvGpa = findViewById(R.id.tvGpa);
        tvTotalCredits = findViewById(R.id.tvTotalCredits);

        // Set up RecyclerViews
        currentCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pastCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        currentCourses = new ArrayList<>();
        pastCourses = new ArrayList<>();
        
        currentCoursesAdapter = new CourseHistoryAdapter(this, currentCourses);
        pastCoursesAdapter = new CourseHistoryAdapter(this, pastCourses);
        
        currentCoursesRecyclerView.setAdapter(currentCoursesAdapter);
        pastCoursesRecyclerView.setAdapter(pastCoursesAdapter);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get student ID from intent
        studentId = getIntent().getStringExtra("student_id");

        // Load course history
        loadCourseHistory();
    }

    private void loadCourseHistory() {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.api_base_url) + "get_course_history.php";

        // Create parameters
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        
                        try {
                            boolean success = response.getBoolean("success");
                            
                            if (success) {
                                // Process current courses
                                if (response.has("current_courses")) {
                                    JSONArray currentArray = response.getJSONArray("current_courses");
                                    currentCourses.clear();
                                    
                                    for (int i = 0; i < currentArray.length(); i++) {
                                        JSONObject course = currentArray.getJSONObject(i);
                                        HistoryCourse historyCourse = new HistoryCourse(
                                                course.getString("course_id"),
                                                course.getString("course_name"),
                                                course.getString("section_id"),
                                                course.getString("semester"),
                                                course.getInt("year"),
                                                course.getInt("credits"),
                                                course.optString("instructor_name", "Not Assigned"),
                                                course.isNull("grade") ? null : course.getString("grade")
                                        );
                                        currentCourses.add(historyCourse);
                                    }
                                    
                                    currentCoursesAdapter.notifyDataSetChanged();
                                    
                                    if (currentCourses.isEmpty()) {
                                        tvEmptyCurrent.setVisibility(View.VISIBLE);
                                    } else {
                                        tvEmptyCurrent.setVisibility(View.GONE);
                                    }
                                }
                                
                                // Process past courses
                                if (response.has("past_courses")) {
                                    JSONArray pastArray = response.getJSONArray("past_courses");
                                    pastCourses.clear();
                                    
                                    double totalPoints = 0;
                                    int totalCredits = 0;
                                    int totalGradedCredits = 0;
                                    
                                    for (int i = 0; i < pastArray.length(); i++) {
                                        JSONObject course = pastArray.getJSONObject(i);
                                        HistoryCourse historyCourse = new HistoryCourse(
                                                course.getString("course_id"),
                                                course.getString("course_name"),
                                                course.getString("section_id"),
                                                course.getString("semester"),
                                                course.getInt("year"),
                                                course.getInt("credits"),
                                                course.optString("instructor_name", "Not Assigned"),
                                                course.isNull("grade") ? null : course.getString("grade")
                                        );
                                        pastCourses.add(historyCourse);
                                        
                                        // Calculate GPA if grade is available
                                        if (!course.isNull("grade")) {
                                            String grade = course.getString("grade");
                                            int credits = course.getInt("credits");
                                            double gradePoints = getGradePoints(grade);
                                            
                                            if (gradePoints >= 0) {
                                                totalPoints += (gradePoints * credits);
                                                totalGradedCredits += credits;
                                            }
                                            
                                            // Add to total credits if passing grade
                                            if (gradePoints >= 1.0) {
                                                totalCredits += credits;
                                            }
                                        }
                                    }
                                    
                                    // Update GPA and credits text
                                    if (totalGradedCredits > 0) {
                                        double gpa = totalPoints / totalGradedCredits;
                                        tvGpa.setText(String.format("GPA: %.2f", gpa));
                                        tvGpa.setVisibility(View.VISIBLE);
                                    } else {
                                        tvGpa.setVisibility(View.GONE);
                                    }
                                    
                                    tvTotalCredits.setText("Total Credits Earned: " + totalCredits);
                                    tvTotalCredits.setVisibility(View.VISIBLE);
                                    
                                    pastCoursesAdapter.notifyDataSetChanged();
                                    
                                    if (pastCourses.isEmpty()) {
                                        tvEmptyPast.setVisibility(View.VISIBLE);
                                    } else {
                                        tvEmptyPast.setVisibility(View.GONE);
                                    }
                                }
                                
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(CourseHistoryActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseHistoryActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CourseHistoryActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add request to queue
        requestQueue.add(jsonRequest);
    }
    
    private double getGradePoints(String grade) {
        switch (grade) {
            case "A+": return 4.0;
            case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            case "D-": return 0.7;
            case "F": return 0.0;
            default: return -1.0; // Invalid grade
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