class CommissionEmployee extends Employee {
    private double totalSales;
    private double commissionRate;

    public CommissionEmployee(String firstName, String lastName, int ssn, double totalSales, double commissionRate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = ssn;
        this.totalSales = totalSales;
        this.commissionRate = commissionRate;
    }

    // Setters
    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    @Override
    public double income() {
        return totalSales * commissionRate;
    }
}
