
import java.util.ArrayList;
import java.util.Scanner;
import Employee;
public class SalarySystem {
    private static ArrayList<Employee> employees = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Choose from the menu:");
            System.out.println("1. Register Employee");
            System.out.println("2. Print Employee Data");
            System.out.println("3. Print by Position");
            System.out.println("4. Print by Contract");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerEmployee();
                    break;
                case 2:
                    printEmployeeData();
                    break;
                case 3:
                    printByPosition();
                    break;
                case 4:
                    printByContract();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void registerEmployee() {
        System.out.println("1. Full Time Employee");
        System.out.println("2. Part Time Employee");
        System.out.println("3. Commission Employee");
        System.out.println("4. Base Employee with Commission");
        System.out.print("Choose type of employee to register: ");
        int type = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter social security number: ");
        int ssn = scanner.nextInt();

        switch (type) {
            case 1:
                System.out.print("Enter weekly salary: ");
                double weeklySalary = scanner.nextDouble();
                employees.add(new FullTimeEmployee(firstName, lastName, ssn, weeklySalary));
                break;
            case 2:
                System.out.print("Enter hours worked: ");
                double hoursWorked = scanner.nextDouble();
                System.out.print("Enter hourly rate: ");
                double hourlyRate = scanner.nextDouble();
                employees.add(new PartTimeEmployee(firstName, lastName, ssn, hoursWorked, hourlyRate));
                break;
            case 3:
                System.out.print("Enter total sales: ");
                double totalSales = scanner.nextDouble();
                System.out.print("Enter commission rate (as decimal): ");
                double commissionRate = scanner.nextDouble();
                employees.add(new CommissionEmployee(firstName, lastName, ssn, totalSales, commissionRate));
                break;
            case 4:
                System.out.print("Enter base salary: ");
                double baseSalary = scanner.nextDouble();
                System.out.print("Enter total sales: ");
                double baseTotalSales = scanner.nextDouble();
                System.out.print("Enter commission rate (as decimal): ");
                double baseCommissionRate = scanner.nextDouble();
                System.out.print("Enter bonus: ");
                double bonus = scanner.nextDouble();
                employees.add(new BaseCommissionEmployee(firstName, lastName, ssn, baseSalary, baseTotalSales, baseCommissionRate, bonus));
                break;
            default:
                System.out.println("Invalid type. Returning to main menu.");
        }
    }

    private static void printEmployeeData() {
        if (employees.isEmpty()) {
            System.out.println("No employees registered.");
        } else {
            System.out.println("\nEmployee Data:");
            for (Employee employee : employees) {
                System.out.println(employee + ", Weekly Income: " + employee.income());
            }
        }
    }

    private static void printByPosition() {
        System.out.println("\nEmployees by Position:");
        for (Employee employee : employees) {
            System.out.println(employee.getClass().getSimpleName() + ": " + employee);
        }
    }

    private static void printByContract() {
        System.out.println("\nEmployee Contracts:");
        for (Employee employee : employees) {
            String contractType = (employee instanceof FullTimeEmployee || employee instanceof PartTimeEmployee)
                                  ? "Seasonal Contract" : "Annual Contract";
            System.out.println(employee + " - " + contractType);
        }
    }
}
