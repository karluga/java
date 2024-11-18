import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

class SalarySystem {
    private static ArrayList<Employee> employees = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            // displaying menu options to the user
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
                case 5 -> editEmployee(); // added edit employee option
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // method to handle editing employee information
    private static void editEmployee() {
        System.out.print("Enter the SSN of the employee you want to edit: ");
        int ssn = validateIntegerInput();

        // find the employee by ssn
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

            // switch case to choose which employee detail to edit
            switch (choice) {
                case 1 -> editFirstName(employee);
                case 2 -> editLastName(employee);
                case 3 -> editWeeklySalary(employee);
                case 4 -> editHoursWorked(employee);
                case 5 -> editHourlyRate(employee);
                case 6 -> editTotalSales(employee);
                case 7 -> editCommissionRate(employee);
                default -> System.out.println("Invalid option.");
            }

            System.out.println("Employee updated successfully!");
        } else {
            System.out.println("Employee with SSN " + ssn + " not found.");
        }
    }

    // edit employee's first name
    private static void editFirstName(Employee employee) {
        System.out.print("Enter new first name: ");
        String firstName = scanner.nextLine();
        employee.setFirstName(firstName); // set the new first name
    }

    // edit employee's last name
    private static void editLastName(Employee employee) {
        System.out.print("Enter new last name: ");
        String lastName = scanner.nextLine();
        employee.setLastName(lastName); // set the new last name
    }

    // edit the weekly salary for full-time employees
    private static void editWeeklySalary(Employee employee) {
        if (employee instanceof FullTimeEmployee) {
            System.out.print("Enter new weekly salary: ");
            double weeklySalary = validateNonNegativeInput(); // validate salary input
            ((FullTimeEmployee) employee).setWeeklySalary(weeklySalary); // update the salary
        } else {
            System.out.println("This is not a full-time employee.");
        }
    }

    // edit the hours worked for part-time employees
    private static void editHoursWorked(Employee employee) {
        if (employee instanceof PartTimeEmployee) {
            System.out.print("Enter new hours worked: ");
            double hoursWorked = validateNonNegativeInput(); // validate hours worked input
            ((PartTimeEmployee) employee).setHoursWorked(hoursWorked); // update hours worked
        } else {
            System.out.println("This is not a part-time employee.");
        }
    }

    // edit the hourly rate for part-time employees
    private static void editHourlyRate(Employee employee) {
        if (employee instanceof PartTimeEmployee) {
            System.out.print("Enter new hourly rate: ");
            double hourlyRate = validateNonNegativeInput(); // validate hourly rate input
            ((PartTimeEmployee) employee).setHourlyRate(hourlyRate); // update hourly rate
        } else {
            System.out.println("This is not a part-time employee.");
        }
    }

    // edit total sales for commission-based employees
    private static void editTotalSales(Employee employee) {
        if (employee instanceof CommissionEmployee) {
            System.out.print("Enter new total sales: ");
            double totalSales = validateNonNegativeInput(); // validate total sales input
            ((CommissionEmployee) employee).setTotalSales(totalSales); // update total sales
        } else {
            System.out.println("This is not a commission employee.");
        }
    }

    // edit commission rate for commission-based employees
    private static void editCommissionRate(Employee employee) {
        if (employee instanceof CommissionEmployee) {
            System.out.print("Enter new commission rate: ");
            double commissionRate = validateNonNegativeInput(); // validate commission rate input
            ((CommissionEmployee) employee).setCommissionRate(commissionRate); // update commission rate
        } else {
            System.out.println("This is not a commission employee.");
        }
    }

    // method to find employee by ssn
    private static Employee findEmployeeBySSN(int ssn) {
        for (Employee employee : employees) {
            if (employee.socialSecurityNumber == ssn) {
                return employee; // return the employee with the matching ssn
            }
        }
        return null; // if no match is found, return null
    }

    // method to register a new employee
    private static void registerEmployee() {
        System.out.println("\n1. Full Time Employee");
        System.out.println("2. Part Time Employee");
        System.out.println("3. Commission Employee");
        System.out.println("4. Base Employee with Commission");
        System.out.print("Choose type of employee to register: ");
        int type = validateIntegerInput();

        try {
            // proper handling for first and last name
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter social security number: ");
            int ssn = validateIntegerInput();

            // check for duplicate ssn before adding employee
            if (isDuplicateSSN(ssn)) {
                throw new DuplicateSSNException("Social Security Number already exists.");
            }

            // registering employee based on the selected type
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

    // print all employee data
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

    // print employees by their position
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

    // print employees by contract type
    private static void printByContract() {
        try {
            if (employees.isEmpty()) {
                throw new NoEmployeesException("No employees registered. Please add employees first.");
            }
            System.out.println("\nEmployees by Contract:");
            for (Employee employee : employees) {
                System.out.println(employee);
            }
        } catch (NoEmployeesException e) {
            System.out.println(e.getMessage());
        }
    }

    // method to validate integer inputs from the user
    private static int validateIntegerInput() {
        int input = -1;
        boolean valid = false;
        while (!valid) {
            try {
                input = scanner.nextInt();
                scanner.nextLine(); // consume newline
                valid = true;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear invalid input
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
        return input;
    }

    // method to validate non-negative double inputs
    private static double validateNonNegativeInput() {
        double input = -1;
        while (input < 0) {
            try {
                input = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                if (input < 0) {
                    System.out.println("Please enter a non-negative value.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear invalid input
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return input;
    }

    // method to check for duplicate ssn
    private static boolean isDuplicateSSN(int ssn) {
        return employees.stream().anyMatch(emp -> emp.socialSecurityNumber == ssn);
    }

    // custom exception: duplicate ssn
    static class DuplicateSSNException extends Exception {
        public DuplicateSSNException(String message) {
            super(message);
        }
    }

    // custom exception: invalid employee type
    static class InvalidEmployeeTypeException extends Exception {
        public InvalidEmployeeTypeException(String message) {
            super(message);
        }
    }

    // custom exception: no employees registered
    static class NoEmployeesException extends Exception {
        public NoEmployeesException(String message) {
            super(message);
        }
    }
}
