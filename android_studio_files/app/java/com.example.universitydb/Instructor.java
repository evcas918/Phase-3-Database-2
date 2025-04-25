package com.example.universitydb;

public class Instructor {
    private String instructorId;
    private String name;
    private String email;
    private String department;
    private String title;

    public Instructor(String instructorId, String name, String email, String department, String title) {
        this.instructorId = instructorId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.title = title;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getTitle() {
        return title;
    }
}