package com.example.demo;

import java.util.List;
import java.util.Map;

public class Student {
    public String name;
    public String id;
    public String course;
    public Map<String, List<Assignment>> grades;  // Subject -> list of assignments

    public Student(String name, String id, String course, Map<String, List<Assignment>> grades) {
        this.name = name;
        this.id = id;
        this.course = course;
        this.grades = grades;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCourse() {
        return course;
    }

    public Map<String, List<Assignment>> getGrades() {
        return grades;
    }

    // Modify the method to handle empty grade lists
    public String getAverageGrade() {
        // Check if there are any assignments first
        long totalAssignments = grades.values().stream()
                .flatMap(List::stream)
                .count();

        if (totalAssignments == 0) {
            return "N/A";  // Return N/A if no grades are available
        }

        // Calculate average grade if assignments exist
        double average = grades.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Assignment::getGrade)
                .average()
                .orElse(0.0);

        return String.format("%.2f", average);  // Return the average formatted to 2 decimal places
    }
}
