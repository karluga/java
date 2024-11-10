import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;

public class Customer {
    private String accountNumber;
    private double balance;

    public Customer(String accountNumber, double initialBalance) {
        setAccountNumber(accountNumber);
        setBalance(formatAmount(initialBalance));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        // Define the pattern for Latvian IBAN format
        String ibanPattern = "^LV\\d{2}[A-Z0-9]{4}\\d{13}$";
        Pattern pattern = Pattern.compile(ibanPattern);
        Matcher matcher = pattern.matcher(accountNumber);

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid account number format. Latvian IBAN must follow: 'LV' + 2 digits + 4 bank code characters + 13 digits.");
        }

        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    private void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.balance = balance;
    }

    public void deposit(double amount) {
        amount = formatAmount(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        amount = formatAmount(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        balance -= amount;
    }

    public void transfer(Customer receiver, double amount) {
        amount = formatAmount(amount);
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver account does not exist.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance for transfer.");
        }
        this.withdraw(amount);
        receiver.deposit(amount);
    }
    private double formatAmount(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setDecimalSeparatorAlwaysShown(true); // Ensure two decimal places are shown
        String formattedAmount = df.format(amount).replace(".", ","); // Use comma as decimal separator
        return Double.parseDouble(formattedAmount.replace(",", ".")); // Convert back for internal use
    }
}
