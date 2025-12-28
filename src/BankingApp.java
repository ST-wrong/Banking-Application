import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Transaction {
    private String type;
    private double amount;
    private LocalDateTime timestamp;
    private String description;

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%-12s | $%-10.2f | %s | %s",
                type, amount, timestamp.format(formatter), description);
    }
}

class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private List<Transaction> transactions;

    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();

        if (initialBalance > 0) {
            transactions.add(new Transaction("DEPOSIT", initialBalance, "Initial deposit"));
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }

    public boolean deposit(double amount, String description) {
        if (amount <= 0) {
            System.out.println("❌ Deposit amount must be positive!");
            return false;
        }

        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, description));
        System.out.println("✓ Successfully deposited $" + String.format("%.2f", amount));
        return true;
    }

    public boolean withdraw(double amount, String description) {
        if (amount <= 0) {
            System.out.println("❌ Withdrawal amount must be positive!");
            return false;
        }

        if (amount > balance) {
            System.out.println("❌ Insufficient funds! Current balance: $" + String.format("%.2f", balance));
            return false;
        }

        balance -= amount;
        transactions.add(new Transaction("WITHDRAWAL", amount, description));
        System.out.println("✓ Successfully withdrew $" + String.format("%.2f", amount));
        return true;
    }

    public boolean transfer(BankAccount recipient, double amount) {
        if (amount <= 0) {
            System.out.println("❌ Transfer amount must be positive!");
            return false;
        }

        if (amount > balance) {
            System.out.println("❌ Insufficient funds for transfer!");
            return false;
        }

        balance -= amount;
        recipient.balance += amount;

        transactions.add(new Transaction("TRANSFER OUT", amount, "To: " + recipient.getAccountHolder()));
        recipient.transactions.add(new Transaction("TRANSFER IN", amount, "From: " + this.accountHolder));

        System.out.println("✓ Successfully transferred $" + String.format("%.2f", amount) + " to " + recipient.getAccountHolder());
        return true;
    }

    public void displayAccountInfo() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║          ACCOUNT INFORMATION                   ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║ Account Number: " + accountNumber + "                       ║");
        System.out.println("║ Account Holder: " + String.format("%-30s", accountHolder) + "║");
        System.out.println("║ Current Balance: $" + String.format("%-28.2f", balance) + "║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
    }

    public void displayTransactionHistory() {
        System.out.println("\n════════════════════════ TRANSACTION HISTORY ════════════════════════");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Type         | Amount      | Date & Time         | Description");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }

        System.out.println("═════════════════════════════════════════════════════════════════════\n");
    }
}

public class BankingApp {
    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, BankAccount> accounts = new HashMap<>();
    private static BankAccount currentAccount = null;

    public static void main(String[] args) {
        // Create some demo accounts
        accounts.put("12345", new BankAccount("12345", "John Doe", 5000.00));
        accounts.put("67890", new BankAccount("67890", "Jane Smith", 3500.00));

        displayWelcome();

        boolean running = true;
        while (running) {
            if (currentAccount == null) {
                running = loginMenu();
            } else {
                running = mainMenu();
            }
        }

        System.out.println("\n✓ Thank you for banking with us. Goodbye!\n");
        scanner.close();
    }

    private static void displayWelcome() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                                                      ║");
        System.out.println("║         ★  MERIDIAN BANKING SYSTEM  ★               ║");
        System.out.println("║                                                      ║");
        System.out.println("║              Your Trust, Our Priority                ║");
        System.out.println("║                                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }

    private static boolean loginMenu() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("         LOGIN MENU");
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. Login to Account");
        System.out.println("2. Create New Account");
        System.out.println("3. Exit");
        System.out.println("═══════════════════════════════════════");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                createAccount();
                break;
            case 3:
                return false;
            default:
                System.out.println("❌ Invalid option. Please try again.");
        }

        return true;
    }

    private static void login() {
        System.out.print("\nEnter account number: ");
        String accountNumber = scanner.nextLine();

        if (accounts.containsKey(accountNumber)) {
            currentAccount = accounts.get(accountNumber);
            System.out.println("\n✓ Login successful! Welcome back, " + currentAccount.getAccountHolder() + "!\n");
        } else {
            System.out.println("❌ Account not found. Please try again.\n");
        }
    }

    private static void createAccount() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter initial deposit amount: $");
        double initialDeposit = getDoubleInput();

        if (initialDeposit < 0) {
            System.out.println("❌ Initial deposit cannot be negative.\n");
            return;
        }

        String accountNumber = String.format("%05d", new Random().nextInt(100000));

        BankAccount newAccount = new BankAccount(accountNumber, name, initialDeposit);
        accounts.put(accountNumber, newAccount);

        System.out.println("\n✓ Account created successfully!");
        System.out.println("Your account number is: " + accountNumber);
        System.out.println("Please remember this for future logins.\n");
    }

    private static boolean mainMenu() {
        currentAccount.displayAccountInfo();

        System.out.println("═══════════════════════════════════════");
        System.out.println("         MAIN MENU");
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. View Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Transaction History");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
        System.out.println("═══════════════════════════════════════");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                viewBalance();
                break;
            case 2:
                deposit();
                break;
            case 3:
                withdraw();
                break;
            case 4:
                transfer();
                break;
            case 5:
                currentAccount.displayTransactionHistory();
                break;
            case 6:
                logout();
                break;
            case 7:
                return false;
            default:
                System.out.println("❌ Invalid option. Please try again.\n");
        }

        return true;
    }

    private static void viewBalance() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        System.out.println("═══════════════════════════════════════\n");
    }

    private static void deposit() {
        System.out.print("\nEnter deposit amount: $");
        double amount = getDoubleInput();

        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "Deposit";
        }

        currentAccount.deposit(amount, description);
        System.out.println("New balance: $" + String.format("%.2f", currentAccount.getBalance()) + "\n");
    }

    private static void withdraw() {
        System.out.print("\nEnter withdrawal amount: $");
        double amount = getDoubleInput();

        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "Withdrawal";
        }

        if (currentAccount.withdraw(amount, description)) {
            System.out.println("New balance: $" + String.format("%.2f", currentAccount.getBalance()) + "\n");
        } else {
            System.out.println();
        }
    }

    private static void transfer() {
        System.out.print("\nEnter recipient account number: ");
        String recipientNumber = scanner.nextLine();

        if (!accounts.containsKey(recipientNumber)) {
            System.out.println("❌ Recipient account not found.\n");
            return;
        }

        if (recipientNumber.equals(currentAccount.getAccountNumber())) {
            System.out.println("❌ Cannot transfer to the same account.\n");
            return;
        }

        BankAccount recipient = accounts.get(recipientNumber);

        System.out.print("Enter transfer amount: $");
        double amount = getDoubleInput();

        if (currentAccount.transfer(recipient, amount)) {
            System.out.println("New balance: $" + String.format("%.2f", currentAccount.getBalance()) + "\n");
        } else {
            System.out.println();
        }
    }

    private static void logout() {
        System.out.println("\n✓ Logged out successfully.\n");
        currentAccount = null;
    }

    private static int getIntInput() {
        try {
            int value = Integer.parseInt(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double getDoubleInput() {
        try {
            double value = Double.parseDouble(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}