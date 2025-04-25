import java.util.*;

class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public void addDepartment(String name) {
        departments.add(new Department(name));
        System.out.println("Department added successfully!\n");
    }

    public void addEmployeeToDepartment(String empName, String empId, int deptIndex) {
        if (deptIndex >= 0 && deptIndex < departments.size()) {
            departments.get(deptIndex).addEmployee(new Employee(empName, empId));
            System.out.println("Employee added to " + departments.get(deptIndex).getName() + " department.\n");
        } else {
            System.out.println("Invalid department selection.");
        }
    }

    public void displayCompanyStructure() {
        System.out.println("Company Structure:");
        for (Department dept : departments) {
            System.out.println("Department: " + dept.getName() + "\n");
            dept.displayEmployees();
        }
    }

    public void listDepartments() {
        for (int i = 0; i < departments.size(); i++) {
            System.out.println((i + 1) + ". " + departments.get(i).getName());
        }
    }

    public int getDepartmentCount() {
        return departments.size();
    }
}