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

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private Context context;
    private List<Section> sectionList;
    private OnSectionClickListener onSectionClickListener;

    public SectionAdapter(Context context, List<Section> sectionList, OnSectionClickListener onSectionClickListener) {
        this.context = context;
        this.sectionList = sectionList;
        this.onSectionClickListener = onSectionClickListener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_item, parent, false);
        return new SectionViewHolder(view, onSectionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Section section = sectionList.get(position);
        
        String courseInfo = section.getCourseId() + ": " + section.getCourseName();
        holder.tvCourseInfo.setText(courseInfo);
        
        String sectionInfo = "Section: " + section.getSectionId();
        holder.tvSectionInfo.setText(sectionInfo);
        
        String termInfo = section.getSemester() + " " + section.getYear();
        holder.tvTermInfo.setText(termInfo);
        
        holder.tvSchedule.setText(section.getSchedule());
        holder.tvLocation.setText(section.getLocation());
        
        String enrollmentInfo = "Enrollment: " + section.getEnrolledStudents();
        holder.tvEnrollment.setText(enrollmentInfo);
        
        // Set current semester indicator
        if (section.isCurrent()) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.current_section_bg));
            holder.tvCurrentIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.past_section_bg));
            holder.tvCurrentIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView tvCourseInfo, tvSectionInfo, tvTermInfo, tvSchedule, tvLocation, tvEnrollment, tvCurrentIndicator;
        OnSectionClickListener onSectionClickListener;

        public SectionViewHolder(@NonNull View itemView, OnSectionClickListener onSectionClickListener) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            tvCourseInfo = itemView.findViewById(R.id.tvCourseInfo);
            tvSectionInfo = itemView.findViewById(R.id.tvSectionInfo);
            tvTermInfo = itemView.findViewById(R.id.tvTermInfo);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvEnrollment = itemView.findViewById(R.id.tvEnrollment);
            tvCurrentIndicator = itemView.findViewById(R.id.tvCurrentIndicator);
            
            this.onSectionClickListener = onSectionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSectionClickListener.onSectionClick(getAdapterPosition());
        }
    }

    public interface OnSectionClickListener {
        void onSectionClick(int position);
    }
}