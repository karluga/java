

class FullTimeEmployee extends Employee {
    private double weeklySalary;

    public FullTimeEmployee(String firstName, String lastName, int socialSecurityNumber, double weeklySalary) {
        super(firstName, lastName, socialSecurityNumber);
        this.weeklySalary = weeklySalary;
    }

    @Override
    public double income() {
        return weeklySalary;
    }
}
