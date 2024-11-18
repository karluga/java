class FullTimeEmployee extends Employee {
    private double weeklySalary;

    public FullTimeEmployee(String firstName, String lastName, int ssn, double weeklySalary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = ssn;
        this.weeklySalary = weeklySalary;
    }

    // Setters
    public void setWeeklySalary(double weeklySalary) {
        this.weeklySalary = weeklySalary;
    }

    @Override
    public double income() {
        return weeklySalary;
    }
}

