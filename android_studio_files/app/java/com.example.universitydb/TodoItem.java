package com.example.universitydb;

import java.util.Date;

public class TodoItem {
    private int todoId;
    private String studentId;
    private Integer eventId; // Nullable for personal todos
    private String todoTitle;
    private String todoDescription;
    private Date dueDate;
    private boolean isCompleted;
    private Date dateCreated;
    
    // Course related info (if it's a course event)
    private String courseId;
    private String courseName;
    private String eventType;
    
    // Constructor for personal todos
    public TodoItem(int todoId, String studentId, String todoTitle, 
                    String todoDescription, Date dueDate, boolean isCompleted, Date dateCreated) {
        this.todoId = todoId;
        this.studentId = studentId;
        this.eventId = null;
        this.todoTitle = todoTitle;
        this.todoDescription = todoDescription;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.dateCreated = dateCreated;
    }
    
    // Constructor for course todos
    public TodoItem(int todoId, String studentId, Integer eventId, String todoTitle, 
                    String todoDescription, Date dueDate, boolean isCompleted, Date dateCreated,
                    String courseId, String courseName, String eventType) {
        this.todoId = todoId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.todoTitle = todoTitle;
        this.todoDescription = todoDescription;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.dateCreated = dateCreated;
        this.courseId = courseId;
        this.courseName = courseName;
        this.eventType = eventType;
    }
    
    // Getters and setters
    public int getTodoId() {
        return todoId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public String getTodoTitle() {
        return todoTitle;
    }
    
    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }
    
    public String getTodoDescription() {
        return todoDescription;
    }
    
    public void setTodoDescription(String todoDescription) {
        this.todoDescription = todoDescription;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public Date getDateCreated() {
        return dateCreated;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public boolean isCourseEvent() {
        return eventId != null;
    }
}