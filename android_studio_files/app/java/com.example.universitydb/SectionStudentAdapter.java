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

public class SectionStudentAdapter extends RecyclerView.Adapter<SectionStudentAdapter.StudentViewHolder> {

    private Context context;
    private List<SectionStudent> studentList;
    private boolean isCurrentSemester;

    public SectionStudentAdapter(Context context, List<SectionStudent> studentList, boolean isCurrentSemester) {
        this.context = context;
        this.studentList = studentList;
        this.isCurrentSemester = isCurrentSemester;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        SectionStudent student = studentList.get(position);
        
        holder.tvStudentId.setText("ID: " + student.getStudentId());
        holder.tvStudentName.setText(student.getName());
        holder.tvStudentEmail.setText(student.getEmail());
        holder.tvStudentType.setText(student.getStudentType());
        
        if (isCurrentSemester || student.isInProgress()) {
            holder.tvGrade.setText("In Progress");
            holder.tvGrade.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
        } else {
            holder.tvGrade.setText("Grade: " + student.getGrade());
            
            // Set color based on grade
            String grade = student.getGrade();
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
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentId, tvStudentName, tvStudentEmail, tvStudentType, tvGrade;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvStudentType = itemView.findViewById(R.id.tvStudentType);
            tvGrade = itemView.findViewById(R.id.tvGrade);
        }
    }
}