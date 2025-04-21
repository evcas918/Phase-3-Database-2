package com.example.universitydb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseHistoryAdapter extends RecyclerView.Adapter<CourseHistoryAdapter.CourseHistoryViewHolder> {

    private Context context;
    private List<HistoryCourse> courseList;

    public CourseHistoryAdapter(Context context, List<HistoryCourse> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_course_item, parent, false);
        return new CourseHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHistoryViewHolder holder, int position) {
        HistoryCourse course = courseList.get(position);
        
        holder.tvCourseInfo.setText(course.getCourseId() + ": " + course.getCourseName());
        holder.tvSectionInfo.setText("Section: " + course.getSectionId() + " | " + 
                                    course.getSemester() + " " + course.getYear());
        holder.tvCredits.setText(course.getCredits() + " credits");
        holder.tvInstructor.setText("Instructor: " + course.getInstructor());
        
        if (course.isInProgress()) {
            holder.tvGrade.setText("In Progress");
            holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
        } else {
            holder.tvGrade.setText("Grade: " + course.getGrade());
            
            // Set color based on grade
            String grade = course.getGrade();
            if (grade.startsWith("A")) {
                holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            } else if (grade.startsWith("B")) {
                holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
            } else if (grade.startsWith("C")) {
                holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
            } else if (grade.startsWith("D")) {
                holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
            } else if (grade.equals("F")) {
                holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            }
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseInfo, tvSectionInfo, tvCredits, tvInstructor, tvGrade;

        public CourseHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvCourseInfo = itemView.findViewById(R.id.tvCourseInfo);
            tvSectionInfo = itemView.findViewById(R.id.tvSectionInfo);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            tvGrade = itemView.findViewById(R.id.tvGrade);
        }
    }
}