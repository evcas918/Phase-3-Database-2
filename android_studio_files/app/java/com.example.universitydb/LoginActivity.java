package com.example.universitydb;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Set login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                // Validate inputs
                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please enter both email and password")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    return;
                }

                // Create response listener for login request
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                String accountType = jsonResponse.getString("account_type");
                                String studentId = "";

                                if (accountType.equals("student")) {
                                    studentId = jsonResponse.getString("student_id");

                                    // Go to student dashboard
                                    Intent intent = new Intent(LoginActivity.this, StudentDashboard.class);
                                    intent.putExtra("student_id", studentId);
                                    intent.putExtra("email", email);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                } else if (accountType.equals("instructor")) {
                                    String instructorId = jsonResponse.getString("instructor_id");
                                    // Check if instructor_name exists in the response
                                    String instructorName = "Instructor"; // Default value
                                    if (jsonResponse.has("instructor_name")) {
                                        instructorName = jsonResponse.getString("instructor_name");
                                    }
                                    
                                    // Go to instructor dashboard
                                    Intent intent = new Intent(LoginActivity.this, InstructorDashboardActivity.class);
                                    intent.putExtra("instructor_id", instructorId);
                                    intent.putExtra("instructor_name", instructorName);
                                    intent.putExtra("email", email);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                }
                            } else {
                                // Login failed
                                String message = jsonResponse.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed: " + message)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // Create and add login request to queue
                LoginRequest loginRequest = new LoginRequest(
                        email, password, getString(R.string.api_base_url) + "login.php", responseListener
                );
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

        // Set register text click listener
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to registration activity
                Intent intent = new Intent(LoginActivity.this, StudentRegistration.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }
}