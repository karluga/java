import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Company company = new Company();

        while (true) {
            System.out.println("""
            Menu:
            1. Add Department
            2. Add Employee to Department
            3. Display Company Structure
            4. Quit
            Enter your choice (1-4): 
            """);

            int choice;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    scanner.nextLine();
                }
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Department Name: ");
                    String deptName = scanner.nextLine();
                    company.addDepartment(deptName);
                    break;
                case 2:
                    if (company.getDepartmentCount() == 0) {
                        System.out.println("There are currently no departments, please add one.");
                        continue;
                    }
                    System.out.print("Enter Employee Name: ");
                    String empName = scanner.nextLine();
                    System.out.print("Enter Employee ID: ");
                    String empId = scanner.nextLine();

                    System.out.println("Available Departments:");
                    company.listDepartments();
                    
                    int deptChoice;
                    while (true) {
                        System.out.print("Choose Department (1-" + company.getDepartmentCount() + "): ");
                        try {
                            deptChoice = scanner.nextInt();
                            scanner.nextLine();
                            if (deptChoice >= 1 && deptChoice <= company.getDepartmentCount()) {
                                break;
                            } else {
                                System.out.println("Invalid choice. Please select a valid department number.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.nextLine();
                        }
                    }

                    company.addEmployeeToDepartment(empName, empId, deptChoice - 1);
                    break;
                case 3:
                    company.displayCompanyStructure();
                    break;
                case 4:
                    System.out.println("Exiting program...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
