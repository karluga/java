import java.util.Scanner;

public class StudentManagementSystem {
    static Scanner input = new Scanner(System.in); // Single static Scanner instance

    public static void main(String[] args) {
        final int MAX_STUDENTS = 100;
        Student[] students = new Student[MAX_STUDENTS];
        int numStudents = 0;

        // Main menu loop
        while (true) {
            System.out.println("\nStudent Management System");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Delete Student");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    // Add a new student
                    if (numStudents < MAX_STUDENTS) {
                        students[numStudents] = new Student();
                        students[numStudents].inputStudentDetails();
                        numStudents++;
                        System.out.println("Student added successfully!");
                    } else {
                        System.out.println("Maximum student capacity reached.");
                    }
                    break;

                case 2:
                    // View student records
                    if (numStudents == 0) {
                        System.out.println("No students added yet.");
                    } else {
                        for (int i = 0; i < numStudents; i++) {
                            students[i].displayStudentDetails();
                            System.out.println("--------------------");
                        }
                    }
                    break;

                case 3:
                    // Delete a student
                    if (numStudents == 0) {
                        System.out.println("No students to delete.");
                    } else {
                        System.out.print("Enter the name of the student to delete: ");
                        String nameToDelete = input.nextLine();

                        boolean found = false;
                        for (int i = 0; i < numStudents; i++) {
                            if (students[i].name.equalsIgnoreCase(nameToDelete)) {
                                for (int j = i; j < numStudents - 1; j++) {
                                    students[j] = students[j + 1];
                                }
                                numStudents--;
                                found = true;
                                System.out.println("Student deleted successfully!");
                                break;
                            }
                        }
                        if (!found) {
                            System.out.println("Student not found.");
                        }
                    }
                    break;

                case 4:
                    // Exit the program
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

// Separate class to represent a student
class Student {
    String name;
    int age;
    double grade;

    // Method to input student details
    public void inputStudentDetails() {
        System.out.print("Enter student name: ");
        name = StudentManagementSystem.input.nextLine();
        System.out.print("Enter student age: ");
        age = StudentManagementSystem.input.nextInt();
        StudentManagementSystem.input.nextLine(); // Consume the newline character
        System.out.print("Enter student grade: ");
        grade = StudentManagementSystem.input.nextDouble();
        StudentManagementSystem.input.nextLine(); // Consume the newline character
    }

    // Method to display student details
    public void displayStudentDetails() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Grade: " + grade);
    }
}