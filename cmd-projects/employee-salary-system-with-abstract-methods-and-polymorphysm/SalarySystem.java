import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

class SalarySystem {
    private static ArrayList<Employee> employees = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
        while (true) {
            System.out.println("Choose from the menu:");
            System.out.println("1. Register Employee");
            System.out.println("2. Print Employee Data");
            System.out.println("3. Print by Position");
            System.out.println("4. Print by Contract");
            System.out.println("5. Edit Employee");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice = validateIntegerInput();

            switch (choice) {
                case 1 -> registerEmployee();
                case 2 -> printEmployeeData();
                case 3 -> printByPosition();
                case 4 -> printByContract();
                case 5 -> editEmployee(); // Added edit employee option
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void editEmployee() {
        System.out.print("Enter the SSN of the employee you want to edit: ");
        int ssn = validateIntegerInput();

        // Find the employee by SSN
        Employee employee = findEmployeeBySSN(ssn);

        if (employee != null) {
            System.out.println("Employee found: " + employee);
            System.out.println("What would you like to edit?");
            System.out.println("1. First Name");
            System.out.println("2. Last Name");
            System.out.println("3. Weekly Salary (Full-Time)");
            System.out.println("4. Hours Worked (Part-Time)");
            System.out.println("5. Hourly Rate (Part-Time)");
            System.out.println("6. Total Sales (Commission)");
            System.out.println("7. Commission Rate (Commission)");
            System.out.print("Choose option: ");
            int choice = validateIntegerInput();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new first name: ");
                    String firstName = scanner.nextLine();
                    employee.setFirstName(firstName); // Assuming Employee has a setter method
                }
                case 2 -> {
                    System.out.print("Enter new last name: ");
                    String lastName = scanner.nextLine();
                    employee.setLastName(lastName); // Assuming Employee has a setter method
                }
                case 3 -> {
                    if (employee instanceof FullTimeEmployee) {
                        System.out.print("Enter new weekly salary: ");
                        double weeklySalary = validateNonNegativeInput();
                        ((FullTimeEmployee) employee).setWeeklySalary(weeklySalary); // Cast and update
                    } else {
                        System.out.println("This is not a full-time employee.");
                    }
                }
                case 4 -> {
                    if (employee instanceof PartTimeEmployee) {
                        System.out.print("Enter new hours worked: ");
                        double hoursWorked = validateNonNegativeInput();
                        ((PartTimeEmployee) employee).setHoursWorked(hoursWorked); // Cast and update
                    } else {
                        System.out.println("This is not a part-time employee.");
                    }
                }
                case 5 -> {
                    if (employee instanceof PartTimeEmployee) {
                        System.out.print("Enter new hourly rate: ");
                        double hourlyRate = validateNonNegativeInput();
                        ((PartTimeEmployee) employee).setHourlyRate(hourlyRate); // Cast and update
                    } else {
                        System.out.println("This is not a part-time employee.");
                    }
                }
                case 6 -> {
                    if (employee instanceof CommissionEmployee) {
                        System.out.print("Enter new total sales: ");
                        double totalSales = validateNonNegativeInput();
                        ((CommissionEmployee) employee).setTotalSales(totalSales); // Cast and update
                    } else {
                        System.out.println("This is not a commission employee.");
                    }
                }
                case 7 -> {
                    if (employee instanceof CommissionEmployee) {
                        System.out.print("Enter new commission rate: ");
                        double commissionRate = validateNonNegativeInput();
                        ((CommissionEmployee) employee).setCommissionRate(commissionRate); // Cast and update
                    } else {
                        System.out.println("This is not a commission employee.");
                    }
                }
                default -> System.out.println("Invalid option.");
            }

            System.out.println("Employee updated successfully!");
        } else {
            System.out.println("Employee with SSN " + ssn + " not found.");
        }
    }

    // Method to find employee by SSN
    private static Employee findEmployeeBySSN(int ssn) {
        for (Employee employee : employees) {
            if (employee.socialSecurityNumber == ssn) {
                return employee;
            }
        }
        return null;
    }

