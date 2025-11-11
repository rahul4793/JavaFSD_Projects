package banksystemsimulator;

import banksystemsimulator.exceptions.BankingExceptions;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BankingSystemSimulator {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    private String generateAccountNumber(String name) {
        String initials = Arrays.stream(name.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
        int randomNum = new Random().nextInt(9000) + 1000;
        return initials + randomNum;
    }

    private void createAccount() {
        try {
            System.out.print("Enter account holder's name: ");
            scanner.nextLine(); // consume newline before reading name
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                throw new BankingExceptions.InvalidNameException("Name cannot be empty.");
            }
            String accNumber;
            do {
                accNumber = generateAccountNumber(name);
            } while (accounts.containsKey(accNumber));
            Account account = new Account(name, accNumber);
            accounts.put(accNumber, account);

            System.out.println("Account created successfully!");
            System.out.println(account);

        } catch (BankingExceptions.InvalidNameException ine) {
            System.out.println("Error: " + ine.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during account creation: " + e.getMessage());
        }
    }

    private Account getAccountByNumber(String prompt) throws BankingExceptions.AccountNotFoundException {
        System.out.print(prompt);
        String accNum = scanner.nextLine().trim();
        Account account = accounts.get(accNum);
        if (account == null) {
            throw new BankingExceptions.AccountNotFoundException("Account with number " + accNum + " not found.");
        }
        return account;
    }

    private void deposit(Account account) {
        try {
            System.out.print("Enter deposit amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            account.deposit(amount);
            System.out.println("Deposit successful.");
            System.out.println(account);
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input. Please enter a numeric amount.");
            scanner.nextLine();
        } catch (BankingExceptions.InvalidAmountException iae) {
            System.out.println("Error: " + iae.getMessage());
        }
    }

    private void withdraw(Account account) {
        try {
            System.out.print("Enter withdrawal amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            account.withdraw(amount);
            System.out.println("Withdrawal successful.");
            System.out.println(account);
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input. Please enter a numeric amount.");
            scanner.nextLine();
        } catch (BankingExceptions.InvalidAmountException | BankingExceptions.InsufficientBalanceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transfer() {
        try {
            Account fromAccount = getAccountByNumber("Enter source account number: ");
            Account toAccount = getAccountByNumber("Enter destination account number: ");
            System.out.print("Enter transfer amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            Account.transfer(fromAccount, toAccount, amount);

            System.out.println("Transfer successful.");
            System.out.println("Source Account: " + fromAccount);
            System.out.println("Destination Account: " + toAccount);

        } catch (BankingExceptions.AccountNotFoundException | BankingExceptions.InvalidAmountException | BankingExceptions.InsufficientBalanceException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input. Please enter a numeric amount.");
            scanner.nextLine();
        }
    }

    private void showBalance() {
        try {
            Account account = getAccountByNumber("Enter account number to display balance: ");
            System.out.println(account);
        } catch (BankingExceptions.AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void accountOperations(Account account) {
        while (true) {
            try {
                System.out.println("\nOperations for Account: " + account.getAccountNumber());
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Transfer");
                System.out.println("4. Show Balance");
                System.out.println("5. Return to Main Menu");
                System.out.print("Select an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        deposit(account);
                        break;
                    case 2:
                        withdraw(account);
                        break;
                    case 3:
                        transfer();
                        break;
                    case 4:
                        System.out.println(account);
                        break;
                    case 5:
                        return;
                    default:
                        throw new BankingExceptions.InvalidMenuChoiceException("Invalid menu option.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            } catch (BankingExceptions.InvalidMenuChoiceException imce) {
                System.out.println("Error: " + imce.getMessage());
            }
        }
    }

    public void mainMenu() {
        while (true) {
            try {
                System.out.println("\nMain Menu:");
                System.out.println("1. Create Account");
                System.out.println("2. Operate on Existing Account");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        try {
                            Account account = getAccountByNumber("Enter account number: ");
                            accountOperations(account);
                        } catch (BankingExceptions.AccountNotFoundException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.println("Thank you for using the Banking System Simulator. Goodbye!");
                        return;
                    default:
                        throw new BankingExceptions.InvalidMenuChoiceException("Please select a valid option from the menu.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            } catch (BankingExceptions.InvalidMenuChoiceException imce) {
                System.out.println("Error: " + imce.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        BankingSystemSimulator simulator = new BankingSystemSimulator();
        System.out.println("Welcome to the Banking System Simulator!");
        simulator.mainMenu();
    }
}
