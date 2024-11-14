class BaseCommissionEmployee extends Employee {
    private double baseSalary;
    private double totalSales;
    private double commissionRate;
    private static final double BONUS_RATE = 0.10; // Fixed 10 percent

    public BaseCommissionEmployee(String firstName, String lastName, int socialSecurityNumber, double baseSalary, double totalSales, double commissionRate) {
        super(firstName, lastName, socialSecurityNumber);
        this.baseSalary = baseSalary;
        this.totalSales = totalSales;
        this.commissionRate = commissionRate;
    }
    
    @Override
    public double income() {
        return baseSalary + (totalSales * commissionRate) + (BONUS_RATE * baseSalary);
    }    
}
