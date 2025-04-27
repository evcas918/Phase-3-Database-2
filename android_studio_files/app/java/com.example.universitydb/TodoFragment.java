package com.example.universitydb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment {
    
    private static final String ARG_TODO_TYPE = "todo_type";
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COURSE = 1;
    public static final int TYPE_PERSONAL = 2;
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyTodos;
    
    private List<TodoItem> todoList = new ArrayList<>();
    private TodoAdapter adapter;
    private OnTodoActionListener listener;
    
    public static TodoFragment newInstance(int todoType) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TODO_TYPE, todoType);
        fragment.setArguments(args);
        return fragment;
    }
    
    public void setOnTodoActionListener(OnTodoActionListener listener) {
        this.listener = listener;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerViewTodos);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyTodos = view.findViewById(R.id.tvEmptyTodos);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        adapter = new TodoAdapter(requireContext(), todoList, new TodoAdapter.OnTodoActionListener() {
            @Override
            public void onTodoStatusChanged(int position, boolean isCompleted) {
                if (listener != null) {
                    listener.onTodoStatusChanged(todoList.get(position), isCompleted);
                }
            }
            
            @Override
            public void onTodoDelete(int position) {
                if (listener != null) {
                    listener.onTodoDelete(todoList.get(position));
                }
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
    
    public void updateTodoList(List<TodoItem> newList) {
        if (todoList == null) {
            todoList = new ArrayList<>();
        }
        todoList.clear();
        
        // Filter based on type
        int todoType = getArguments() != null ? getArguments().getInt(ARG_TODO_TYPE, TYPE_ALL) : TYPE_ALL;
        
        for (TodoItem todo : newList) {
            if (todoType == TYPE_ALL || 
                (todoType == TYPE_COURSE && todo.isCourseEvent()) ||
                (todoType == TYPE_PERSONAL && !todo.isCourseEvent())) {
                todoList.add(todo);
            }
        }
        
        // Add null check for adapter
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        
        // Show/hide empty view (with null checks)
        if (recyclerView != null && tvEmptyTodos != null) {
            if (todoList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyTodos.setVisibility(View.VISIBLE);
                
                // Set appropriate message
                switch (todoType) {
                    case TYPE_COURSE:
                        tvEmptyTodos.setText("No course to-do items found");
                        break;
                    case TYPE_PERSONAL:
                        tvEmptyTodos.setText("No personal to-do items found");
                        break;
                    default:
                        tvEmptyTodos.setText("No to-do items found");
                        break;
                }
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyTodos.setVisibility(View.GONE);
            }
        }
    }
    
    public void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    public interface OnTodoActionListener {
        void onTodoStatusChanged(TodoItem todo, boolean isCompleted);
        void onTodoDelete(TodoItem todo);
    }
}