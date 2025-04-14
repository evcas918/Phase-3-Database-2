package com.example.universitydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationSuccess extends AppCompatActivity {

    private TextView tvStudentId;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvDepartment;
    private TextView tvStudentType;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);

        // Initialize UI elements
        tvStudentId = findViewById(R.id.tvStudentId);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvStudentType = findViewById(R.id.tvStudentType);
        btnLogin = findViewById(R.id.btnLogin);

        // Get data from intent
        Intent intent = getIntent();
        String studentId = intent.getStringExtra("student_id");
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String department = intent.getStringExtra("department");
        String studentType = intent.getStringExtra("student_type");

        // Set text views with data
        tvStudentId.setText("Student ID: " + studentId);
        tvName.setText("Name: " + name);
        tvEmail.setText("Email: " + email);
        tvDepartment.setText("Department: " + department);
        tvStudentType.setText("Student Type: " + studentType);

        // Set up login button to go to login screen
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to login activity
                Intent loginIntent = new Intent(RegistrationSuccess.this, LoginActivity.class);
                RegistrationSuccess.this.startActivity(loginIntent);
                finish();
            }
        });
    }
}