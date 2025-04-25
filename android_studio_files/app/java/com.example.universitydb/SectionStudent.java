package com.example.universitydb;

public class SectionStudent {
    private String studentId;
    private String name;
    private String email;
    private String studentType;
    private String grade;

    public SectionStudent(String studentId, String name, String email, String studentType, String grade) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.studentType = studentType;
        this.grade = grade;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentType() {
        return studentType;
    }

    public String getGrade() {
        return grade;
    }

    public boolean isInProgress() {
        return grade == null;
    }
}