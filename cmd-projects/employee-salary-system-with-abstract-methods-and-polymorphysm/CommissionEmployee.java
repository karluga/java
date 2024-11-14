
class CommissionEmployee extends Employee {
    private double totalSales;
    private double commissionRate;

    public CommissionEmployee(String firstName, String lastName, int socialSecurityNumber, double totalSales, double commissionRate) {
        super(firstName, lastName, socialSecurityNumber);
        this.totalSales = totalSales;
        this.commissionRate = commissionRate;
    }

    @Override
    public double income() {
        return totalSales * commissionRate;
    }
}