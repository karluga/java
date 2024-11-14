

class PartTimeEmployee extends Employee {
    private double hoursWorked;
    private double hourlyRate;

    public PartTimeEmployee(String firstName, String lastName, int socialSecurityNumber, double hoursWorked, double hourlyRate) {
        super(firstName, lastName, socialSecurityNumber);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double income() {
        return hoursWorked * hourlyRate;
    }
}
