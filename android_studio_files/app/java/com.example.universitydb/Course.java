package com.example.universitydb;

public class Course {
    private String courseId;
    private String courseName;
    private String sectionId;
    private int credits;
    private String instructor;
    private String day;
    private String startTime;
    private String endTime;
    private String location;
    private int enrolledStudents;
    private int availableSeats;
    private boolean isFull;

    public Course(String courseId, String courseName, String sectionId, int credits, String instructor, 
                  String day, String startTime, String endTime, String location, 
                  int enrolledStudents, int availableSeats, boolean isFull) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.sectionId = sectionId;
        this.credits = credits;
        this.instructor = instructor;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.enrolledStudents = enrolledStudents;
        this.availableSeats = availableSeats;
        this.isFull = isFull;
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

    public int getCredits() {
        return credits;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean isFull() {
        return isFull;
    }
}