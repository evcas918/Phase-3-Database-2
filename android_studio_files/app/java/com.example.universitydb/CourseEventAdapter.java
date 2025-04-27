package com.example.universitydb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CourseEventAdapter extends RecyclerView.Adapter<CourseEventAdapter.EventViewHolder> {

    private Context context;
    private List<CourseEvent> eventList;
    private OnEventActionListener listener;
    private SimpleDateFormat dateFormat;

    public CourseEventAdapter(Context context, List<CourseEvent> eventList, OnEventActionListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        CourseEvent event = eventList.get(position);
        
        holder.tvCourseInfo.setText(event.getCourseId() + ": " + event.getCourseName());
        holder.tvEventTitle.setText(event.getEventTitle());
        
        if (event.getEventDescription() != null && !event.getEventDescription().isEmpty()) {
            holder.tvEventDescription.setText(event.getEventDescription());
            holder.tvEventDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvEventDescription.setVisibility(View.GONE);
        }
        
        holder.tvEventDate.setText(dateFormat.format(event.getEventDate()));
        
        // Capitalize first letter of event type
        String eventType = event.getEventType();
        if (eventType != null && !eventType.isEmpty()) {
            eventType = eventType.substring(0, 1).toUpperCase() + eventType.substring(1);
            holder.tvEventType.setText(eventType);
        } else {
            holder.tvEventType.setText("Event");
        }
        
        // Set button state based on whether it's already in the to-do list
        if (event.isInTodoList()) {
            holder.btnAddToTodo.setText("Added to To-Do List");
            holder.btnAddToTodo.setEnabled(false);
        } else {
            holder.btnAddToTodo.setText("Add to To-Do List");
            holder.btnAddToTodo.setEnabled(true);
            
            final int pos = position;
            holder.btnAddToTodo.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToTodoList(pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseInfo, tvEventTitle, tvEventDescription, tvEventDate, tvEventType;
        Button btnAddToTodo;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvCourseInfo = itemView.findViewById(R.id.tvCourseInfo);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventType = itemView.findViewById(R.id.tvEventType);
            btnAddToTodo = itemView.findViewById(R.id.btnAddToTodo);
        }
    }

    public interface OnEventActionListener {
        void onAddToTodoList(int position);
    }
}