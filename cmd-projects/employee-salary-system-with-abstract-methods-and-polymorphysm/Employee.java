abstract class Employee {
    protected String firstName;
    protected String lastName;
    protected int socialSecurityNumber;

    // Other fields for subclasses (like weeklySalary, hourlyRate, etc.)

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSocialSecurityNumber(int ssn) {
        this.socialSecurityNumber = ssn;
    }

    // Abstract method that subclasses must implement
    public abstract double income();

    // Override toString() method to display employee details
    @Override
    public String toString() {
        return firstName + " " + lastName + ", SSN: " + socialSecurityNumber;
    }
}
