import java.util.*;

class Department {
    private String name;
    private List<Employee> employees;

    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void displayEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No one here yet.");
        } else {
            System.out.println("""
            +----------------+------------+
            | Name           | ID         |
            +----------------+------------+
            """);
            for (Employee e : employees) {
                System.out.printf("| %-14s | %-10s |%n", e.getName(), e.getId());
            }
            System.out.println("""
            +----------------+------------+
            """);
        }
    }
    
}