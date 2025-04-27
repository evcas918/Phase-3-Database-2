package com.example.universitydb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private List<TodoItem> todoList;
    private OnTodoActionListener listener;
    private SimpleDateFormat dateFormat;

    public TodoAdapter(Context context, List<TodoItem> todoList, OnTodoActionListener listener) {
        this.context = context;
        this.todoList = todoList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todo = todoList.get(position);
        
        holder.tvTitle.setText(todo.getTodoTitle());
        
        if (todo.getTodoDescription() != null && !todo.getTodoDescription().isEmpty()) {
            holder.tvDescription.setText(todo.getTodoDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        
        holder.tvDueDate.setText("Due: " + dateFormat.format(todo.getDueDate()));
        
        // Set checkbox state without triggering listener
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(todo.isCompleted());
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onTodoStatusChanged(pos, isChecked);
            }
        });
        
        // Show course info if it's a course event
        if (todo.isCourseEvent()) {
            String courseInfo = todo.getCourseId() + ": " + todo.getCourseName();
            holder.tvCourseInfo.setText(courseInfo);
            
            if (todo.getEventType() != null) {
                String eventType = todo.getEventType().substring(0, 1).toUpperCase() + 
                                  todo.getEventType().substring(1);
                holder.tvEventType.setText("Type: " + eventType);
                holder.tvEventType.setVisibility(View.VISIBLE);
            } else {
                holder.tvEventType.setVisibility(View.GONE);
            }
            
            holder.tvCourseInfo.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.course_todo_bg));
        } else {
            holder.tvCourseInfo.setVisibility(View.GONE);
            holder.tvEventType.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.personal_todo_bg));
        }
        
        // Set delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onTodoDelete(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvDescription, tvDueDate, tvCourseInfo, tvEventType;
        CheckBox cbCompleted;
        View btnDelete;

        public TodoViewHolder(@NonNull View itemView, OnTodoActionListener listener) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvCourseInfo = itemView.findViewById(R.id.tvCourseInfo);
            tvEventType = itemView.findViewById(R.id.tvEventType);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnTodoActionListener {
        void onTodoStatusChanged(int position, boolean isCompleted);
        void onTodoDelete(int position);
    }
}