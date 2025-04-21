package com.example.universitydb;

public class HistoryCourse {
    private String courseId;
    private String courseName;
    private String sectionId;
    private String semester;
    private int year;
    private int credits;
    private String instructor;
    private String grade;

    public HistoryCourse(String courseId, String courseName, String sectionId, 
                         String semester, int year, int credits, String instructor, String grade) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.sectionId = sectionId;
        this.semester = semester;
        this.year = year;
        this.credits = credits;
        this.instructor = instructor;
        this.grade = grade;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public int getCredits() {
        return credits;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getGrade() {
        return grade;
    }

    public boolean isInProgress() {
        return grade == null;
    }
}