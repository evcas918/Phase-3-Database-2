package com.example.universitydb;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class InstructorLoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_login);

        // Initialize UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Set login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInstructor();
            }
        });
    }

    private void loginInstructor() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showErrorDialog("Please enter both email and password");
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Create request parameters
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
            params.put("type", "instructor");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request
        String url = getString(R.string.api_base_url) + "instructor_login.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        try {
                            boolean success = response.getBoolean("success");
                            
                            if (success) {
                                String instructorId = response.getString("instructor_id");
                                String instructorName = response.getString("instructor_name");
                                
                                // Start instructor dashboard
                                Intent intent = new Intent(InstructorLoginActivity.this, 
                                        InstructorDashboardActivity.class);
                                intent.putExtra("instructor_id", instructorId);
                                intent.putExtra("instructor_name", instructorName);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = response.getString("message");
                                showErrorDialog("Login failed: " + message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showErrorDialog("Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        showErrorDialog("Network error: " + error.getMessage());
                    }
                }
        );

        // Add request to queue
        requestQueue.add(request);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}