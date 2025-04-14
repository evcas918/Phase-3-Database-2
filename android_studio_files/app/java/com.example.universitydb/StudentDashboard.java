package com.example.universitydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentDashboard extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvStudentId;
    private TextView tvDepartment;
    private TextView tvCredits;
    private Button btnViewCourses;
    private Button btnRegisterCourses;
    private Button btnViewHistory;

    private String studentId;
    private String email;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvCredits = findViewById(R.id.tvCredits);
        btnViewCourses = findViewById(R.id.btnViewCourses);
        btnRegisterCourses = findViewById(R.id.btnRegisterCourses);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get student info from intent
        Intent intent = getIntent();
        studentId = intent.getStringExtra("student_id");
        email = intent.getStringExtra("email");

        // Set initial student ID
        tvStudentId.setText("Student ID: " + studentId);

        // Fetch student details
        fetchStudentDetails();

        // Set up button click listeners
        btnViewCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a toast
                Toast.makeText(StudentDashboard.this, "View Courses feature coming soon", Toast.LENGTH_SHORT).show();

                // TODO: Implement this in a future iteration
                // Intent intent = new Intent(StudentDashboard.this, CurrentCourses.class);
                // intent.putExtra("student_id", studentId);
                // startActivity(intent);
            }
        });

        btnRegisterCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a toast
                Toast.makeText(StudentDashboard.this, "Register for Courses feature coming soon", Toast.LENGTH_SHORT).show();

                // TODO: Implement this in a future iteration
                // Intent intent = new Intent(StudentDashboard.this, CourseRegistration.class);
                // intent.putExtra("student_id", studentId);
                // startActivity(intent);
            }
        });

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a toast
                Toast.makeText(StudentDashboard.this, "Course History feature coming soon", Toast.LENGTH_SHORT).show();

                // TODO: Implement this in a future iteration
                // Intent intent = new Intent(StudentDashboard.this, CourseHistory.class);
                // intent.putExtra("student_id", studentId);
                // startActivity(intent);
            }
        });
    }

    private void fetchStudentDetails() {
        String url = getString(R.string.api_base_url) + "get_student_info.php";

        // Create JSON request parameters
        Map<String, String> params = new HashMap<>();
        params.put("student_id", studentId);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");

                            if (success) {
                                String name = response.getString("name");
                                String deptName = response.getString("dept_name");
                                String studentType = response.getString("student_type");

                                // Set welcome message
                                tvWelcome.setText("Welcome, " + name + "!");

                                // Set department info
                                tvDepartment.setText("Department: " + deptName);

                                // Set credits based on student type
                                if (studentType.equals("undergraduate")) {
                                    int credits = response.getInt("credits");
                                    String classStanding = response.getString("class_standing");
                                    tvCredits.setText("Credits: " + credits + " (" + classStanding + ")");
                                } else if (studentType.equals("master")) {
                                    int credits = response.getInt("credits");
                                    tvCredits.setText("Credits: " + credits);
                                } else {
                                    tvCredits.setText("PhD Student");
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(StudentDashboard.this,
                                        "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(StudentDashboard.this,
                                    "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StudentDashboard.this,
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
            Intent intent = new Intent(StudentDashboard.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}