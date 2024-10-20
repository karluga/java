package com.example.demo;

public class Assignment {
    public String dueDate;  // Timestamp
    public double grade;

    public Assignment(String dueDate, double grade) {
        this.dueDate = dueDate;
        this.grade = grade;
    }

    public String getDueDate() {
        return dueDate;
    }

    public double getGrade() {
        return grade;
    }
}
