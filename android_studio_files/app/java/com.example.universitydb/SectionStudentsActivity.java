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

public class SectionStudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SectionStudentAdapter studentAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptyStudents;
    private TextView tvSectionHeader;
    private List<SectionStudent> studentList;
    private RequestQueue requestQueue;
    
    private String sectionIdentifier;
    private String courseName;
    private String sectionId;
    private String semester;
    private int year;
    private boolean isCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_students);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Section Students");
        }

        // Get data from intent
        sectionIdentifier = getIntent().getStringExtra("section_identifier");
        courseName = getIntent().getStringExtra("course_name");
        sectionId = getIntent().getStringExtra("section_id");
        semester = getIntent().getStringExtra("semester");
        year = getIntent().getIntExtra("year", 0);
        isCurrent = getIntent().getBooleanExtra("is_current", false);

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewStudents);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyStudents = findViewById(R.id.tvEmptyStudents);
        tvSectionHeader = findViewById(R.id.tvSectionHeader);

        // Set section header
        String headerText = courseName + " (Section " + sectionId + ")\n" + semester + " " + year;
        tvSectionHeader.setText(headerText);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        studentAdapter = new SectionStudentAdapter(this, studentList, isCurrent);
        recyclerView.setAdapter(studentAdapter);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Load students
        loadSectionStudents();
    }

    private void loadSectionStudents() {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.api_base_url) + "get_section_students.php";

        // Create parameters
        JSONObject params = new JSONObject();
        try {
            params.put("section_identifier", sectionIdentifier);
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
                                JSONArray studentsArray = response.getJSONArray("students");
                                studentList.clear();
                                
                                for (int i = 0; i < studentsArray.length(); i++) {
                                    JSONObject studentObject = studentsArray.getJSONObject(i);
                                    
                                    SectionStudent student = new SectionStudent(
                                            studentObject.getString("student_id"),
                                            studentObject.getString("name"),
                                            studentObject.getString("email"),
                                            studentObject.getString("student_type"),
                                            studentObject.isNull("grade") ? null : studentObject.getString("grade")
                                    );
                                    
                                    studentList.add(student);
                                }
                                
                                studentAdapter.notifyDataSetChanged();
                                
                                if (studentList.isEmpty()) {
                                    tvEmptyStudents.setText("No students enrolled in this section.");
                                    tvEmptyStudents.setVisibility(View.VISIBLE);
                                } else {
                                    tvEmptyStudents.setVisibility(View.GONE);
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(SectionStudentsActivity.this, message, Toast.LENGTH_LONG).show();
                                tvEmptyStudents.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SectionStudentsActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                            tvEmptyStudents.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SectionStudentsActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        tvEmptyStudents.setVisibility(View.VISIBLE);
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