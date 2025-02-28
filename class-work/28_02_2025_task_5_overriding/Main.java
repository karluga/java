public class Main {
    public static void main(String[] args) {
        int[] grades = {54, 58, 72, 96};
        
        Student undergrad = new Student("Anna", grades);
        GraduateStudent grad = new GraduateStudent("Anna", grades);
        
        System.out.println("student: " + undergrad.name);
        for (int i = 0; i < grades.length; i++) {
            System.out.println("Grade " + (i + 1) + ": " + grades[i]);
        }
        
        System.out.println("Undergraduate letter grade: " + undergrad.getLetterGrade());
        System.out.println("Graduate letter grade: " + grad.getLetterGrade());
    }
}
