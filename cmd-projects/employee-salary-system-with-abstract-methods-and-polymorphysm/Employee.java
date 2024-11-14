
abstract class Employee {
    protected String firstName;
    protected String lastName;
    protected int socialSecurityNumber;

    public Employee(String firstName, String lastName, int socialSecurityNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public abstract double income();

    @Override
    public String toString() {
        return firstName + " " + lastName + ", SSN: " + socialSecurityNumber;
    }
}
