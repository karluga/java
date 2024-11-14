

class BaseCommissionEmployee extends Employee {
    private double baseSalary;
    private double totalSales;
    private double commissionRate;
    private double bonus;

    public BaseCommissionEmployee(String firstName, String lastName, int socialSecurityNumber, double baseSalary, double totalSales, double commissionRate, double bonus) {
        super(firstName, lastName, socialSecurityNumber);
        this.baseSalary = baseSalary;
        this.totalSales = totalSales;
        this.commissionRate = commissionRate;
        this.bonus = bonus;
    }

    @Override
    public double income() {
        return baseSalary + (totalSales * commissionRate) + bonus;
    }
}

