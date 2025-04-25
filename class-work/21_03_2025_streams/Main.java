import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Student> students = Arrays.asList(
            new Student("Mārtiņš Kalniņš", 9.2),
            new Student("Elza Bērziņa", 9.0),
            new Student("Dace Ozoliņa", 8.7),
            new Student("Gustavs Liepiņš", 5.1)
        );
        
        // Students with grade > 8.5
        students.stream()
                .filter(student -> student.getGrade() > 8.5)
                .forEach(student -> System.out.println(student.getName()));

        Student bestStudent = Collections.max(students, Comparator.comparing(student -> student.getGrade()));
        System.out.println(bestStudent);
    }
}

class Student {
    private String name;
    private double grade;

    public Student(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }
    public String toString() {
        return name + " - " + grade;
    }
}
