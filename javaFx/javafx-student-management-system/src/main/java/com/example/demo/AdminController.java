package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;

public class AdminController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> idColumn;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> courseColumn;

    @FXML
    private TableColumn<Student, Double> gradeColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField idField;

    @FXML
    private ComboBox<String> courseDropdown;

    private StudentManager studentManager;
    private CourseManager courseManager;

    @FXML
    public void initialize() {
        studentManager = new StudentManager();
        courseManager = new CourseManager();

        // Populate ComboBox with courses
        courseDropdown.getItems().addAll(courseManager.getCourses());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("averageGrade"));

        studentTable.getItems().addAll(studentManager.getStudents());
    }

    @FXML
    protected void onAddStudentClick() {
        String name = nameField.getText();
        String id = idField.getText();
        String course = courseDropdown.getSelectionModel().getSelectedItem();

        try {
            validateStudentData(name, id, course); // Validate inputs

            // Create and add student
            Student student = new Student(name, id, course, new HashMap<>());
            studentManager.addStudent(student);
            studentTable.getItems().add(student);

            // Clear fields after adding
            nameField.clear();
            idField.clear();
            courseDropdown.getSelectionModel().clearSelection();

        } catch (InvalidStudentDataException e) {
            // Display error message to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Data");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void onDeleteStudentClick() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            studentManager.deleteStudent(selectedStudent.getId());
            studentTable.getItems().remove(selectedStudent);
        } else {
            // Show an error dialog if no student is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Student Selected");
            alert.setContentText("Please select a student to delete.");
            alert.showAndWait();
        }
    }

    // Validate student name, ID, and course
    private void validateStudentData(String name, String id, String course) throws InvalidStudentDataException {
        if (name.isEmpty() || id.isEmpty() || course == null) {
            throw new InvalidStudentDataException("All fields must be filled.");
        }
        // Validate ID format (2-3 letters followed by 5 digits)
        if (!id.matches("^[A-Za-z]{2,3}\\d{5}$")) {
            throw new InvalidStudentDataException("Student ID must start with 2-3 letters and end with 5 digits.");
        }
    }
}
