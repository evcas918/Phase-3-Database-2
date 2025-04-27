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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseEventsActivity extends AppCompatActivity implements CourseEventAdapter.OnEventActionListener {

    private RecyclerView recyclerView;
    private CourseEventAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyEvents;
    
    private List<CourseEvent> eventList;
    private String studentId;
    private RequestQueue requestQueue;
    private SimpleDateFormat apiDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_events);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Course Events");
        }
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewEvents);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyEvents = findViewById(R.id.tvEmptyEvents);
        
        // Initialize data
        studentId = getIntent().getStringExtra("student_id");
        eventList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Set up RecyclerView
        adapter = new CourseEventAdapter(this, eventList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Load course events
        loadCourseEvents();
    }
    
    private void loadCourseEvents() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyEvents.setVisibility(View.GONE);
        
        String url = getString(R.string.api_base_url) + "get_course_events.php";
        
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        
                        try {
                            boolean success = response.getBoolean("success");
                            
                            if (success) {
                                eventList.clear();
                                
                                if (response.has("events")) {
                                    JSONArray eventsArray = response.getJSONArray("events");
                                    
                                    for (int i = 0; i < eventsArray.length(); i++) {
                                        JSONObject eventObj = eventsArray.getJSONObject(i);
                                        
                                        CourseEvent event = new CourseEvent();
                                        event.setEventId(eventObj.getInt("event_id"));
                                        event.setCourseId(eventObj.getString("course_id"));
                                        event.setCourseName(eventObj.getString("course_name"));
                                        event.setSectionId(eventObj.getString("section_id"));
                                        event.setSemester(eventObj.getString("semester"));
                                        event.setYear(eventObj.getInt("year"));
                                        event.setEventTitle(eventObj.getString("event_title"));
                                        event.setEventDescription(eventObj.optString("event_description", ""));
                                        event.setEventDate(apiDateFormat.parse(eventObj.getString("event_date")));
                                        event.setEventType(eventObj.optString("event_type", ""));
                                        event.setInTodoList(eventObj.getInt("in_todo_list") == 1);
                                        
                                        eventList.add(event);
                                    }
                                }
                                
                                adapter.notifyDataSetChanged();
                                
                                if (eventList.isEmpty()) {
                                    recyclerView.setVisibility(View.GONE);
                                    tvEmptyEvents.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    tvEmptyEvents.setVisibility(View.GONE);
                                }
                                
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(CourseEventsActivity.this, message, Toast.LENGTH_LONG).show();
                                
                                recyclerView.setVisibility(View.GONE);
                                tvEmptyEvents.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseEventsActivity.this, 
                                    "Error parsing response", Toast.LENGTH_LONG).show();
                            
                            recyclerView.setVisibility(View.GONE);
                            tvEmptyEvents.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        tvEmptyEvents.setVisibility(View.VISIBLE);
                        
                        Toast.makeText(CourseEventsActivity.this, 
                                "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        
        requestQueue.add(request);
    }
    
    @Override
    public void onAddToTodoList(int position) {
        CourseEvent event = eventList.get(position);
        addEventToTodoList(event);
    }
    
    private void addEventToTodoList(CourseEvent event) {
        String url = getString(R.string.api_base_url) + "add_course_event_todo.php";
        
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", studentId);
            params.put("event_id", event.getEventId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            
                            if (success) {
                                // Update the event in the list
                                event.setInTodoList(true);
                                adapter.notifyDataSetChanged();
                                
                                Toast.makeText(CourseEventsActivity.this, 
                                        "Added to your to-do list", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(CourseEventsActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseEventsActivity.this, 
                                    "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CourseEventsActivity.this, 
                                "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        
        requestQueue.add(request);
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