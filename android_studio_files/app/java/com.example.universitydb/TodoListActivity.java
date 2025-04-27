package com.example.universitydb;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoListActivity extends AppCompatActivity implements TodoFragment.OnTodoActionListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAddTodo;
    
    private TodoFragment allTodosFragment;
    private TodoFragment courseTodosFragment;
    private TodoFragment personalTodosFragment;
    
    private String studentId;
    private RequestQueue requestQueue;
    private List<TodoItem> todoList;
    
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat apiDateFormat;
    private Button btnCourseEvents;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("To-Do List");
        }
        
        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fabAddTodo = findViewById(R.id.fabAddTodo);
        btnCourseEvents = findViewById(R.id.btnCourseEvents);

        // Set click listener
        btnCourseEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodoListActivity.this, CourseEventsActivity.class);
                intent.putExtra("student_id", studentId);
                startActivity(intent);
            }
        });
        
        // Initialize data
        studentId = getIntent().getStringExtra("student_id");
        requestQueue = Volley.newRequestQueue(this);
        todoList = new ArrayList<>();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Initialize fragments
        allTodosFragment = TodoFragment.newInstance(TodoFragment.TYPE_ALL);
        courseTodosFragment = TodoFragment.newInstance(TodoFragment.TYPE_COURSE);
        personalTodosFragment = TodoFragment.newInstance(TodoFragment.TYPE_PERSONAL);
        
        allTodosFragment.setOnTodoActionListener(this);
        courseTodosFragment.setOnTodoActionListener(this);
        personalTodosFragment.setOnTodoActionListener(this);
        
        // Set up ViewPager
        viewPager.setAdapter(new TodoPagerAdapter(this));

        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All");
                    break;
                case 1:
                    tab.setText("Course");
                    break;
                case 2:
                    tab.setText("Personal");
                    break;
            }
        }).attach();

        // Set up FAB click listener for adding new todo
        fabAddTodo.setOnClickListener(view -> showAddTodoDialog());

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadTodoItems();
                    }
                }, 100); // 100ms delay
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload to-do items when returning from CourseEventsActivity
        loadTodoItems();
    }

        private class TodoPagerAdapter extends FragmentStateAdapter {
            public TodoPagerAdapter(FragmentActivity fragmentActivity) {
                super(fragmentActivity);
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return allTodosFragment;
                    case 1:
                        return courseTodosFragment;
                    case 2:
                        return personalTodosFragment;
                    default:
                        return allTodosFragment;
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        }

        private void loadTodoItems() {
            allTodosFragment.showLoading(true);
            courseTodosFragment.showLoading(true);
            personalTodosFragment.showLoading(true);
            
            String url = getString(R.string.api_base_url) + "get_todo_items.php";
            
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
                            allTodosFragment.showLoading(false);
                            courseTodosFragment.showLoading(false);
                            personalTodosFragment.showLoading(false);
                            
                            try {
                                boolean success = response.getBoolean("success");
                                
                                if (success) {
                                    todoList.clear();
                                    
                                    // Parse course todos
                                    if (response.has("course_todos")) {
                                        JSONArray courseTodos = response.getJSONArray("course_todos");
                                        
                                        for (int i = 0; i < courseTodos.length(); i++) {
                                            JSONObject todoObj = courseTodos.getJSONObject(i);
                                            
                                            int todoId = todoObj.getInt("todo_id");
                                            int eventId = todoObj.getInt("event_id");
                                            String title = todoObj.getString("todo_title");
                                            String description = todoObj.optString("todo_description", "");
                                            Date dueDate = apiDateFormat.parse(todoObj.getString("due_date"));
                                            boolean isCompleted = todoObj.getInt("is_completed") == 1;
                                            Date dateCreated = todoObj.has("date_created") ? 
                                                    apiDateFormat.parse(todoObj.getString("date_created")) : new Date();
                                            
                                            // Course related info
                                            String courseId = todoObj.getString("course_id");
                                            String courseName = todoObj.getString("course_name");
                                            String eventType = todoObj.optString("event_type", null);
                                            
                                            TodoItem todo = new TodoItem(
                                                    todoId, studentId, eventId, title, description, dueDate, 
                                                    isCompleted, dateCreated, courseId, courseName, eventType
                                            );
                                            
                                            todoList.add(todo);
                                        }
                                    }
                                    
                                    // Parse personal todos
                                    if (response.has("personal_todos")) {
                                        JSONArray personalTodos = response.getJSONArray("personal_todos");
                                        
                                        for (int i = 0; i < personalTodos.length(); i++) {
                                            JSONObject todoObj = personalTodos.getJSONObject(i);
                                            
                                            int todoId = todoObj.getInt("todo_id");
                                            String title = todoObj.getString("todo_title");
                                            String description = todoObj.optString("todo_description", "");
                                            Date dueDate = apiDateFormat.parse(todoObj.getString("due_date"));
                                            boolean isCompleted = todoObj.getInt("is_completed") == 1;
                                            Date dateCreated = todoObj.has("date_created") ? 
                                                    apiDateFormat.parse(todoObj.getString("date_created")) : new Date();
                                            
                                            TodoItem todo = new TodoItem(
                                                    todoId, studentId, title, description, dueDate, 
                                                    isCompleted, dateCreated
                                            );
                                            
                                            todoList.add(todo);
                                        }
                                    }
                                    
                                    // Update fragments
                                    updateFragments();
                                    
                                } else {
                                    String message = response.getString("message");
                                    Toast.makeText(TodoListActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(TodoListActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            allTodosFragment.showLoading(false);
                            courseTodosFragment.showLoading(false);
                            personalTodosFragment.showLoading(false);
                            
                            Toast.makeText(TodoListActivity.this, 
                                    "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
            
            requestQueue.add(request);
        }

        private void updateFragments() {
            allTodosFragment.updateTodoList(todoList);
            courseTodosFragment.updateTodoList(todoList);
            personalTodosFragment.updateTodoList(todoList);
        }

        private void showAddTodoDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_todo, null);
            builder.setView(dialogView);
            
            EditText etTitle = dialogView.findViewById(R.id.etTitle);
            EditText etDescription = dialogView.findViewById(R.id.etDescription);
            Button btnPickDate = dialogView.findViewById(R.id.btnPickDate);
            TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
            
            final Calendar calendar = Calendar.getInstance();
            final Date[] selectedDate = {calendar.getTime()};
            tvSelectedDate.setText(dateFormat.format(selectedDate[0]));
            
            btnPickDate.setOnClickListener(v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TodoListActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            selectedDate[0] = calendar.getTime();
                            tvSelectedDate.setText(dateFormat.format(selectedDate[0]));
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            });
            
            builder.setPositiveButton("Add", (dialog, which) -> {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                
                if (title.isEmpty()) {
                    Toast.makeText(TodoListActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                addPersonalTodo(title, description, selectedDate[0]);
            });
            
            builder.setNegativeButton("Cancel", null);
            
            builder.create().show();
        }

        private void addPersonalTodo(String title, String description, Date dueDate) {
            String url = getString(R.string.api_base_url) + "add_todo_item.php";
            
            JSONObject params = new JSONObject();
            try {
                params.put("student_id", studentId);
                params.put("todo_title", title);
                params.put("todo_description", description);
                params.put("due_date", apiDateFormat.format(dueDate));
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
                                    Toast.makeText(TodoListActivity.this, 
                                            "To-do item added successfully", Toast.LENGTH_SHORT).show();
                                    loadTodoItems(); // Reload all items
                                } else {
                                    String message = response.getString("message");
                                    Toast.makeText(TodoListActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TodoListActivity.this, 
                                        "Error parsing response", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TodoListActivity.this, 
                                    "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
            
            requestQueue.add(request);
        }

        @Override
        public void onTodoStatusChanged(TodoItem todo, boolean isCompleted) {
            String url = getString(R.string.api_base_url) + "update_todo_status.php";
            
            JSONObject params = new JSONObject();
            try {
                params.put("todo_id", todo.getTodoId());
                params.put("is_completed", isCompleted ? 1 : 0);
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
                                    // Update local data
                                    for (TodoItem item : todoList) {
                                        if (item.getTodoId() == todo.getTodoId()) {
                                            item.setCompleted(isCompleted);
                                            break;
                                        }
                                    }
                                    updateFragments();
                                } else {
                                    String message = response.getString("message");
                                    Toast.makeText(TodoListActivity.this, message, Toast.LENGTH_LONG).show();
                                    loadTodoItems(); // Reload to reset state
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TodoListActivity.this, 
                                        "Error parsing response", Toast.LENGTH_LONG).show();
                                loadTodoItems(); // Reload to reset state
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TodoListActivity.this, 
                                    "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            loadTodoItems(); // Reload to reset state
                        }
                    }
            );
            
            requestQueue.add(request);
        }

        @Override
        public void onTodoDelete(TodoItem todo) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete To-Do Item")
                    .setMessage("Are you sure you want to delete this to-do item?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteTodoItem(todo);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void deleteTodoItem(TodoItem todo) {
            String url = getString(R.string.api_base_url) + "delete_todo_item.php";
            
            JSONObject params = new JSONObject();
            try {
                params.put("todo_id", todo.getTodoId());
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
                                    Toast.makeText(TodoListActivity.this, 
                                            "To-do item deleted successfully", Toast.LENGTH_SHORT).show();
                                    loadTodoItems(); // Reload all items
                                } else {
                                    String message = response.getString("message");
                                    Toast.makeText(TodoListActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TodoListActivity.this, 
                                        "Error parsing response", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TodoListActivity.this, 
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