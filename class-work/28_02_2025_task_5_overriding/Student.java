class Student {
    protected String name;
    protected int[] grades;
    
    public Student(String name, int[] grades) {
        this.name = name;
        this.grades = grades;
    }
    
    public double getAverageGrade() {
        int sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        return (double) sum / grades.length;
    }
    
    public char getLetterGrade() {
        double avg = getAverageGrade();
        if (avg >= 90) return 'A';
        if (avg >= 80) return 'B';
        if (avg >= 70) return 'C';
        if (avg >= 60) return 'D';
        return 'F';
    }
}

class GraduateStudent extends Student {
    public GraduateStudent(String name, int[] grades) {
        super(name, grades);
    }
    
    @Override
    public char getLetterGrade() {
        if (getAverageGrade() < 70) {
            return 'F';
        } else {
            return super.getLetterGrade();
        }
    }
}