    private static void registerEmployee() {
        System.out.println("\n1. Full Time Employee");
        System.out.println("2. Part Time Employee");
        System.out.println("3. Commission Employee");
        System.out.println("4. Base Employee with Commission");
        System.out.print("Choose type of employee to register: ");
        int type = validateIntegerInput();

        try {
            // Proper handling for first and last name
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter social security number: ");
            int ssn = validateIntegerInput();


            if (isDuplicateSSN(ssn)) {
                throw new DuplicateSSNException("Social Security Number already exists.");
            }

            switch (type) {
                case 1 -> {
                    System.out.print("Enter weekly salary: ");
                    double weeklySalary = validateNonNegativeInput();
                    employees.add(new FullTimeEmployee(firstName, lastName, ssn, weeklySalary));
                }
                case 2 -> {
                    System.out.print("Enter hours worked: ");
                    double hoursWorked = validateNonNegativeInput();
                    System.out.print("Enter hourly rate: ");
                    double hourlyRate = validateNonNegativeInput();
                    employees.add(new PartTimeEmployee(firstName, lastName, ssn, hoursWorked, hourlyRate));
                }
                case 3 -> {
                    System.out.print("Enter total sales: ");
                    double totalSales = validateNonNegativeInput();
                    System.out.print("Enter commission rate (as decimal): ");
                    double commissionRate = validateNonNegativeInput();
                    employees.add(new CommissionEmployee(firstName, lastName, ssn, totalSales, commissionRate));
                }
                case 4 -> {
                    System.out.print("Enter base salary: ");
                    double baseSalary = validateNonNegativeInput();
                    System.out.print("Enter total sales: ");
                    double totalSales = validateNonNegativeInput();
                    System.out.print("Enter commission rate (as decimal): ");
                    double commissionRate = validateNonNegativeInput();
                    employees.add(new BaseCommissionEmployee(firstName, lastName, ssn, baseSalary, totalSales, commissionRate));
                }
                default -> throw new InvalidEmployeeTypeException("Invalid employee type selected.");
            }
            System.out.println("Employee registered successfully!");
        } catch (DuplicateSSNException | InvalidEmployeeTypeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printEmployeeData() {
        try {
            if (employees.isEmpty()) {
                throw new NoEmployeesException("No employees registered. Please add employees first.");
            }
            System.out.println("\nEmployee Data:");
            for (Employee employee : employees) {
                System.out.println(employee + ", Weekly Income: " + employee.income());
            }
        } catch (NoEmployeesException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printByPosition() {
        try {
            if (employees.isEmpty()) {
                throw new NoEmployeesException("No employees registered. Please add employees first.");
            }
            System.out.println("\nEmployees by Position:");
            for (Employee employee : employees) {
                System.out.println(employee.getClass().getSimpleName() + ": " + employee);
            }
        } catch (NoEmployeesException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printByContract() {
        try {
            if (employees.isEmpty()) {
                throw new NoEmployeesException("No employees registered. Please add employees first.");
            }
            System.out.println("\nEmployee Contracts:");
            for (Employee employee : employees) {
                String contractType = (employee instanceof FullTimeEmployee || employee instanceof PartTimeEmployee)
                        ? "Seasonal Contract" : "Annual Contract";
                System.out.println(employee + " - " + contractType);
            }
        } catch (NoEmployeesException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean isDuplicateSSN(int ssn) {
        for (Employee employee : employees) {
            if (employee.socialSecurityNumber == ssn) {
                return true;
            }
        }
        return false;
    }

    private static int validateIntegerInput() {
        while (true) {
            try {
                int value = scanner.nextInt(); // Read numeric input
                scanner.nextLine(); // Consume the newline character after the number
                return value;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
                scanner.next(); // Clear invalid input
            }
        }
    }
    
    private static double validateNonNegativeInput() {
        while (true) {
            try {
                double value = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character after the number
                if (value >= 0) {
                    return value;
                } else {
                    System.out.print("Invalid input. Please enter a non-negative number: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
                scanner.next(); // Clear invalid input
            }
        }
    }
}
