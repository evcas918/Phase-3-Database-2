package com.example.universitydb;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentRegistration extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Spinner spinnerDepartment;
    private Spinner spinnerStudentType;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // Initialize UI elements
        etStudentId = findViewById(R.id.etStudentId);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerStudentType = findViewById(R.id.spinnerStudentType);
        btnRegister = findViewById(R.id.btnRegister);

        // Set up department spinner
        String[] departments = {
                "Miner School of Computer & Information Sciences",
                "Manning School of Business",
                "Francis College of Engineering",
                "College of Fine Arts, Humanities and Social Sciences",
                "Zuckerberg College of Health Sciences"
        };
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Set up student type spinner
        String[] studentTypes = {"undergraduate", "master", "phd"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, studentTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudentType.setAdapter(typeAdapter);

        // Set register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String studentId = etStudentId.getText().toString();
                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String department = spinnerDepartment.getSelectedItem().toString();
                final String studentType = spinnerStudentType.getSelectedItem().toString();

                // Validate inputs
                if (studentId.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentRegistration.this);
                    builder.setMessage("Please fill in all fields")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    return;
                }

                // Create response listener for the server response
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Registration successful, go to success page
                                Intent intent = new Intent(StudentRegistration.this, RegistrationSuccess.class);
                                intent.putExtra("student_id", studentId);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("department", department);
                                intent.putExtra("student_type", studentType);
                                StudentRegistration.this.startActivity(intent);
                                finish();
                            } else {
                                // Registration failed
                                String message = jsonResponse.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(StudentRegistration.this);
                                builder.setMessage("Registration Failed: " + message)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // Create and add request to queue
                RegistrationRequest registrationRequest = new RegistrationRequest(
                        studentId, name, email, password, department, studentType,
                        getString(R.string.api_base_url) + "student_registration.php",
                        responseListener
                );
                RequestQueue queue = Volley.newRequestQueue(StudentRegistration.this);
                queue.add(registrationRequest);
            }
        });
    }
}