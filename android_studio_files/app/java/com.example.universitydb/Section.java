package com.example.universitydb;

public class Section {
    private String courseId;
    private String courseName;
    private String sectionId;
    private String semester;
    private int year;
    private String schedule;
    private String location;
    private int enrolledStudents;
    private boolean isCurrent;

    public Section(String courseId, String courseName, String sectionId, String semester, 
                   int year, String schedule, String location, int enrolledStudents, boolean isCurrent) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.sectionId = sectionId;
        this.semester = semester;
        this.year = year;
        this.schedule = schedule;
        this.location = location;
        this.enrolledStudents = enrolledStudents;
        this.isCurrent = isCurrent;
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

    public String getSchedule() {
        return schedule;
    }

    public String getLocation() {
        return location;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public String getSectionIdentifier() {
        return courseId + "|" + sectionId + "|" + semester + "|" + year;
    }
}