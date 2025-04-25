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

public class SectionListActivity extends AppCompatActivity implements SectionAdapter.OnSectionClickListener {

    private RecyclerView recyclerView;
    private SectionAdapter sectionAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptySections;
    private List<Section> sectionList;
    private RequestQueue requestQueue;
    private String instructorId;
    private boolean currentOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get data from intent
        instructorId = getIntent().getStringExtra("instructor_id");
        currentOnly = getIntent().getBooleanExtra("current_only", false);
        
        // Set title based on what we're showing
        if (currentOnly) {
            getSupportActionBar().setTitle("Current Sections");
        } else {
            getSupportActionBar().setTitle("Teaching History");
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewSections);
        progressBar = findViewById(R.id.progressBar);
        tvEmptySections = findViewById(R.id.tvEmptySections);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sectionList = new ArrayList<>();
        sectionAdapter = new SectionAdapter(this, sectionList, this);
        recyclerView.setAdapter(sectionAdapter);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Load sections
        loadSections();
    }

    private void loadSections() {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.api_base_url) + "get_instructor_sections.php";

        // Create parameters
        JSONObject params = new JSONObject();
        try {
            params.put("instructor_id", instructorId);
            params.put("current_only", currentOnly);
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
                                JSONArray sectionsArray = response.getJSONArray("sections");
                                sectionList.clear();
                                
                                for (int i = 0; i < sectionsArray.length(); i++) {
                                    JSONObject sectionObject = sectionsArray.getJSONObject(i);
                                    
                                    // Create schedule string
                                    String schedule = sectionObject.getString("day") + " " +
                                            sectionObject.getString("start_time") + "-" +
                                            sectionObject.getString("end_time");
                                    
                                    // Create location string
                                    String location = sectionObject.getString("building") + " " +
                                            sectionObject.getString("room_number");
                                    
                                    // Create Section object
                                    Section section = new Section(
                                            sectionObject.getString("course_id"),
                                            sectionObject.getString("course_name"),
                                            sectionObject.getString("section_id"),
                                            sectionObject.getString("semester"),
                                            sectionObject.getInt("year"),
                                            schedule,
                                            location,
                                            sectionObject.getInt("enrolled_students"),
                                            sectionObject.getBoolean("is_current")
                                    );
                                    
                                    sectionList.add(section);
                                }
                                
                                sectionAdapter.notifyDataSetChanged();
                                
                                if (sectionList.isEmpty()) {
                                    if (currentOnly) {
                                        tvEmptySections.setText("You are not teaching any sections this semester.");
                                    } else {
                                        tvEmptySections.setText("No teaching history found.");
                                    }
                                    tvEmptySections.setVisibility(View.VISIBLE);
                                } else {
                                    tvEmptySections.setVisibility(View.GONE);
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(SectionListActivity.this, message, Toast.LENGTH_LONG).show();
                                tvEmptySections.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SectionListActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                            tvEmptySections.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SectionListActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        tvEmptySections.setVisibility(View.VISIBLE);
                    }
                });

        // Add request to queue
        requestQueue.add(jsonRequest);
    }

    @Override
    public void onSectionClick(int position) {
        Section selectedSection = sectionList.get(position);
        
        // Open section students view
        Intent intent = new Intent(SectionListActivity.this, SectionStudentsActivity.class);
        intent.putExtra("section_identifier", selectedSection.getSectionIdentifier());
        intent.putExtra("course_name", selectedSection.getCourseName());
        intent.putExtra("section_id", selectedSection.getSectionId());
        intent.putExtra("semester", selectedSection.getSemester());
        intent.putExtra("year", selectedSection.getYear());
        intent.putExtra("is_current", selectedSection.isCurrent());
        startActivity(intent);
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