package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CourseManager {
    private static final String FILE_PATH = "courses.json";
    public List<String> courses;

    public CourseManager() {
        courses = loadCourses();
    }

    private List<String> loadCourses() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type courseListType = new TypeToken<ArrayList<String>>() {}.getType();
            return new Gson().fromJson(reader, courseListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getCourses() {
        return courses;
    }
}
