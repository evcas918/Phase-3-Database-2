package com.example.universitydb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;
    private OnCourseClickListener onCourseClickListener;

    public CourseAdapter(Context context, List<Course> courseList, OnCourseClickListener onCourseClickListener) {
        this.context = context;
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view, onCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        
        holder.tvCourseId.setText(course.getCourseId());
        holder.tvCourseName.setText(course.getCourseName());
        holder.tvSection.setText("Section: " + course.getSectionId());
        holder.tvCredits.setText(course.getCredits() + " credits");
        holder.tvInstructor.setText("Instructor: " + course.getInstructor());
        holder.tvSchedule.setText(course.getDay() + " " + course.getStartTime() + "-" + course.getEndTime());
        holder.tvLocation.setText("Location: " + course.getLocation());
        
        String enrollmentText = "Enrollment: " + course.getEnrolledStudents() + "/15";
        holder.tvEnrollment.setText(enrollmentText);
        
        // Set availability message and color
        if (course.isFull()) {
            holder.tvAvailability.setText("FULL");
            holder.tvAvailability.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.course_full_bg));
        } else {
            holder.tvAvailability.setText(course.getAvailableSeats() + " seats available");
            holder.tvAvailability.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.course_available_bg));
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCourseId, tvCourseName, tvSection, tvCredits, tvInstructor, 
                 tvSchedule, tvLocation, tvEnrollment, tvAvailability;
        CardView cardView;
        OnCourseClickListener onCourseClickListener;

        public CourseViewHolder(@NonNull View itemView, OnCourseClickListener onCourseClickListener) {
            super(itemView);
            
            tvCourseId = itemView.findViewById(R.id.tvCourseId);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvSection = itemView.findViewById(R.id.tvSection);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvEnrollment = itemView.findViewById(R.id.tvEnrollment);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            cardView = itemView.findViewById(R.id.cardView);
            
            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCourseClickListener.onCourseClick(getAdapterPosition());
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(int position);
    }
}