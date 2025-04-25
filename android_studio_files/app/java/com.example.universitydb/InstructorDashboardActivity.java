package com.example.universitydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

public class InstructorDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvInstructorId;
    private TextView tvDepartment;
    private TextView tvTitle;
    private Button btnViewCurrentSections;
    private Button btnViewPastSections;
    private ProgressBar progressBar;
    
    private String instructorId;
    private String instructorName;
    private String email;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_dashboard);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Instructor Dashboard");

        // Initialize UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        tvInstructorId = findViewById(R.id.tvInstructorId);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvTitle = findViewById(R.id.tvTitle);
        btnViewCurrentSections = findViewById(R.id.btnViewCurrentSections);
        btnViewPastSections = findViewById(R.id.btnViewPastSections);
        progressBar = findViewById(R.id.progressBar);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get instructor info from intent
        Intent intent = getIntent();
        instructorId = intent.getStringExtra("instructor_id");
        instructorName = intent.getStringExtra("instructor_name");
        email = intent.getStringExtra("email");

        // Set initial instructor info
        tvWelcome.setText("Welcome, " + instructorName);
        tvInstructorId.setText("Instructor ID: " + instructorId);

        // Fetch additional instructor details
        fetchInstructorDetails();

        // Set up button click listeners
        btnViewCurrentSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sectionsIntent = new Intent(InstructorDashboardActivity.this, 
                        SectionListActivity.class);
                sectionsIntent.putExtra("instructor_id", instructorId);
                sectionsIntent.putExtra("current_only", true);
                startActivity(sectionsIntent);
            }
        });

        btnViewPastSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sectionsIntent = new Intent(InstructorDashboardActivity.this, 
                        SectionListActivity.class);
                sectionsIntent.putExtra("instructor_id", instructorId);
                sectionsIntent.putExtra("current_only", false);
                startActivity(sectionsIntent);
            }
        });
    }

    private void fetchInstructorDetails() {
        progressBar.setVisibility(View.VISIBLE);
        
        String url = getString(R.string.api_base_url) + "get_instructor_info.php";

        // Create parameters
        JSONObject params = new JSONObject();
        try {
            params.put("instructor_id", instructorId);
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
                                String deptName = response.getString("dept_name");
                                String title = response.getString("title");
                                
                                // Update UI with instructor details
                                tvDepartment.setText("Department: " + deptName);
                                tvTitle.setText("Title: " + title);
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(InstructorDashboardActivity.this,
                                        "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InstructorDashboardActivity.this,
                                    "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(InstructorDashboardActivity.this,
                                "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add request to queue
        requestQueue.add(jsonRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Log out and go back to login screen
            Intent intent = new Intent(InstructorDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}