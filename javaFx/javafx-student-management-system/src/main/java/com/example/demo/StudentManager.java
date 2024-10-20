package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StudentManager {
    private static final String FILE_PATH = "students.json";
    public List<Student> students;

    public StudentManager() {
        students = loadStudents();
    }

    private List<Student> loadStudents() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type studentListType = new TypeToken<ArrayList<Student>>() {}.getType();
            return new Gson().fromJson(reader, studentListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveStudents() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            new Gson().toJson(students, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
        saveStudents();
    }

    public void deleteStudent(String id) {
        students.removeIf(student -> student.getId().equals(id));
        saveStudents();
    }
}
